package me.mlshv.simpletranslate.network;

import android.net.Uri;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TranslationRequest {
    private HttpURLConnection urlConnection = null;

    @Nullable
    public String perform(String translationDirection, String textToTranslate) {
        URL url;
        String result = null;
        try {
            url = new URL(buildRequest(translationDirection, textToTranslate));
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String responseJson = Util.readStream(in);
            result = getResultFromResponseJson(responseJson);
        } catch (Exception ignored) {
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return result;
    }

    public void cancel() {
        urlConnection.disconnect();
    }

    private String getResultFromResponseJson(String jsonResponse) throws JSONException {
        JSONObject jObject = new JSONObject(jsonResponse);
        JSONArray textArray = jObject.getJSONArray("text");
        return textArray.get(0).toString();
    }

    /*
    https://translate.yandex.net/api/v1.5/tr.json/translate ?
    key=<API-ключ>
     & text=<переводимый текст>
     & lang=<направление перевода>
     & [format=<формат текста>]
     & [options=<опции перевода>]
     & [callback=<имя callback-функции>]
     */
    private String buildRequest(String translationDirection, String textToTranslate) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("translate.yandex.net")
                .appendPath("api")
                .appendPath("v1.5")
                .appendPath("tr.json")
                .appendPath("translate")
                .appendQueryParameter("key", Config.trnslKey)
                .appendQueryParameter("text", textToTranslate)
                .appendQueryParameter("lang", translationDirection);
        return builder.build().toString();
    }
}