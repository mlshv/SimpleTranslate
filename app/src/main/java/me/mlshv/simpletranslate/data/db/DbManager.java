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
        String selection = DbHelper.SAVED_STATE + "=? OR " + DbHelper.SAVED_STATE + "=?";
        String[] selectionArgs = new String[] {
                String.valueOf(Translation.SAVED_HISTORY),
                String.valueOf(Translation.SAVED_HISTORY_FAVORITES)
        };
        return fetchWhere(selection, selectionArgs);
    }

    public Cursor fetchFavorites() {
        String selection = DbHelper.SAVED_STATE + "=?";
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
                DbHelper.SAVED_STATE,
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

    public void saveTranslation(Translation translation) {
        Log.d(App.tag(this), "saveTranslation: сохраняю перевод " + translation);
        ContentValues contentValue = new ContentValues();
        contentValue.put(DbHelper.SOURCE_LANG, translation.getTermLang());
        contentValue.put(DbHelper.TRANSLATION_LANG, translation.getTranslationLang());
        contentValue.put(DbHelper.TERM, translation.getTerm());
        contentValue.put(DbHelper.TRANSLATION, translation.getTranslation());
        contentValue.put(DbHelper.SAVED_STATE, translation.getSavedState());
        if (translation.getVariations() != null) {
            contentValue.put(DbHelper.VARIATIONS, translation.getVariations().getJson());
        }
        deleteTranslation(translation);
        database.insert(DbHelper.TRANSLATIONS_TABLE, null, contentValue);
    }

    public void deleteTranslation(Translation translation) {
        Log.d(App.tag(this), "deleteTranslation: удаляю перевод " + translation);
        String whereClause = DbHelper.TERM + "=? AND " + DbHelper.TRANSLATION + "=?";
        String[] whereArgs = new String[] { translation.getTerm(), translation.getTranslation() };
        database.delete(DbHelper.TRANSLATIONS_TABLE, whereClause, whereArgs);
    }
}
