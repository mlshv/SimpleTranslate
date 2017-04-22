package me.mlshv.simpletranslate.util;

import java.util.ArrayList;
import java.util.List;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.data.db.DbManager;
import me.mlshv.simpletranslate.data.model.Lang;

public class LangUtil {
    private static List<Lang> languages;
    private static ArrayList<String> langNames;

    static {
        DbManager dbManager = new DbManager(App.getInstance()).open();
        languages = dbManager.getLanguages();
        dbManager.close();
    }

    public static ArrayList<String> getLangNames() {
        if (langNames == null) { // ленивая инициализация
            langNames = new ArrayList<>();
            for (Lang lang : languages) {
                langNames.add(lang.getName());
            }
        }
        return langNames;
    }

    public static String getCodeByName(String name) {
        for (Lang l : languages) {
            if (l.getName().equals(name)) {
                return l.getCode();
            }
        }
        return null;
    }

    public static String getNameByCode(String code) {
        for (Lang l : languages) {
            if (l.getCode().equals(code)) {
                return l.getName();
            }
        }
        return null;
    }
}
