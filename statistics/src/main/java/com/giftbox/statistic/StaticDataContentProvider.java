package com.giftbox.statistic;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.giftbox.statistic.database.DataBaseHelper;


//CHECKSTYLE:OFF

public class StaticDataContentProvider extends ContentProvider {
	private static final long MIN_GET_TIME = 100;
	private volatile long mLastGetTime = 0;
	private static final String AUTHORITIE = "com.giftbox.statistic.staticprovider";

	private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static final String DATA_PATH = "data_new";
	private static final String CTRLINFO_PATH = "ctrlinfo";
	private static final String OLD_DATA_PATH = "data";

	private static final int CODE_DATA = 1;
	private static final int INFO_DATA = 2;
	private static final int OLD_DATA = 3;

	public static Uri STATIC_NEW_URL = new Uri.Builder()
			.scheme(ContentResolver.SCHEME_CONTENT)
			.authority(AUTHORITIE).appendPath(DATA_PATH).build();
	public static Uri STATIC_URL = new Uri.Builder()
			.scheme(ContentResolver.SCHEME_CONTENT)
			.authority(AUTHORITIE).appendPath(OLD_DATA_PATH).build();
	public static Uri CTRL_INFO_URL = new Uri.Builder()
			.scheme(ContentResolver.SCHEME_CONTENT)
			.authority(AUTHORITIE).appendPath(CTRLINFO_PATH).build();

	private byte[] mMutex = new byte[0];
	private DataBaseHelper mHelper;
	public static boolean isNew = true;
	static {
		sUriMatcher.addURI(AUTHORITIE, DATA_PATH, CODE_DATA);
		sUriMatcher.addURI(AUTHORITIE, CTRLINFO_PATH, INFO_DATA);
		sUriMatcher.addURI(AUTHORITIE, OLD_DATA_PATH, OLD_DATA);
	}

	@Override
	public boolean onCreate() {
		mHelper = new DataBaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor = null;
		String table = null;
		synchronized (mMutex) {
			if (mLastGetTime != 0
					 && System.currentTimeMillis() - mLastGetTime < MIN_GET_TIME) { //爲了解決兩個進程同時取數據的暫時方案
					 return cursor;
			}
			switch (sUriMatcher.match(uri)) {
			case CODE_DATA:
				table = DataBaseHelper.TABLE_STATISTICS_NEW;
				break;
			case INFO_DATA:
				table = DataBaseHelper.TABLE_CTRLINFO;
				break;
			case OLD_DATA:
				table = DataBaseHelper.TABLE_STATISTICS;
				break;
			}
			if (table != null) {
				try {
					cursor = mHelper.query(table, projection, selection,
							selectionArgs, sortOrder);
					if (cursor != null && cursor.getCount() > 0) {
						mLastGetTime = System.currentTimeMillis();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return cursor;
		}
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String table = null;
		switch (sUriMatcher.match(uri)) {
		case CODE_DATA:
			table = DataBaseHelper.TABLE_STATISTICS_NEW;
			break;
		case INFO_DATA:
			table = DataBaseHelper.TABLE_CTRLINFO;
			break;
		}
		if (table != null) {
			try {
				long count = mHelper.insert(table, values);
				if (count > 0) {
					return uri;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String table = null;
		switch (sUriMatcher.match(uri)) {
		case CODE_DATA:
			table = DataBaseHelper.TABLE_STATISTICS_NEW;
			break;
		case INFO_DATA:
			table = DataBaseHelper.TABLE_CTRLINFO;
			break;
		case OLD_DATA:
			table = DataBaseHelper.TABLE_STATISTICS;
			break;
		}
		if (table != null) {
			try {
				return mHelper.delete(table, selection, selectionArgs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		String table = null;
		switch (sUriMatcher.match(uri)) {
		case CODE_DATA:
			table = DataBaseHelper.TABLE_STATISTICS_NEW;
			break;
		case INFO_DATA:
			table = DataBaseHelper.TABLE_CTRLINFO;
			// break;
		}
		if (table != null) {
			try {
				return mHelper.update(table, values, selection, selectionArgs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

}
