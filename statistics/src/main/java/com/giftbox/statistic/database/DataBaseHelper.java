package com.giftbox.statistic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.giftbox.statistic.StatisticsManager;
import com.giftbox.statistic.beans.PostBean;
import com.giftbox.statistic.utiltool.UtilTool;

import java.util.ArrayList;


/**
 * 
 * <br>
 * 类描述:统计数据post失败时持久化DB <br>
 * 功能详细描述:
 * 
 */
public class DataBaseHelper extends SQLiteOpenHelper {

	private final static int DB_VERSION_ONE = 1;
	public final static int CUR_VERSION = 6;
	public static final String DB_NAME = "batstatistics_sdk.db";

	public static String TABLE_STATISTICS_NEW = "statistics_new";
	public static String TABLE_CTRLINFO = "ctrl_info";
	public static String TABLE_STATISTICS = "statistics";

	private final static String TYPE_NUMERIC = "numeric";
	private final static String TYPE_TEXT = "text";

	public static String TABLE_STATISTICS_COLOUM_FUNID = "funid";
	public static String TABLE_STATISTICS_COLOUM_ID = "id";
	public static String TABLE_STATISTICS_COLOUM_CHANNEL = "channel";
	public static String TABLE_STATISTICS_COLOUM_DATA = "data";
	public static String TABLE_STATISTICS_COLOUM_URL = "url";
	public static String TABLE_STATISTICS_COLOUM_OPCODE = "opcode";
	@Deprecated
	public static String TABLE_STATISTICS_COLOUM_NROOTINFO = "nrootinfo";
	@Deprecated
	public static String TABLE_STATISTICS_COLOUM_ISNEW = "isnew";
	@Deprecated
	public static String TABLE_STATISTICS_COLOUM_KEY = "key";

	public static String TABLE_STATISTICS_COLOUM_TIME = "time";
	public static String TABLE_STATISTICS_COLOUM_ISOLD = "isold";

	public static String TABLE_CTRLINFO_COLOUM_FUNID = "funid";
	public static String TABLE_CTRLINFO_COLOUM_VALIDTIME = "duration";
	public static String TABLE_CTRLINFO_COLOUM_INTERVALTIME = "intervaltime";
	public static String TABLE_CTRLINFO_COLOUM_BN = "bn";
	public static String TABLE_CTRLINFO_COLOUM_NETWORK = "network";
	public static String TABLE_CTRLINFO_COLOUM_PRIORITY = "priority";

	private static String CREATE_DATA_TABLE_SQL_NEW = "create table IF NOT EXISTS " + TABLE_STATISTICS_NEW + "("
			+ "id text, " + "funid numeric, " + "data text, " + "time text, " + "opcode numeric," + "isold boolean, "
			+ "network numeric" + ")";

	private boolean mUpdateResult = true; // 更新数据库结果，默认是成功的。
	private Context mContext;

	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, CUR_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL(CREATE_DATA_TABLE_SQL_NEW);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}

	}

	/**
	 * 检查表中是否存在该字段
	 * 
	 * @param db
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	private boolean isExistColumnInTable(SQLiteDatabase db, String tableName, String columnName) {
		boolean result = false;
		Cursor cursor = null;
		try {
			// 查询列数
			String columns[] = { columnName };
			cursor = db.query(tableName, columns, null, null, null, null, null);
			if (cursor != null && cursor.getColumnIndex(columnName) >= 0) {
				result = true;
			}
		} catch (Exception e) {
			// e.printStackTrace();
			UtilTool.log(StatisticsManager.TAG, "isExistColumnInTable has exception");
			result = false;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
	}

	// @Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// super.onDowngrade(db, oldVersion, newVersion);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < DB_VERSION_ONE || oldVersion > newVersion || newVersion > CUR_VERSION) {
			UtilTool.log(StatisticsManager.TAG, "onUpgrade() false oldVersion = " + oldVersion + ", newVersion = "
					+ newVersion);
			return;
		}
		ArrayList<UpgradeDB> upgradeDBFuncS = new ArrayList<UpgradeDB>();
		upgradeDBFuncS.add(new UpgradeDBOneToTwo());
		upgradeDBFuncS.add(new UpgradeDBTwoToThree());
		upgradeDBFuncS.add(new UpgradeDBThreeToFour());
		upgradeDBFuncS.add(new UpgradeDBFourToFive());
		upgradeDBFuncS.add(new UpgradeDBFiveToSix());
		for (int i = oldVersion - 1; i < newVersion - 1; i++) {
			mUpdateResult = upgradeDBFuncS.get(i).onUpgradeDB(db);
			if (!mUpdateResult) {
				// 中间有任何一次升级失败，则直接返回
				if (db != null) {
					db.close();
				}
				mContext.deleteDatabase(DB_NAME);
				getWritableDatabase();
				break;
			}
		}
		upgradeDBFuncS.clear();
	}

	abstract class UpgradeDB {
		abstract boolean onUpgradeDB(SQLiteDatabase db);
	}

	public long insert(String tableName, ContentValues initialValues) {
		SQLiteDatabase db = getWritableDatabase();
		long rowId = 0;
		try {
			rowId = db.insert(tableName, null, initialValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowId;
	}

	public int delete(String tableName, String selection, String[] selectionArgs) {
		SQLiteDatabase db = getWritableDatabase();
		int count = 0;
		try {
			count = db.delete(tableName, selection, selectionArgs);
		} catch (Exception e) {
		}
		return count;
	}

	public int update(String tableName, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = getWritableDatabase();
		int count = 0;
		try {
			count = db.update(tableName, values, selection, selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	public Cursor query(String tableName, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db;
		try {
			db = getReadableDatabase();
		} catch (SQLiteException e) {

		} finally {
			db = getWritableDatabase();
		}
		try {
			return db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void delete(ArrayList<PostBean> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			for (int i = 0; i < list.size(); i++) {
				PostBean bean = list.get(i);
				String delete = "delete from " + TABLE_STATISTICS_NEW + " where " + TABLE_STATISTICS_COLOUM_ID + "="
						+ bean.mId;
				db.execSQL(delete);
			}
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}


	private void addColumnToTable(SQLiteDatabase db, String tableName, String columnName, String columnType,
			String defaultValue) {
		if (!isExistColumnInTable(db, tableName, columnName)) {
			db.beginTransaction();
			try {
				// 增加字段
				String updateSql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType + ";";
				db.execSQL(updateSql);
				// 提供默认值
				if (defaultValue != null) {
					if (columnType.equals(TYPE_TEXT)) {
						// 如果是字符串类型，则需加单引号
						defaultValue = "'" + defaultValue + "'";
					}

					updateSql = "update " + tableName + " set " + columnName + " = " + defaultValue;
					db.execSQL(updateSql);
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
		}
	}

	class UpgradeDBOneToTwo extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade1To2(db);
		}
	}

	class UpgradeDBTwoToThree extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade2To3(db);
		}
	}

	class UpgradeDBThreeToFour extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade3To4(db);
		}
	}

	class UpgradeDBFourToFive extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade4To5(db);
		}
	}

	class UpgradeDBFiveToSix extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade5To6(db);
		}
	}

	private boolean onUpgrade1To2(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, TABLE_STATISTICS, TABLE_STATISTICS_COLOUM_URL, TYPE_TEXT, null);
			addColumnToTable(db, TABLE_STATISTICS, TABLE_STATISTICS_COLOUM_OPCODE, TYPE_NUMERIC, String.valueOf(3));
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	private boolean onUpgrade2To3(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, TABLE_STATISTICS, TABLE_STATISTICS_COLOUM_NROOTINFO, TYPE_NUMERIC, String.valueOf(0));
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	private boolean onUpgrade3To4(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, TABLE_STATISTICS, TABLE_STATISTICS_COLOUM_ISNEW, TYPE_NUMERIC, String.valueOf(0));
			addColumnToTable(db, TABLE_STATISTICS, TABLE_STATISTICS_COLOUM_KEY, TYPE_TEXT, String.valueOf(-1));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	private boolean onUpgrade4To5(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			db.execSQL(CREATE_DATA_TABLE_SQL_NEW);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	private boolean onUpgrade5To6(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, TABLE_CTRLINFO, TABLE_CTRLINFO_COLOUM_PRIORITY, TYPE_NUMERIC, String.valueOf(0));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
}
