package com.giftbox.statistic.beans;

import android.content.ContentValues;
import android.database.Cursor;

import com.giftbox.statistic.database.DataBaseHelper;


/**
 * 
 * <br>类描述: 统计数据bean
 * <br>功能详细描述:
 */
public class PostBean extends CommonBean{

	public static final int STATE_WAITTING = 0;
	public static final int STATE_POSTING = STATE_WAITTING + 1;
	public static final int STATE_POSTFAILED = STATE_POSTING + 1;
	public static final int STATE_POSTED = STATE_POSTFAILED + 1;

	public static final int MAX_RETRY_COUNT = 3;

	public static final int DATAHANDLECODE_NONE = 0;
	public static final int DATAHANDLECODE_ZIP = 1;
	public static final int DATAHANDLECODE_ENCODE = 2;
	public static final int DATAHANDLECODE_ENCODE_ZIP = 3;

	public int mProtocolNumber;//协议号
	public int mFunId;//功能点Id
	public int mCpu;//cpu
	public long mRam;//内存
	public String mRom;//手机容量
	public int mNetwork = 0;//网络类型
	public String mScreenSize;//分辨率
	public int is_tablet;//是否平板 ，0：不是，1，是
	public String mOperator;//运营商
	public String mTimeZone;//时区
	public String mImei;//IMEI号
	public String mImsi;//
	public String mId;//存到数据库的id
	public int mState = 0;
	public int mReTryCount = 0;
	public String mData;
	private boolean mFromDB = false;
	public String mUrl; //指定服务器url
	public String mDebugUrl;//测试服务器
	public int mDataOption = PostBean.DATAHANDLECODE_ENCODE_ZIP; // 上传时对数据的处理 0不做任何处理，1 压缩，2 加密，3,压缩加密
	
	public boolean mNeedRootInfo;
	public boolean mIsNew = false;
	public String mKey = "-1";
	public  PostBean mNext;
	public String bn;
	public boolean mIsOld = false;
	public void parse(Cursor cursor) {
		if (cursor != null) {
			int index = cursor
					.getColumnIndex(DataBaseHelper.TABLE_STATISTICS_COLOUM_FUNID);
			if (index != -1) {
				mFunId = cursor.getInt(index);
			}
			index = cursor
					.getColumnIndex(DataBaseHelper.TABLE_STATISTICS_COLOUM_ID);
			if (index != -1) {
				mId = cursor.getString(index);
			}
			
			index = cursor
					.getColumnIndex(DataBaseHelper.TABLE_CTRLINFO_COLOUM_NETWORK);
			
			if (index != -1) {
				mNetwork = cursor.getInt(index);
			}
			index = cursor
					.getColumnIndex(DataBaseHelper.TABLE_STATISTICS_COLOUM_DATA);
			if(index != -1){
				mData = cursor.getString(index);
			}

			index = cursor
					.getColumnIndex(DataBaseHelper.TABLE_STATISTICS_COLOUM_OPCODE);
			if(index != -1){
				mDataOption = cursor.getInt(index);
			}
			index = cursor
					.getColumnIndex(DataBaseHelper.TABLE_STATISTICS_COLOUM_TIME);
			if(index != -1){
				mTimeStamp = cursor.getString(index);
			}
			
			index = cursor.getColumnIndex(DataBaseHelper.TABLE_STATISTICS_COLOUM_ISOLD);
			if(index != -1) {
				mIsOld = cursor.getInt(index) == 1 ? true : false;
			}
			mFromDB = true;
		}
	}

	public void setFromDB(boolean value){
		mFromDB = value;
	}
	
	public boolean isFromDB(){
		return mFromDB;
	}
	
	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.TABLE_STATISTICS_COLOUM_FUNID, mFunId);
		values.put(DataBaseHelper.TABLE_STATISTICS_COLOUM_ISOLD, mIsOld);
		//values.put(DataBaseHelper.TABLE_STATISTICS_COLOUM_TIME, mTimeStamp);
		values.put(DataBaseHelper.TABLE_STATISTICS_COLOUM_ID, mId);
		values.put(DataBaseHelper.TABLE_STATISTICS_COLOUM_DATA, mData);
		//values.put(DataBaseHelper.TABLE_STATISTICS_COLOUM_OPCODE, mDataOption);
		//values.put(DataBaseHelper.TABLE_CTRLINFO_COLOUM_NETWORK, mNetwork);

		return values;
	}
}
