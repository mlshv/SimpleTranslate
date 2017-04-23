package me.mlshv.simpletranslate.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.Callable;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.Util;
import me.mlshv.simpletranslate.data.db.DbManager;
import me.mlshv.simpletranslate.data.model.Lang;
import me.mlshv.simpletranslate.network.LangsLoadTask;

public class LangsLoadActivity extends AppCompatActivity {
    private LangsLoadTask langsLoadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_langs_load);

        langsLoadTask = new LangsLoadTask(onLoadFinished);
        langsLoadTask.execute();
    }

    Callable<Void> onLoadFinished = new Callable<Void>() {
        @Override
        public Void call() throws Exception {
            List<Lang> langsList = langsLoadTask.get();
            DbManager dbManager = new DbManager(App.getInstance()).open();
            dbManager.updateLanguages(langsList);
            dbManager.close();
            Util.SPrefs.setLangListLoadedTrue();
            finish();
            return null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        langsLoadTask.cancel(true);
        langsLoadTask = null;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getResources().getString(R.string.please_wait_for_langs_to_load), Toast.LENGTH_LONG).show();
    }
}