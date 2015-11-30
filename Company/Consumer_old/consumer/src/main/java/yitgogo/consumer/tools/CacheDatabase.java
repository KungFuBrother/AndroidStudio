package yitgogo.consumer.tools;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class CacheDatabase {

	Context context;
	private static String databaseName = "yitgogo.db";
	private static String tableName = "cache_data";
	public static String column_url = "url";
	public static String column_parameters = "parameters";
	public static String column_result = "result";
	public static String column_time = "time";
	SQLiteDatabase cacheDatabase;

	public CacheDatabase(Context context) {
		this.context = context;
		initTable();
	}

	private void openDatabase() {
		cacheDatabase = context.openOrCreateDatabase(databaseName,
				Context.MODE_PRIVATE, null);
	}

	private void closeDatabase() {
		cacheDatabase.close();
	}

	private void initTable() {
		openDatabase();
		if (!containTable(tableName)) {
			createTable();
		} else {
			if (!containColumn(column_url) || !containColumn(column_result)
					|| !containColumn(column_time)) {
				reCreateTable();
			}
		}
		closeDatabase();
	}

	private void createTable() {
		cacheDatabase.execSQL("CREATE TABLE " + tableName
				+ " (_id integer primary key autoincrement, " + column_url
				+ " varchar, " + column_parameters + " varchar, "
				+ column_result + " varchar, " + column_time + " varchar)");
	}

	public boolean containData(String url, List<NameValuePair> nameValuePairs) {
		String selection = column_url + "=? and " + column_parameters + "=?";
		String parameters = "";
		if (nameValuePairs != null) {
			parameters = nameValuePairs.toString().trim();
		}
		String[] selectionValue = { url, parameters };
		openDatabase();
		Cursor cursor = cacheDatabase.query(tableName, null, selection,
				selectionValue, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				closeDatabase();
				return true;
			}
		}
		closeDatabase();
		return false;
	}

	public ContentValues getResultData(String url,
			List<NameValuePair> nameValuePairs) {
		String selection = column_url + "=? and " + column_parameters + "=?";
		String parameters = "";
		if (nameValuePairs != null) {
			parameters = nameValuePairs.toString().trim();
		}
		String[] selectionValue = { url, parameters };
		openDatabase();
		Cursor cursor = cacheDatabase.query(tableName, null, selection,
				selectionValue, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				ContentValues contentValues = new ContentValues();
				String result = cursor.getString(cursor
						.getColumnIndex(column_result));
				String time = cursor.getString(cursor
						.getColumnIndex(column_time));
				contentValues.put("data", result);
				contentValues.put("time", time);
				closeDatabase();
				return contentValues;
			}
		}
		closeDatabase();
		return null;
	}

	public void insertData(ContentValues values) {
		openDatabase();
		cacheDatabase.insert(tableName, null, values);
		closeDatabase();
	}

	public void updateData(ContentValues values, String url,
			List<NameValuePair> nameValuePairs) {
		String selection = column_url + "=? and " + column_parameters + "=?";
		String parameters = "";
		if (nameValuePairs != null) {
			parameters = nameValuePairs.toString().trim();
		}
		String[] selectionValue = { url, parameters };
		openDatabase();
		cacheDatabase.update(tableName, values, selection, selectionValue);
		closeDatabase();
	}

	private void reCreateTable() {
		cacheDatabase.execSQL("DROP TABLE " + tableName);
		createTable();
	}

	private boolean containTable(String tableName) {
		boolean result = false;
		if (TextUtils.isEmpty(tableName)) {
			return false;
		}
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
					+ tableName.trim() + "' ";
			cursor = cacheDatabase.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}
		} catch (Exception e) {
		}
		return result;
	}

	private boolean containColumn(String columnName) {
		boolean result = false;
		if (TextUtils.isEmpty(columnName)) {
			return false;
		}
		Cursor cursor = null;
		try {
			cursor = cacheDatabase
					.rawQuery(
							"select * from sqlite_master where name = ? and sql like ?",
							new String[] { tableName, "%" + columnName + "%" });
			result = null != cursor && cursor.moveToFirst();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;
	}
}
