package me.mlshv.simpletranslate.data.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "simple_translate.db";
    private static final int DB_VERSION = 1;

    // Таблицы для истории и избранного
    public static final String HISTORY_TABLE = "history";
    public static final String FAVORITES_TABLE = "favorites";

    // Колонки таблиц
    public static final String _ID = "_id";
    public static final String SOURCE_LANG = "source_lang";
    public static final String TRANSLATION_LANG = "translation_lang";
    public static final String SOURCE_STRING = "word";
    public static final String TRANSLATION = "translation";
    public static final String VARIATIONS = "variations";

    private static final String CREATE_HISTORY_TABLE = "create table " + HISTORY_TABLE + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SOURCE_LANG + " TEXT, "
            + TRANSLATION_LANG + " TEXT, "
            + SOURCE_STRING + " TEXT NOT NULL UNIQUE, "
            + TRANSLATION + " TEXT, "
            + VARIATIONS + " TEXT);";

    private static final String CREATE_FAVORITES_TABLE = "create table " + FAVORITES_TABLE + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SOURCE_LANG + " TEXT, "
            + TRANSLATION_LANG + " TEXT, "
            + SOURCE_STRING + " TEXT NOT NULL UNIQUE, "
            + TRANSLATION + " TEXT, "
            + VARIATIONS + " TEXT);";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_HISTORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_FAVORITES_TABLE);
    }

    DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FAVORITES_TABLE);
        onCreate(sqLiteDatabase);
    }
}
