package me.mlshv.simpletranslate;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.mlshv.simpletranslate.data.db.DbManager;
import me.mlshv.simpletranslate.data.model.Lang;

public final class Util {
    public static String readStream(InputStream in) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

    public static void bottomNavigationRemoveShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
        }
    }

    public static final class Langs {
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

        private Langs() {}
    }

    public static final class SPrefs {
        private static SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        public static String loadSourceLangCode() {
            String result = preferences.getString("SourceLang", "ru");
            Log.d("SPrefs", "loadSourceLangCode: " + result);
            return result;
        }

        public static String loadTargetLangCode() {
            String result = preferences.getString("TargetLang", "en");
            Log.d("SPrefs", "loadTargetLangCode: " + result);
            return result;
        }

        public static void saveSourceLangCode(String code) {
            SharedPreferences.Editor editor = preferences.edit();
            Log.d("SPrefs", "saveSourceLangCode: " + code);
            editor.putString("SourceLang", code);
            editor.apply();
        }

        public static void saveTargetLangCode(String code) {
            SharedPreferences.Editor editor = preferences.edit();
            Log.d("SPrefs", "saveTargetLangCode: " + code);
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

    private Util() {}
}
