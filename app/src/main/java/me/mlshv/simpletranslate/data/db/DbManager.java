package me.mlshv.simpletranslate.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.data.model.Lang;
import me.mlshv.simpletranslate.data.model.Translation;

public class DbManager {
    private DbHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    private List<Lang> languages;

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

    public Cursor fetchHistory() {
        String selection = DbHelper.STORE_OPTIONS + " & ? != 0";
        String[] selectionArgs = new String[] { String.valueOf(Translation.SAVED_HISTORY) };
        return fetchTranslationsWhere(selection, selectionArgs);
    }

    public Cursor fetchFavorites() {
        String selection = DbHelper.STORE_OPTIONS + " & ? != 0";
        String[] selectionArgs = new String[] { String.valueOf(Translation.SAVED_FAVORITES) };
        return fetchTranslationsWhere(selection, selectionArgs);
    }

    @Nullable
    public Translation tryGetFromCache(String term, String direction) {
        String selection = DbHelper.TERM + "=? AND " + DbHelper.DIRECTION + "=?";
        String[] selectionArgs = new String[] { term, direction };
        Cursor c = fetchTranslationsWhere(selection, selectionArgs);
        if (c == null || c.getCount() == 0) return null;
        return Translation.fromCursor(c);
    }

    @Nullable
    private Cursor fetchTranslationsWhere(String selection, String[] selectionArgs) {
        String[] columns = new String[] {
                DbHelper._ID,
                DbHelper.DIRECTION,
                DbHelper.TERM,
                DbHelper.TRANSLATION,
                DbHelper.STORE_OPTIONS,
                DbHelper.VARIATIONS };
        if (database != null && database.isOpen()) {
            Cursor cursor = database.query(
                    DbHelper.TRANSLATIONS_TABLE, columns, selection, selectionArgs, null, null,
                    DbHelper._ID + " DESC"); // сортировка по убыванию id
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;
        }

        return null;
    }

    public void updateOrInsertTranslation(Translation translation) {
        Translation existed = tryGetFromCache(translation.getTerm(), translation.getDirection());
        if (existed != null) {
            translation.addStoreOption(existed.getStoreOptions());
        }
        insertTranslation(translation);
    }

    public void insertTranslation(Translation translation) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.DIRECTION, translation.getDirection());
        contentValues.put(DbHelper.TERM, translation.getTerm());
        contentValues.put(DbHelper.TRANSLATION, translation.getTranslation());
        contentValues.put(DbHelper.STORE_OPTIONS, translation.getStoreOptions());
        if (translation.getVariations() != null) {
            contentValues.put(DbHelper.VARIATIONS, translation.getVariations().getJson());
        }
        deleteTranslation(translation);
        Log.d(App.tag(this), "insertTranslation: сохраняю перевод " + translation);
        if (database != null && database.isOpen())
            database.insert(DbHelper.TRANSLATIONS_TABLE, null, contentValues);
    }

    public void deleteTranslation(Translation translation) {
        Log.d(App.tag(this), "deleteTranslation: удаляю перевод " + translation);
        String whereClause = DbHelper.TERM + "=? AND " +DbHelper.DIRECTION + "=?";
        String[] whereArgs = new String[] { translation.getTerm(), translation.getDirection() };
        if (database != null && database.isOpen())
            database.delete(DbHelper.TRANSLATIONS_TABLE, whereClause, whereArgs);
    }

    public void clearCache() {
        String whereClause = DbHelper.STORE_OPTIONS + "=?";
        String[] whereArgs = new String[] {String.valueOf(Translation.SAVED_CACHE)};
        database.delete(DbHelper.TRANSLATIONS_TABLE, whereClause, whereArgs);
    }

    public void clearHistory() {
        removeStoreOptionEverywhere(Translation.SAVED_HISTORY);
    }

    public void clearFavorites() {
        removeStoreOptionEverywhere(Translation.SAVED_FAVORITES);
    }

    private void removeStoreOptionEverywhere(int storeOption) {
        database.execSQL("UPDATE " + DbHelper.TRANSLATIONS_TABLE +
                " SET " + DbHelper.STORE_OPTIONS + " = " +
                DbHelper.STORE_OPTIONS + " & ~" + storeOption +
                " WHERE " + DbHelper.STORE_OPTIONS + " & " + storeOption + " != 0");
    }

    public List<Lang> getLanguages() {
        List<Lang> langs = new ArrayList<>();
        String[] columns = new String[] {
                DbHelper._ID,
                DbHelper.LANG_CODE,
                DbHelper.LANG_NAME };
        if (database != null && database.isOpen()) {
            Cursor cursor = database.query(
                    DbHelper.LANGS_TABLE, columns, null, null, null, null,
                    DbHelper.LANG_NAME + " ASC"); // сортировка по возрастанию
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isLast()) {
                    langs.add(Lang.fromCursor(cursor));
                    cursor.moveToNext();
                }
            }
        }
        return langs;
    }

    public void updateLanguages(List<Lang> langsList) {
        database.delete(DbHelper.LANGS_TABLE, null, null);
        ContentValues contentValues = new ContentValues();
        for (Lang lang : langsList) {
            contentValues.put(DbHelper.LANG_CODE, lang.getCode());
            contentValues.put(DbHelper.LANG_NAME, lang.getName());
            database.insert(DbHelper.LANGS_TABLE, null, contentValues);
        }
    }
}
