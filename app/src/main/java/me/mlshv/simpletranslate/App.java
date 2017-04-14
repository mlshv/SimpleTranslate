package me.mlshv.simpletranslate;


import android.app.Application;

public class App extends Application {
    private static App singleton;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    public static App getInstance() {
        return singleton;
    }

    public static String tag(Object logSubject) {
        return logSubject.getClass().getSimpleName();
    }
}
