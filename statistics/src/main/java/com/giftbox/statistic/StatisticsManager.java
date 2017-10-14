package com.giftbox.statistic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;

import com.giftbox.statistic.beans.PostBean;
import com.giftbox.statistic.connect.BaseConnectHandle;
import com.giftbox.statistic.connect.PostFactory;
import com.giftbox.statistic.database.DataBaseProvider;
import com.giftbox.statistic.utiltool.DrawUtils;
import com.giftbox.statistic.utiltool.Machine;
import com.giftbox.statistic.utiltool.UtilTool;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 
 * <br>
 * 类描述:统计管理 <br>
 * 功能详细描述:
 */
public class StatisticsManager {

	private static final String SDK_VER = "1.0";

	public static final String TAG = "StatisticsManager";

	protected static final long ONE_MINUTES = 1 * 60 * 1000;

	public static final int BASIC_FUN_ID = 19;

	public static final int BASIC_OPTION_FUN_ID = 17;
	public static final int CHANNEL_CONTROL_FUN_ID = 530001;

	protected static final String STATISTICS_DATA_SEPARATE_STRING = "||";
	public static boolean sDebugMode = false;

	private static StatisticsManager mSelf;
	private Context mContext;
	private volatile boolean mQuit = true;
	private PostQueue mQueue;
	private DataBaseProvider mDBProvider;
	private Looper mLooper;
	private Object mMutex;
	private long mLastImportFromDB = 0;

	/** 2014.8 */
	/** 访问开关控制记录器 */
	public static final String CTRL_SP_NAME = "ctrl_sp_";
	public static final String USER_FIRST_RUN_TIME = "first_run_time";
	public static final long NEW_USER_VALID_TIME = 32 * 60 * 60 * 1000;
	/** 此broadcast用于主进程从控制信息获取成功后通知子进程更新内存中的控制开关 */
	private static final String BROADCAST_GETCTRLINFO = "com.android.broadcast.ctrlinfo";
	/** 此broadcast用于子进程被调用了upload××接口后，需要通知主进程立即上传 */
	private static final String BROADCAST_UPLOADDATA = "com.android.broadcast.uploaddata";
	private static final String BROADCAST_INTENT_ID = "id";
	private static final String BROADCAST_INTENT_PKGNAME = "pkg_name";
	/**
	 * 内存中的控制开关信息
	 */
	private ExecutorService mExecutor;
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mEditor;

	public static String sAndroidId = null;
	public static String sCountry = null;
	public static int sVersionCode = 0;
	public static int sOSVersionCode = 0;
	public static String sChannel = null;
	public static String sVersionName = null;
	public static String sIMEI = null;
	public static String sGADID = null;
	public final static int URL_RQUEST_FUNID = 1030;
	public PostBean bean;
	public String mUrl;
	public String mDebugUr;

	/**
	 * 是否为新用户
	 */
	public static boolean sIsNew = true;
	private OnInsertDBListener mInsertDBListener;

	private StatisticsManager(Context context) {
		Log.i("dyf", "StatisticsManager init");
		if (context == null) {
			throw new NullPointerException("context can not be null");
		}
		mContext = context;

		mSelf = this;
		DrawUtils.resetDensity(context);
		mQueue = new PostQueue();
		mDBProvider = new DataBaseProvider(mContext);

		mLooper = new Looper();
		mMutex = new Object();

		mSharedPreferences = mContext.getSharedPreferences(CTRL_SP_NAME
				+ mContext.getPackageName(), Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
		mExecutor = Executors.newSingleThreadExecutor();

		IntentFilter filter = new IntentFilter();
		filter.addAction(BROADCAST_GETCTRLINFO);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mContext.registerReceiver(mReceiver, filter);

	}

	/**
	 * 给线程池中加入队列
	 * 
	 * @param runnable
	 */
	private synchronized void addTaskToExecutor(Runnable runnable) {
		DBAsyncTask task = new DBAsyncTask();
		task.addTask(runnable);
		try {
			if (!mExecutor.isShutdown()) {
				mExecutor.execute(task);
			}
		} catch (Exception e) {
			UtilTool.printException(e);
		}
	}

	public static synchronized StatisticsManager getInstance(Context context) {
		if (mSelf == null && context != null) {
			Context appContext = context.getApplicationContext();
			if (appContext != null) {
				mSelf = new StatisticsManager(appContext);
			} else {
				mSelf = new StatisticsManager(context);
			}
		}
		return mSelf;
	}

	/**
	 * 重要！！需要在进程退出的时候调用
	 */
	public void destory() {
		UtilTool.logStatic("destroy sdk");
		mQuit = true;
		try {
			if (mContext != null) {
				mContext.unregisterReceiver(mReceiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (mDBProvider != null) {
				mDBProvider.destory();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mSelf = null;

	}

	/**
	 * 设置是否打印log
	 * 
	 * @param onOff
	 */
	public void enableLog(boolean onOff) {
		UtilTool.enableLog(onOff);
	}

	/**
	 * <br>
	 * 功能简述:测试模式，上传到测试服务器 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void setDebugMode() {
		sDebugMode = true;
		enableLog(true);
	}

	/**
	 * <br>
	 * 功能简述:测试模式，上传到测试服务器 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param
	 */
	public boolean getDebugMode() {
		return sDebugMode;
	}

	/**
	 * <br>
	 * 功能简述:启动任务队列 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	private synchronized void startTask(boolean isOld) {
		try {
			if (mContext != null) {
				if (Machine.getNetworkType(mContext) != Machine.NETWORKTYPE_INVALID) {
					if (mQuit) {
						mQuit = false;
						mLooper.loop();
						UtilTool.log(TAG, "start loop task");
					} else {
						UtilTool.log(TAG, "task already running");
					}
				} else {
					if (!isOld) {
						PostBean bean = mQueue.pop();
						while (bean != null) {
							mDBProvider.setDataOld(bean);
							bean = mQueue.pop();
						}
					} else {

					}
				}
			}

		} catch (Exception e) {
		}
	}

	/**
	 * <br>
	 * 功能简述:用于17和19以外的统计协议，sdk只负责传送 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public synchronized void upLoadStaticData(String staticData) {
		if (staticData != null) {
			upLoadStaticData(null, staticData,
					PostBean.DATAHANDLECODE_ENCODE_ZIP);
		}
	}

	/**
	 * <br>
	 * 功能简述: <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param url
	 *            指定url，
	 * @param staticData
	 *            传输的数据
	 * @param
	 *
	 */
	private synchronized void upLoadStaticData(final String url,
			final String staticData, final int dataOpCode) {
		addTaskToExecutor(new Runnable() {
			@Override
			public void run() {
				if (staticData != null) {
					bean = new PostBean();
					bean.mIsOld = true;
//					bean.mFunId = 19;// 这个值是随便写的，固定的，为了方便从数据库查询
					bean.mId = getUniqueID();
					bean.mData = staticData.substring(0,
							staticData.length() - 1);
//					Log.i("wss", "StatisticManager_upLoadStaticData = " + bean.mData);
					ayncStartTask(bean, true, null);
				}
			}
		});
	}

	private synchronized void ayncStartTask(final PostBean bean,
			final boolean isOld, final OnInsertDBListener insertDBListener) {
		if (insertDBListener != null) {
			insertDBListener.onBeforeInsertToDB();
		}
		mDBProvider.insertPostDataAsync(bean, new DBAsyncTask.AsyncCallBack() {
			@Override
			public void onFinish() {
				if (insertDBListener != null) {
					insertDBListener.onInsertToDBFinish();
				}
				pushTOQueue(bean);
				// 上传
				startTask(isOld);
			}

		});
	}

	private synchronized String getUniqueID() {
		long id = Math.abs(System.nanoTime());
		String ID = String.valueOf(id);
		return ID;
	}

	/**
	 * <br>
	 * 功能简述:网络发送接口 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param bean
	 */
	private synchronized void postData(PostBean bean) {
		if (bean != null && mContext != null) {
			BaseConnectHandle handle = PostFactory.produceHandle(mContext,
					bean.mFunId);
			handle.postData(bean);
			if (bean.mState == PostBean.STATE_POSTED) {
				UtilTool.log(TAG, "a request has been posted");
			} else {
				UtilTool.log(TAG, "post fundid:" + bean.mFunId + " failed!");
			}
		}
	}

	private synchronized void quitPost() {
		// saveDataTODB();
		if (mQuit) {
			mQueue.clear();
		}

	}

	/**
	 * 
	 * <br>
	 * 类描述:数据上传 <br>
	 * 功能详细描述:
	 */
	
	private class Looper {
		public synchronized void loop() {
			Thread thread = new Thread() {
				@Override
				public void run() {
					android.os.Process
							.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND
									+ android.os.Process.THREAD_PRIORITY_LESS_FAVORABLE);
					HashSet<String> funidList = new HashSet<String>();
					Log.i("TongJiTest", "upload loop start");
					while (true) {
						try {
							if (mQuit) {
								lastUpdateTime = System.currentTimeMillis();
								UtilTool.log(TAG, "quit post!");
								quitPost();
								Log.i("TongJiTest", "quit loop");
								return;
							}
							PostBean bean = mQueue.pop();
							if(bean!=null){
								Log.i("dyf", "loop:"+bean.mData);
							}
							// 隊列空,查看buffer是否有,buffer也无的话退出
							if (bean == null) {
								if (getPostDataFromDBTask(funidList)) {
									UtilTool.log(TAG, "now push data from DB!");
								} else {
									UtilTool.log(TAG, "no data quit!");
									mQuit = true;
								}
								continue;
							}
							funidList.add(String.valueOf(bean.mFunId));

							if (bean.mReTryCount < PostBean.MAX_RETRY_COUNT) {
								postData(bean);
							}

							if (bean.mState == PostBean.STATE_POSTED) {
								mDBProvider.deletePushData(bean);
								Log.i("TongJiTest", "upload success");
							} else {
								bean.mReTryCount++;
								Log.i("TongJiTest",
										"upload failed and retryTime:"
												+ bean.mReTryCount);
								if (bean.mReTryCount < PostBean.MAX_RETRY_COUNT) {
									mQueue.push(bean);
								} else {
									bean.mIsOld = true;
									mDBProvider.setDataOld(bean);
									mQuit = true;
									UtilTool.logStatic("quit loop");
									Log.i("TongJiTest",
											"upload failed and exit upload loop");
									break;
								}
							}
						} catch (Exception e) {
							UtilTool.printException(e);
						}
					}

				}

			};
			thread.start();
		}

	}

	private void checkPostTask() {
		if (getOldDataFromDB()) {
			startTask(true);
		}
	}

	/**
	 * <br>
	 * 功能简述:检查db中是否有未成功发送的数据 <br>
	 * 功能详细描述: <br>
	 * 注意:上次检查如果与本次时间差大于10s则检查，否则跳过
	 */
	private synchronized boolean getPostDataFromDBTask(HashSet<String> funid) {
		synchronized (mMutex) {
			LinkedList<PostBean> list = mDBProvider.queryPostDatas(funid);
			if (list.isEmpty()) {
				list.addAll(mDBProvider.queryOldData());
			}
			if (list != null && !list.isEmpty()) {
				for (PostBean postBean : list) {
					if (postBean.mNetwork <= Machine.getNetworkType(mContext)) {
						mQueue.push(postBean);
					}
				}
				return true;
			}
			return false;
		}
	}

	/**
	 * 功能简述:检查db中是否有未成功发送的数据
	 * 
	 * @return
	 */
	private synchronized boolean getOldDataFromDB() {
		synchronized (mMutex) {
			long now = System.currentTimeMillis();
			if (now - mLastImportFromDB > 10000) {
				mLastImportFromDB = now;
				LinkedList<PostBean> list = mDBProvider.queryOldData();
				if (list != null && !list.isEmpty()) {
					for (PostBean postBean : list) {
						if (postBean.mNetwork <= Machine
								.getNetworkType(mContext)) {
							mQueue.push(postBean);
						}
					}
					return true;
				}
			}
			return false;
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				if (Machine.getNetworkType(context) != Machine.NETWORKTYPE_INVALID) {
					UtilTool.log(TAG, "net connection ok , check post queue!");
					addTaskToExecutor(new Runnable() {
						public void run() {
							checkPostTask(); // 查询需要上传但未上传成功的数据
						}
					});
				} else {
					UtilTool.log(TAG, "lost network,quit!");
					mQuit = true;
				}
				return;
			} else if (intent.getStringExtra(BROADCAST_INTENT_PKGNAME) != null
					&& intent.getStringExtra(BROADCAST_INTENT_PKGNAME).equals(
							mContext.getPackageName())) {
				if (action.equals(BROADCAST_GETCTRLINFO)) {
				} else if (action.equals(BROADCAST_UPLOADDATA)) {
					PostBean bean = mDBProvider.queryPostData(intent
							.getStringExtra(BROADCAST_INTENT_ID));
					if (bean != null) {
						pushTOQueue(bean);
						startTask(true);
					}
				}
			}
		}
	};

	public void uploadAllData() {
		synchronized (mMutex) {
			long now = System.currentTimeMillis();
			if (now - mLastImportFromDB > 10000) {
				mLastImportFromDB = now;
				LinkedList<PostBean> list = mDBProvider.queryAllData();
				if (list != null && !list.isEmpty()) {
					for (PostBean postBean : list) {
						if (postBean.mNetwork <= Machine
								.getNetworkType(mContext)) {
							mQueue.push(postBean);
						}
					}
					startTask(true);
				}
			}
		}
	}

	private void pushTOQueue(PostBean bean) {
		if (bean.mNetwork <= Machine.getNetworkType(mContext)) {
			mQueue.push(bean);
		} else {
			mDBProvider.setDataOld(bean);
		}
	}

	public String getVerionName() {
		return SDK_VER;
	}

	public void restoreDefault() {
		mEditor.clear();
		mEditor.commit();
	}

	/**
	 * 察看在统计sdk中用户是否是最新用户
	 * 
	 * @return
	 */
	public boolean userIsNew() {
		return sIsNew;
	}

	@SuppressWarnings("unused")
	private long lastUpdateTime = 0;

	@SuppressWarnings("unused")
	private boolean stopUpload = false;

	public void setStop(boolean stop) {
		stopUpload = stop;
	}

	public OnInsertDBListener getInsertDBListener() {
		return mInsertDBListener;
	}

	public void setInsertDBListener(OnInsertDBListener mInsertDBListener) {
		this.mInsertDBListener = mInsertDBListener;
	}

	/**
	 * 
	 * @param url
	 *            正式服务器url
	 * @param debugUrl
	 *            测试服务器url
	 */
	public void setUrl(String url, String debugUrl) {
		this.mUrl = url;
		this.mDebugUr = debugUrl;
	}
}
