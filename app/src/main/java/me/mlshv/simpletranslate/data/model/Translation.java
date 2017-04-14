package me.mlshv.simpletranslate.data.model;

import android.database.Cursor;

import me.mlshv.simpletranslate.data.db.DbHelper;

public class Translation {
    private final String term;
    private final String translation;
    private TranslationVariations variations;

    public Translation(String term, String translation, TranslationVariations variations) {
        this.term = term;
        this.translation = translation;
        this.variations = variations;
    }

    public static Translation fromCursor(Cursor cursor) {
        String term = cursor.getString(cursor.getColumnIndex(DbHelper.SOURCE_STRING));
        String translation = cursor.getString(cursor.getColumnIndex(DbHelper.TRANSLATION));
        TranslationVariations variations = new TranslationVariations(cursor.getString(cursor.getColumnIndex(DbHelper.VARIATIONS)));
        return new Translation(term, translation, variations);
    }

    public String getTerm() {
        return term;
    }

    public String getTranslation() {
        return translation;
    }

    public TranslationVariations getVariations() {
        return variations;
    }

    @Override
    public String toString() {
        return "Translation{" +
                "term='" + term + '\'' +
                ", translation='" + translation + '\'' +
                ", variations=" + variations +
                '}';
    }
}
