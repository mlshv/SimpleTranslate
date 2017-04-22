package me.mlshv.simpletranslate.network;


import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.Callable;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.data.db.DbManager;
import me.mlshv.simpletranslate.data.model.Translation;
import me.mlshv.simpletranslate.data.model.TranslationVariations;
import me.mlshv.simpletranslate.util.SpHelper;

public class TranslationTask extends AsyncTask<Object, String, Translation> {
    private TranslationRequest translationRequest;
    private TranslationVariationsRequest variationsRequest;
    private DbManager dbManager = new DbManager(App.getInstance()).open();
    private Callable<Void> onResultCallback;

    public TranslationTask(Callable<Void> onResultCallback) {
        this.onResultCallback = onResultCallback;
    }

    @Override
    protected void onPreExecute() {
        Log.d(App.tag(this), "Запускаю TranslationTask");
    }

    @Override
    protected Translation doInBackground(Object... params) {
        Log.d(App.tag(this), "TranslationTask запущена");
        String textToTranslate = (String) params[0];
        String source = SpHelper.loadSourceLangCode();
        String target = SpHelper.loadTargetLangCode();
        Translation t = dbManager.tryGetFromCache(textToTranslate, source + "-" + target);
        if (t != null) {
            Log.d(App.tag(this), "Достал из кэша " + t);
            return t;
        }
        translationRequest = new TranslationRequest();
        variationsRequest = new TranslationVariationsRequest();
        String result = translationRequest.perform(source + "-" + target, textToTranslate);
        TranslationVariations variations = variationsRequest.perform(source + "-" + target, textToTranslate);
        t = new Translation(source + "-" + target, textToTranslate, result, variations);
        dbManager.updateOrInsertTranslation(t);
        return t;
    }

    @Override
    protected void onPostExecute(Translation result) {
        Log.d(App.tag(this), "TranslationTask завершена");
        try {
            onResultCallback.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbManager.close();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (translationRequest != null)
            translationRequest.cancel();
        if (variationsRequest != null)
            variationsRequest.cancel();
        dbManager.close();
    }
}