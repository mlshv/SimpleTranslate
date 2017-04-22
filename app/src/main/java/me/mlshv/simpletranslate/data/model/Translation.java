package me.mlshv.simpletranslate.data.model;

import android.database.Cursor;

import me.mlshv.simpletranslate.data.db.DbHelper;

public class Translation {
    public static final int SAVED_CACHE = 0;
    public static final int SAVED_HISTORY = 1;
    public static final int SAVED_FAVORITES = 10;

    private final String termLang;
    private final String translationLang;
    private final String term;
    private final String translation;
    private final TranslationVariations variations;
    // Битовая маска. Нулевой разряд - сохранение в истории, первый - в избранном. Если по нулям оба - кэш
    private int storeOptions = SAVED_CACHE;

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
        String term = cursor.getString(cursor.getColumnIndex(DbHelper.TERM));
        String translation = cursor.getString(cursor.getColumnIndex(DbHelper.TRANSLATION));
        int savedState = cursor.getInt(cursor.getColumnIndex(DbHelper.STORE_OPTIONS));
        TranslationVariations variations = new TranslationVariations(cursor.getString(cursor.getColumnIndex(DbHelper.VARIATIONS)));
        Translation t = new Translation(termLang, translationLang, term, translation, variations);
        t.addStoreOption(savedState);
        return t;
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

    public int getStoreOptions() {
        return storeOptions;
    }

    public void addStoreOption(int mask) {
        this.storeOptions |= mask;
    }

    public void removeStoreOption(int mask) {
        this.storeOptions &= ~mask;
    }

    public boolean hasOption(int option) {
        return (this.storeOptions & option) != 0;
    }

    @Override
    public String toString() {
        return "Translation { " + term + " => " + translation + " } : " + storeOptions;
    }

    @Override
    public int hashCode() {
        return term.hashCode() * 13 + translation.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Translation t = (Translation) o;
        return this.term.equals(t.getTerm()) && this.translation.equals(t.getTranslation());
    }
}
