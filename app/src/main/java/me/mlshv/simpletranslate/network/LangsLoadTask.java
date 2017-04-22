package me.mlshv.simpletranslate.network;

import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.Callable;

import me.mlshv.simpletranslate.data.model.Lang;

public class LangsLoadTask extends AsyncTask<Object, Object, List<Lang>> {
    private LangsLoadRequest request;
    private Callable<Void> resultCallback;

    public LangsLoadTask(Callable<Void> resultCallback) {
        this.resultCallback = resultCallback;
    }

    @Override
    protected List<Lang> doInBackground(Object[] objects) {
        request = new LangsLoadRequest();
        return request.perform();
    }

    @Override
    protected void onPostExecute(List<Lang> langs) {
        super.onPostExecute(langs);
        try {
            resultCallback.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (request != null)
            request.cancel();
            resultCallback = null;
    }
}
