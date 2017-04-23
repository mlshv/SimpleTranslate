package me.mlshv.simpletranslate.network;

import android.net.Uri;
import android.support.annotation.Nullable;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import me.mlshv.simpletranslate.data.model.TranslationVariations;

public class TranslationVariationsRequest {
    private HttpURLConnection urlConnection = null;

    @Nullable
    public TranslationVariations perform(String translationDirection, String textToLookup) throws JSONException {
        URL url;
        TranslationVariations result = null;
        try {
            url = new URL(buildRequest(translationDirection, textToLookup));
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String responseJson = Util.readStream(in);
            result = getResultFromResponseJson(responseJson);
        } catch (IOException ignored) {
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return result;
    }

    public void cancel() {
        if (urlConnection != null)
            urlConnection.disconnect();
    }

    private TranslationVariations getResultFromResponseJson(String responseJson) throws JSONException {
        return new TranslationVariations(responseJson);
    }

    /*
    https://dictionary.yandex.net/api/v1/dicservice.json/lookup ?
    key=<API-ключ>
     & lang=<направление перевода>
     & text=<переводимый текст>
     & flags=<опции поиска (битовая маска флагов)>
     */
    private String buildRequest(String translationDirection, String textToLookup) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("dictionary.yandex.net")
                .appendPath("api")
                .appendPath("v1")
                .appendPath("dicservice.json")
                .appendPath("lookup")
                .appendQueryParameter("key", Config.dictKey)
                .appendQueryParameter("text", textToLookup)
                .appendQueryParameter("lang", translationDirection)
                .appendQueryParameter("flags", String.valueOf(100)); // включает поиск по форме слова
        return builder.build().toString();
    }
}
