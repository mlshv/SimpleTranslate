package me.mlshv.simpletranslate.data.model;

import android.database.Cursor;

import me.mlshv.simpletranslate.data.db.DbHelper;

public class Translation {
    private final String termLang;
    private final String translationLang;
    private final String term;
    private final String translation;
    private final TranslationVariations variations;

    public Translation(String termLang, String translationLang, String term, String translation, TranslationVariations variations) {
        this.termLang = termLang;
        this.translationLang = translationLang;
        this.term = term;
        this.translation = translation;
        this.variations = variations;
    }

    public static Translation fromCursor(Cursor cursor) {
        String termLang = cursor.getString(cursor.getColumnIndex(DbHelper.SOURCE_LANG));
        String translationLang = cursor.getString(cursor.getColumnIndex(DbHelper.TRANSLATION_LANG));
        String term = cursor.getString(cursor.getColumnIndex(DbHelper.SOURCE_STRING));
        String translation = cursor.getString(cursor.getColumnIndex(DbHelper.TRANSLATION));
        TranslationVariations variations = new TranslationVariations(cursor.getString(cursor.getColumnIndex(DbHelper.VARIATIONS)));
        return new Translation(termLang, translationLang, term, translation, variations);
    }

    public String getTermLang() {
        return termLang;
    }

    public String getTranslationLang() {
        return translationLang;
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
        return "Translation { " + term + " => " + translation + " }";
    }
}
