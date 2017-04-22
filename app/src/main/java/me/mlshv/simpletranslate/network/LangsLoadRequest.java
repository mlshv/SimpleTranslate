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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.mlshv.simpletranslate.data.model.Lang;

public class LangsLoadRequest {
    private HttpURLConnection urlConnection = null;

    @Nullable
    public List<Lang> perform() {
        URL url;
        List<Lang> result = null;
        try {
            url = new URL(buildRequest());
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

    private List<Lang> getResultFromResponseJson(String jsonResponse) throws JSONException {
        List<Lang> langs = new ArrayList<>();
        JSONObject jObject = new JSONObject(jsonResponse);
        JSONObject langsMap = jObject.getJSONObject("langs");
        Iterator<String> keysIterator = langsMap.keys();
        while (keysIterator.hasNext()) {
            String langCode = keysIterator.next();
            String langName = langsMap.getString(langCode);
            langs.add(new Lang(langCode, langName));
        }
        return langs;
    }

    /*
    https://translate.yandex.net/api/v1.5/tr.json/getLangs ?
    key=<API-ключ>
     & [ui=<код языка>]
     & [callback=<имя callback-функции>]
     */
    private String buildRequest() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("translate.yandex.net")
                .appendPath("api")
                .appendPath("v1.5")
                .appendPath("tr.json")
                .appendPath("getLangs")
                .appendQueryParameter("key", Config.trnslKey)
                .appendQueryParameter("ui", "ru");
        return builder.build().toString();
    }
}
