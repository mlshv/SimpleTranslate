package me.mlshv.simpletranslate.data.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "simple_translate.db";
    private static final int DB_VERSION = 1;

    // Таблицы для перевода и языков
    public static final String TRANSLATIONS_TABLE = "translations";
    public static final String LANGS_TABLE = "langs";

    // Колонки таблицы с переводами
    public static final String _ID = "_id";
    public static final String SOURCE_LANG = "source_lang";
    public static final String TRANSLATION_LANG = "translation_lang";
    public static final String TERM = "word";
    public static final String TRANSLATION = "translation";
    public static final String STORE_OPTIONS = "store_options"; // битовая маска. Описание в Translation.java
    public static final String VARIATIONS = "variations";

    // Колонки таблицы с языками
    public static final String LANG_CODE = "lang_code";
    public static final String LANG_NAME = "lang_name";

    private static final String CREATE_TRANSLATIONS_TABLE = "create table " + TRANSLATIONS_TABLE + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SOURCE_LANG + " TEXT, "
            + TRANSLATION_LANG + " TEXT, "
            + TERM + " TEXT NOT NULL UNIQUE, "
            + TRANSLATION + " TEXT, "
            + STORE_OPTIONS + " INTEGER, "
            + VARIATIONS + " TEXT);";

    private static final String CREATE_LANGS_TABLE = "create table " + LANGS_TABLE + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LANG_CODE + " TEXT NOT NULL UNIQUE, "
            + LANG_NAME + " TEXT NOT NULL UNIQUE);";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TRANSLATIONS_TABLE);
        sqLiteDatabase.execSQL(CREATE_LANGS_TABLE);
        putLangs(sqLiteDatabase);
    }

    DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRANSLATIONS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LANGS_TABLE);
        onCreate(sqLiteDatabase);
    }

    private void putLangs(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("INSERT INTO langs (lang_code, lang_name) VALUES (\"ru\", \"Русский\");");
        sqLiteDatabase.execSQL("INSERT INTO langs (lang_code, lang_name) VALUES (\"en\", \"Английский\");");
        sqLiteDatabase.execSQL("INSERT INTO langs (lang_code, lang_name) VALUES (\"fr\", \"Французский\");");
        sqLiteDatabase.execSQL("INSERT INTO langs (lang_code, lang_name) VALUES (\"de\", \"Немецкий\");");
    }
}
