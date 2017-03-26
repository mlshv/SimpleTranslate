package me.mlshv.simpletranslate.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.R;

public class SpHelper {
    private static SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
    public static int getSourceLangId() {
        return preferences.getInt("SourceLang", R.string.russian);
    }

    public static int getTargetLangId() {
        return preferences.getInt("TargetLang", R.string.english);
    }

    public static void saveSourceTargetLangIds(int source, int target) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("SourceLang", source);
        editor.putInt("TargetLang", target);
        editor.apply();
    }
}
