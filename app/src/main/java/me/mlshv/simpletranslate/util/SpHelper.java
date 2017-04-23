package me.mlshv.simpletranslate.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import me.mlshv.simpletranslate.App;

public class SpHelper {
    private static SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
    public static String loadSourceLangCode() {
        String result = preferences.getString("SourceLang", "ru");
        Log.d("SpHelper", "loadSourceLangCode: " + result);
        return result;
    }

    public static String loadTargetLangCode() {
        String result = preferences.getString("TargetLang", "en");
        Log.d("SpHelper", "loadTargetLangCode: " + result);
        return result;
    }

    public static void saveSourceLangCode(String code) {
        SharedPreferences.Editor editor = preferences.edit();
        Log.d("SpHelper", "saveSourceLangCode: " + code);
        editor.putString("SourceLang", code);
        editor.apply();
    }

    public static void saveTargetLangCode(String code) {
        SharedPreferences.Editor editor = preferences.edit();
        Log.d("SpHelper", "saveTargetLangCode: " + code);
        editor.putString("TargetLang", code);
        editor.apply();
    }

    public static void setLangListLoadedTrue() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("LangsListLoaded", true);
        editor.apply();
    }

    public static boolean isLangListLoaded() {
        return preferences.getBoolean("LangsListLoaded", false);
    }
}