package me.mlshv.simpletranslate.network;

import android.net.Uri;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TranslationRequest {
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
                .appendQueryParameter("key", Config.apiKey)
                .appendQueryParameter("text", textToTranslate)
                .appendQueryParameter("lang", translationDirection);
        return builder.build().toString();
    }

    @Nullable
    public String getTranslation(String translationDirection, String textToTranslate) {
        URL url;
        HttpURLConnection urlConnection = null;
        String result = null;
        try {
            url = new URL(buildRequest(translationDirection, textToTranslate));
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String responseJson = readStream(in);
            result = getResultFromResponseJson(responseJson);
        } catch (Exception ignored) {
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return result;
    }

    private String getResultFromResponseJson(String jsonResponse) throws JSONException {
        JSONObject jObject = new JSONObject(jsonResponse);
        JSONArray textArray = jObject.getJSONArray("text");
        return textArray.get(0).toString();
    }

    private String readStream(InputStream in) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
}
