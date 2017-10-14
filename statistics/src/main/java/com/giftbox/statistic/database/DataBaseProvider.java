package com.giftbox.statistic.database;

//CHECKSTYLE:OFF

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.giftbox.statistic.DBAsyncTask;
import com.giftbox.statistic.StaticDataContentProvider;
import com.giftbox.statistic.beans.PostBean;
import com.giftbox.statistic.utiltool.UtilTool;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * <br>
 * 类描述:DB封装工具 <br>
 * 功能详细描述:
 */
public class DataBaseProvider {

	private Context mContext;
	private DataBaseHelper mHelp;
	private boolean mCanNotFindUrl = false;
	private ExecutorService mSingleExecutor;

	public DataBaseProvider(Context context) {
		mContext = context;
		mSingleExecutor = Executors.newSingleThreadExecutor();
	}

	private synchronized DataBaseHelper getDataHelper() {
		if (mHelp == null) {
			mHelp = new DataBaseHelper(mContext);
		}
		return mHelp;
	}

	public void insertPostDataAsync(final PostBean bean, DBAsyncTask.AsyncCallBack callBack) {
		DBAsyncTask task = new DBAsyncTask();
		task.addCallBack(callBack);
		task.addTask(new Runnable() {
			@Override
			public void run() {
				ContentResolver resolver = mContext.getContentResolver();
				Uri ret = null;
				try {
					ret = resolver.insert(StaticDataContentProvider.STATIC_NEW_URL, bean.getContentValues());
					if (ret != null) {
						bean.setFromDB(true);
					}
					UtilTool.logStatic("Insert static Data to DB:"
							+ bean.getContentValues().get(DataBaseHelper.TABLE_STATISTICS_COLOUM_DATA));
				} catch (Exception e) {
					try {
						mCanNotFindUrl = true;
						long id = getDataHelper().insert(DataBaseHelper.TABLE_STATISTICS_NEW, bean.getContentValues());
						if (id != -1) {
							bean.setFromDB(true);
						}
					} catch (Exception e1) {
						UtilTool.printException(e1);
					}
				}
			}
		});
		try {
			if (!mSingleExecutor.isShutdown()) {
				mSingleExecutor.execute(task);
			}
		} catch (Exception e) {
			UtilTool.printException(e);
		}
	}


	/**
	 * 查询老版本（Version < 1.10）SDK数据库中的数据
	 * 
	 * @return
	 */
	public LinkedList<PostBean> queryOldSDKVersionData() {
		Cursor cursor = null;
		LinkedList<PostBean> list = null;
		try {
			ContentResolver resolver = mContext.getContentResolver();
			cursor = resolver.query(StaticDataContentProvider.STATIC_URL, null, null, null, null);
			if (cursor != null) {
				list = new LinkedList<PostBean>();
				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					PostBean bean = new PostBean();
					bean.parse(cursor);
					list.add(bean);
				}

			} else if (mCanNotFindUrl && cursor == null) {
				throw new IllegalArgumentException("Unknown URL" + StaticDataContentProvider.STATIC_URL);
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}

	
	public LinkedList<PostBean> queryPostDatas(HashSet<String> funidList) {
		Cursor cursor = null;
		LinkedList<PostBean> list = new LinkedList<PostBean>();
		StringBuffer where = null;
		if (funidList != null && funidList.size() > 0) {
			where = new StringBuffer("funid IN (");
			for (String funid : funidList) {
				where.append(funid + ",");
			}
			where.deleteCharAt(where.length() - 1);
			where.append(")");
		}

		if (where == null) {
			return list;
		}
		try {
			ContentResolver resolver = mContext.getContentResolver();
			cursor = resolver.query(StaticDataContentProvider.STATIC_NEW_URL, null, where.toString(), null,
					DataBaseHelper.TABLE_STATISTICS_COLOUM_ID + " DESC limit " + QUERYLIMIT);
			if (cursor != null) {
				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					PostBean bean = new PostBean();
					bean.parse(cursor);
					list.add(bean);
				}
				UtilTool.logStatic("Query post data:" + where.toString() + ",data count:" + cursor.getCount());
			} else if (mCanNotFindUrl && cursor == null) {
				throw new IllegalArgumentException("Unknown URL" + StaticDataContentProvider.STATIC_NEW_URL);
			}
		} catch (Exception e) {
			cursor = getDataHelper().query(DataBaseHelper.TABLE_STATISTICS_NEW, null, where.toString(), null,
					DataBaseHelper.TABLE_STATISTICS_COLOUM_ID + " DESC limit " + QUERYLIMIT);
			try {
				if (cursor != null && cursor.getCount() > 0) {
					list = new LinkedList<PostBean>();
					cursor.moveToPosition(-1);
					while (cursor.moveToNext()) {
						PostBean bean = new PostBean();
						bean.parse(cursor);
						list.add(bean);
					}
				}
				UtilTool.printException(e);
			} catch (Exception e1) {
				UtilTool.printException(e1);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}

	private static final int QUERYLIMIT = 300;

	/**
	 * 查询数据库中未上传但是急需上传的数据（isold = 1）
	 * 
	 * @return
	 */
	public LinkedList<PostBean> queryOldData() {
		Cursor cursor = null;
		LinkedList<PostBean> list = new LinkedList<PostBean>();
		try {
			ContentResolver resolver = mContext.getContentResolver();
			cursor = resolver.query(StaticDataContentProvider.STATIC_NEW_URL, null, "isold=1", null,
					DataBaseHelper.TABLE_STATISTICS_COLOUM_ID + " DESC limit " + QUERYLIMIT);
			if (cursor != null) {
				UtilTool.logStatic("Query all old data, data count:" + cursor.getCount());
				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					PostBean bean = new PostBean();
					bean.parse(cursor);
					list.add(bean);
				}
			} else if (mCanNotFindUrl && cursor == null) {
				throw new IllegalArgumentException("Unknown URL" + StaticDataContentProvider.STATIC_NEW_URL);
			}
		} catch (Exception e) {
			cursor = getDataHelper().query(DataBaseHelper.TABLE_STATISTICS_NEW, null, "isold=1", null,
					DataBaseHelper.TABLE_STATISTICS_COLOUM_ID + " DESC limit " + QUERYLIMIT);
			try {
				if (cursor != null && cursor.getCount() > 0) {
					list = new LinkedList<PostBean>();
					cursor.moveToPosition(-1);
					while (cursor.moveToNext()) {
						PostBean bean = new PostBean();
						bean.parse(cursor);
						list.add(bean);
					}
				}
				UtilTool.printException(e);
			} catch (Exception e1) {
				UtilTool.printException(e1);
			}
			
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}

	public LinkedList<PostBean> queryAllData() {
		Cursor cursor = null;
		LinkedList<PostBean> list = new LinkedList<PostBean>();
		try {
			ContentResolver resolver = mContext.getContentResolver();
			cursor = resolver.query(StaticDataContentProvider.STATIC_NEW_URL, null, null, null,
					DataBaseHelper.TABLE_STATISTICS_COLOUM_ID + " DESC");
			if (cursor != null) {
				UtilTool.logStatic("Query all data in db, data count:" + cursor.getCount());
				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					PostBean bean = new PostBean();
					bean.parse(cursor);
					list.add(bean);
				}
			} else if (mCanNotFindUrl && cursor == null) {
				throw new IllegalArgumentException("Unknown URL" + StaticDataContentProvider.STATIC_NEW_URL);
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}

	public int queryDataCount() {
		Cursor cursor = null;
		int count = 0;
		try {
			ContentResolver resolver = mContext.getContentResolver();
			cursor = resolver.query(StaticDataContentProvider.STATIC_NEW_URL, null, null, null, null);
			if (cursor != null) {
				count = cursor.getCount();
			} else if (mCanNotFindUrl && cursor == null) {
				throw new IllegalArgumentException("Unknown URL" + StaticDataContentProvider.STATIC_NEW_URL);
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return count;
	}

	public LinkedList<PostBean> queryPostDatas(String funid) {
		Cursor cursor = null;
		LinkedList<PostBean> list = null;

		try {
			ContentResolver resolver = mContext.getContentResolver();
			cursor = resolver.query(StaticDataContentProvider.STATIC_NEW_URL, null, "funid IN (" + funid + ")", null,
					DataBaseHelper.TABLE_STATISTICS_COLOUM_ID + " DESC limit " + QUERYLIMIT);

			if (cursor == null) {
//				UtilTool.logStatic("cursor is null");
			}

			if (cursor != null) {
				UtilTool.logStatic("Query Post Data In funid:" + funid + " and data Count:" + cursor.getCount());
				list = new LinkedList<PostBean>();
				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					PostBean bean = new PostBean();
					bean.parse(cursor);
					list.add(bean);
//					UtilTool.logStatic("beanData:" + bean.mId);
				}

			} else if (mCanNotFindUrl && cursor == null) {
				throw new IllegalArgumentException("Unknown URL" + StaticDataContentProvider.STATIC_NEW_URL);
			}
		} catch (Exception e) {
			UtilTool.logStatic("contentProvider exception");

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return list;
	}

	public void deletePushData(PostBean bean) {
		StringBuilder builder = new StringBuilder();
		PostBean tmp = bean;
		builder.append("(");
		int beanCount = 0;
		while (tmp != null) {
			beanCount++;
			builder.append("'");
			builder.append(tmp.mId);
			builder.append("'");
			if (tmp.mNext != null) {
				builder.append(",");
			}
			tmp = tmp.mNext;
		}
		builder.append(")");
		String where = null;
		if (beanCount > 1) {
			/*where = "funid=" + bean.mFunId + " and " + DataBaseHelper.TABLE_STATISTICS_COLOUM_ID + " IN "
					+ builder.toString();*/
			where = DataBaseHelper.TABLE_STATISTICS_COLOUM_ID + " IN "
					+ builder.toString();
		} else {
			/*where = "funid=" + bean.mFunId + " and " + DataBaseHelper.TABLE_STATISTICS_COLOUM_ID + "='" + bean.mId
					+ "'";*/
			where = DataBaseHelper.TABLE_STATISTICS_COLOUM_ID + "='" + bean.mId
					+ "'";
		}
		try {
			ContentResolver resolver = mContext.getContentResolver();
			int count = resolver.delete(StaticDataContentProvider.STATIC_NEW_URL, where, null);
			UtilTool.log(null, "deletePushData from db count:" + count + ",where:" + where);
		} catch (Exception e) {
			try {
				getDataHelper().delete(DataBaseHelper.TABLE_STATISTICS_NEW, where, null);
			} catch (Exception e1) {
				UtilTool.printException(e1);
			}
		}
	}

	private synchronized void closeDB() {
		if (mHelp != null) {
			mHelp.close();
		}
	}

	public void destory() {
		try {
			mSingleExecutor.shutdown();
			closeDB();
		} catch (Exception e) {
			UtilTool.printException(e);
		}
	}

	public void setDataOld(PostBean bean) {
		PostBean tmp = bean.mNext;
		StringBuffer buffer = new StringBuffer();
		buffer.append("'" + bean.mId + "',");
		while (tmp != null) {
			buffer.append("'" + tmp.mId + "',");
			tmp = tmp.mNext;
		}
		String where = "";
		buffer.deleteCharAt(buffer.length() - 1);
		where = buffer.toString();

		ContentValues contentValues = new ContentValues();
		contentValues.put(DataBaseHelper.TABLE_STATISTICS_COLOUM_ISOLD, true);
		try {
			ContentResolver resolver = mContext.getContentResolver();
			int count = resolver.update(StaticDataContentProvider.STATIC_NEW_URL, contentValues, "id IN (" + where
					+ ")", null);
			UtilTool.log(null, "setDataOld in db count:" + count);
		} catch (Exception e) {
			try {
				getDataHelper().update(DataBaseHelper.TABLE_STATISTICS_NEW, contentValues, "id IN (" + where + ")",
						null);
			} catch (Exception e1) {
				UtilTool.printException(e1);
			}
		}
	}

	/**
	 * 将所有现有数据库中的数据的isold字段设置为true
	 */
	public int setAllDataOld() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DataBaseHelper.TABLE_STATISTICS_COLOUM_ISOLD, true);
		int count = 0;
		try {
			ContentResolver resolver = mContext.getContentResolver();
			count = resolver.update(StaticDataContentProvider.STATIC_NEW_URL, contentValues, "isold=0", null);
			UtilTool.logStatic("Set Data new to old,success count:" + count);
		} catch (Exception e) {
			try {
				count = getDataHelper().update(DataBaseHelper.TABLE_STATISTICS_NEW, contentValues, null, null);
			} catch (Exception e1) {
				UtilTool.printException(e1);
			}
		}
		return count;
	}

	public int deleteOldCtrlInfo() {
		int count = 0;
		try {
			ContentResolver resolver = mContext.getContentResolver();
			count = resolver.delete(StaticDataContentProvider.CTRL_INFO_URL, null, null);
			UtilTool.logStatic("Delete old ctrlInfo from db, ctrlInfo count:" + count);
		} catch (Exception e) {
			UtilTool.printException(e);
			try {
				count = getDataHelper().delete(DataBaseHelper.TABLE_CTRLINFO, null, null);
			} catch (Exception e1) {
				UtilTool.printException(e1);
			}
		}
		return count;
	}

	public PostBean queryPostData(String stringExtra) {
		Cursor cursor = null;
		PostBean bean = null;

		try {
			ContentResolver resolver = mContext.getContentResolver();

			cursor = resolver.query(StaticDataContentProvider.STATIC_NEW_URL, null, "id IN ('" + stringExtra + "')",
					null, null);
			if (cursor == null) {
				UtilTool.logStatic("cursor is null");
			}

			if (cursor != null && cursor.getCount() > 0) {
				UtilTool.logStatic("Query Post Data In id:" + stringExtra + " and data Count:" + cursor.getCount());
				cursor.moveToPosition(0);

				bean = new PostBean();
				bean.parse(cursor);
				UtilTool.logStatic("beanData:" + bean.mData);

			} else if (mCanNotFindUrl && cursor == null) {
				throw new IllegalArgumentException("Unknown URL" + StaticDataContentProvider.STATIC_NEW_URL);
			}
		} catch (Exception e) {
			UtilTool.logStatic("contentProvider exception");
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return bean;
	}

	public void deleteOldData(LinkedList<PostBean> bean) {
		StringBuffer where = new StringBuffer("id IN (");
		for (PostBean postBean : bean) {
			where.append("" + postBean.mId + ",");
		}
		where.deleteCharAt(where.length() - 1);
		where.append(")");
		try {
			ContentResolver resolver = mContext.getContentResolver();
			int count = resolver.delete(StaticDataContentProvider.STATIC_URL, where.toString(), null);
			UtilTool.log(null, "Delete old data from db and where: " + where.toString() + " and count:" + count);
		} catch (Exception e) {
			UtilTool.printException(e);
			try {
				getDataHelper().delete(DataBaseHelper.TABLE_STATISTICS, where.toString(), null);
			} catch (Exception e1) {
				UtilTool.printException(e1);
			}
		}
	}

}
