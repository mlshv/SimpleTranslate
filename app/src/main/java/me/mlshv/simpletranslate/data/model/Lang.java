package me.mlshv.simpletranslate.data.model;


import android.database.Cursor;

import me.mlshv.simpletranslate.data.db.DbHelper;

public class Lang {
    private final String code;
    private final String name;

    public Lang(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Lang fromCursor(Cursor cursor) {
        String code = cursor.getString(cursor.getColumnIndex(DbHelper.LANG_CODE));
        String name = cursor.getString(cursor.getColumnIndex(DbHelper.LANG_NAME));
        return new Lang(code, name);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Lang{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
