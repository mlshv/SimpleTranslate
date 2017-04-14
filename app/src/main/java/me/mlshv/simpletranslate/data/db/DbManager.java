package me.mlshv.simpletranslate.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import me.mlshv.simpletranslate.data.model.Translation;

public class DbManager {
    private DbHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DbManager(Context context) {
        this.context = context;
    }

    public DbManager open() throws SQLException {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor fetchTable(String tableName) {
        String[] columns = new String[] { DbHelper._ID, DbHelper.SOURCE_STRING, DbHelper.TRANSLATION, DbHelper.VARIATIONS };
        Cursor cursor = database.query(tableName, columns, null, null, null, null, DbHelper._ID + " DESC"); // сортировка по убыванию id
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int updateHistory(String sourceString, String translation, String variations) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.SOURCE_STRING, sourceString);
        contentValues.put(DbHelper.TRANSLATION, translation);
        contentValues.put(DbHelper.VARIATIONS, variations);
        return database.update(DbHelper.HISTORY_TABLE, contentValues, DbHelper.SOURCE_STRING + " = " + sourceString, null);
    }

    public void clearHistory() {
        database.delete(DbHelper.HISTORY_TABLE, null, null);
    }

    public void saveTranslation(Translation translation, String table) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DbHelper.SOURCE_STRING, translation.getTerm());
        contentValue.put(DbHelper.TRANSLATION, translation.getTranslation());
        if (translation.getVariations() != null) {
            contentValue.put(DbHelper.VARIATIONS, translation.getVariations().getJson());
        }
        deleteRowsWhereSourceStringIs(translation.getTerm(), table);
        database.insert(table, null, contentValue);
    }

    private void deleteRowsWhereSourceStringIs(String sourceString, String table) {
        String whereClause = DbHelper.SOURCE_STRING + "=?";
        String[] whereArgs = new String[] { sourceString };
        database.delete(table, whereClause, whereArgs);
    }
}
