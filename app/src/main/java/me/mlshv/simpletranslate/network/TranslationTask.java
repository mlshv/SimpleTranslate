package me.mlshv.simpletranslate.network;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.util.concurrent.Callable;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.data.db.DbManager;
import me.mlshv.simpletranslate.data.model.Translation;
import me.mlshv.simpletranslate.data.model.TranslationVariations;

public class TranslationTask extends AsyncTask<Object, String, TranslationTaskResult> {
    private TranslationRequest translationRequest;
    private TranslationVariationsRequest variationsRequest;
    private DbManager dbManager = new DbManager(App.getInstance()).open();
    private Callable<Void> resultCallback;

    public TranslationTask(Callable<Void> resultCallback) {
        this.resultCallback = resultCallback;
    }

    @Override
    protected TranslationTaskResult doInBackground(Object... params) {
        Log.d(App.tag(this), "TranslationTask " + hashCode() + " запущена");
        String textToTranslate = (String) params[0];
        String direction = (String) params[1];
        Translation t = dbManager.tryGetFromCache(textToTranslate, direction);
        if (t != null) {
            Log.d(App.tag(this), "Достал из кэша " + t);
            return new TranslationTaskResult(t);
        }
        translationRequest = new TranslationRequest();
        variationsRequest = new TranslationVariationsRequest();
        String translationResultString;
        try {
            translationResultString = translationRequest.perform(direction, textToTranslate);
        } catch (Exception e) {
            Log.d(App.tag(this), "Исключение при загрузке перевода " + e);
            return new TranslationTaskResult(e);
        }
        TranslationVariations variations = null;
        try {
            variations = variationsRequest.perform(direction, textToTranslate);
        } catch (JSONException ignored) {} // не получилось найти варианты перевода, игнорируем
        t = new Translation(direction, textToTranslate, translationResultString, variations);
        dbManager.updateOrInsertTranslation(t);
        return new TranslationTaskResult(t);
    }

    @Override
    protected void onPostExecute(TranslationTaskResult result) {
        Log.d(App.tag(this), "TranslationTask завершена");
        try {
            resultCallback.call();
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
        resultCallback = null;
        dbManager.close();
    }
}