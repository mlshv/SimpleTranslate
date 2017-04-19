package me.mlshv.simpletranslate.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import me.mlshv.simpletranslate.App;
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

    public Cursor fetchHistory() {
        String selection = DbHelper.STORE_OPTIONS + " & ? != 0";
        String[] selectionArgs = new String[] { String.valueOf(Translation.SAVED_HISTORY) };
        return fetchWhere(selection, selectionArgs);
    }

    public Cursor fetchFavorites() {
        String selection = DbHelper.STORE_OPTIONS + " & ? != 0";
        String[] selectionArgs = new String[] { String.valueOf(Translation.SAVED_FAVORITES) };
        return fetchWhere(selection, selectionArgs);
    }

    @Nullable
    public Translation tryGetFromCache(String term) {
        String selection = DbHelper.TERM + "=?";
        String[] selectionArgs = new String[] { term };
        Cursor c = fetchWhere(selection, selectionArgs);
        if (c.getCount() == 0) return null;
        return Translation.fromCursor(c);
    }

    private Cursor fetchWhere(String selection, String[] selectionArgs) {
        String[] columns = new String[] {
                DbHelper._ID,
                DbHelper.SOURCE_LANG,
                DbHelper.TRANSLATION_LANG,
                DbHelper.TERM,
                DbHelper.TRANSLATION,
                DbHelper.STORE_OPTIONS,
                DbHelper.VARIATIONS };
        Cursor cursor = database.query(DbHelper.TRANSLATIONS_TABLE, columns, selection, selectionArgs, null, null, DbHelper._ID + " DESC"); // сортировка по убыванию id
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void clearTranslations() {
        database.delete(DbHelper.TRANSLATIONS_TABLE, null, null);
    }

    public void updateOrInsertTranslation(Translation translation) {
        Translation existed = tryGetFromCache(translation.getTerm());
        if (existed != null) {
            translation.addStoreOption(existed.getStoreOptions());
        }
        insertTranslation(translation);
    }

    public void insertTranslation(Translation translation) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.SOURCE_LANG, translation.getTermLang());
        contentValues.put(DbHelper.TRANSLATION_LANG, translation.getTranslationLang());
        contentValues.put(DbHelper.TERM, translation.getTerm());
        contentValues.put(DbHelper.TRANSLATION, translation.getTranslation());
        contentValues.put(DbHelper.STORE_OPTIONS, translation.getStoreOptions());
        if (translation.getVariations() != null) {
            contentValues.put(DbHelper.VARIATIONS, translation.getVariations().getJson());
        }
        deleteTranslation(translation);
        Log.d(App.tag(this), "insertTranslation: сохраняю перевод " + translation);
        database.insert(DbHelper.TRANSLATIONS_TABLE, null, contentValues);
    }

    public void deleteTranslation(Translation translation) {
        Log.d(App.tag(this), "deleteTranslation: удаляю перевод " + translation);
        String whereClause = DbHelper.TERM + "=?";
        String[] whereArgs = new String[] { translation.getTerm() };
        database.delete(DbHelper.TRANSLATIONS_TABLE, whereClause, whereArgs);
    }
}
