package me.mlshv.simpletranslate.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import me.mlshv.simpletranslate.App;

public class SpHelper {
    private static SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
    public static String getSourceLangCode() {
        return preferences.getString("SourceLang", "ru");
    }

    public static String getTargetLangCode() {
        return preferences.getString("TargetLang", "en");
    }

    public static void saveSourceTargetLangs(String source, String target) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("SourceLang", source);
        editor.putString("TargetLang", target);
        editor.apply();
    }
}
