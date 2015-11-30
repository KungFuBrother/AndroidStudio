package yitgogo.consumer.suning.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class SuningCarDataManager {

    Context context;
    private static String databaseName = "yitgogo.db";
    private static String tableName = "car_suning";
    public static String column_sku = "sku";
    public static String column_object = "object";
    public static String column_count = "count";
    public static String column_selection = "selection";
    SQLiteDatabase cacheDatabase;

    public SuningCarDataManager(Context context) {
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
        }
        closeDatabase();
    }

    private void createTable() {
        cacheDatabase.execSQL("CREATE TABLE " + tableName
                + " (_id integer primary key autoincrement, " + column_sku
                + " varchar, " + column_object + " varchar, "
                + column_count + " int, " + column_selection + " int)");
    }


    public boolean containData(String sku) {
        String selection = column_sku + "=?";
        String[] selectionValue = {sku};
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

    public void insertData(ContentValues values) {
        openDatabase();
        cacheDatabase.insert(tableName, null, values);
        closeDatabase();
    }

    public void updateData(ContentValues values, String sku) {
        String selection = column_sku + "=?";
        String[] selectionValue = {sku};
        openDatabase();
        cacheDatabase.update(tableName, values, selection, selectionValue);
        closeDatabase();
    }


    public ContentValues getData(String sku) {
        String selection = column_sku + "=?";
        String[] selectionValue = {sku};
        openDatabase();
        Cursor cursor = cacheDatabase.query(tableName, null, selection,
                selectionValue, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                ContentValues values = new ContentValues();
                values.put(column_sku, cursor.getString(cursor
                        .getColumnIndex(column_sku)));
                values.put(column_object, cursor.getString(cursor
                        .getColumnIndex(column_object)));
                values.put(column_count, cursor.getInt(cursor
                        .getColumnIndex(column_count)));
                values.put(column_selection, cursor.getInt(cursor
                        .getColumnIndex(column_selection)));
                closeDatabase();
                return values;
            }
        }
        closeDatabase();
        return null;
    }

    public List<ContentValues> getDatas() {
        openDatabase();
        Cursor cursor = cacheDatabase.query(tableName, null, null,
                null, null, null, null, null);
        if (cursor != null) {
            System.out.println(cursor.getCount());
            if (cursor.getCount() > 0) {
                List<ContentValues> contentValues = new ArrayList<>();
                while (cursor.moveToNext()) {
                    ContentValues values = new ContentValues();
                    values.put(column_sku, cursor.getString(cursor
                            .getColumnIndex(column_sku)));
                    values.put(column_object, cursor.getString(cursor
                            .getColumnIndex(column_object)));
                    values.put(column_count, cursor.getInt(cursor
                            .getColumnIndex(column_count)));
                    values.put(column_selection, cursor.getInt(cursor
                            .getColumnIndex(column_selection)));
                    contentValues.add(values);
                }
                closeDatabase();
                return contentValues;
            }
        }
        closeDatabase();
        return null;
    }

    public void selectAll(int selection) {
        openDatabase();
        cacheDatabase.execSQL("UPDATE " + tableName + " SET " + column_selection + " = '" + selection + "'");
        closeDatabase();
    }

    public void deleteSelectedProducts() {
        openDatabase();
        cacheDatabase.execSQL("DELETE FROM " + tableName + " WHERE " + column_selection + " = 1");
        closeDatabase();
    }

    public void delete(String sku) {
        openDatabase();
        cacheDatabase.execSQL("DELETE FROM " + tableName + " WHERE " + column_sku + " = " + sku);
        closeDatabase();
    }


    public List<ContentValues> geteSelectedProducts() {
        List<ContentValues> contentValues = new ArrayList<>();
        String selection = column_selection + "=?";
        String[] selectionValue = {"1"};
        openDatabase();
        Cursor cursor = cacheDatabase.query(tableName, null, selection,
                selectionValue, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ContentValues values = new ContentValues();
                    values.put(column_sku, cursor.getString(cursor
                            .getColumnIndex(column_sku)));
                    values.put(column_object, cursor.getString(cursor
                            .getColumnIndex(column_object)));
                    values.put(column_count, cursor.getInt(cursor
                            .getColumnIndex(column_count)));
                    values.put(column_selection, cursor.getInt(cursor
                            .getColumnIndex(column_selection)));
                    contentValues.add(values);
                }
            }
        }
        closeDatabase();
        return contentValues;
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
}
