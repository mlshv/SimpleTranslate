package me.mlshv.simpletranslate.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import me.mlshv.simpletranslate.App;


public class TranslationVariations {
    /* стуктура:
        {
          слово:
            { первод: значение, ... },
          ...
        }
    */
    private final LinkedHashMap<String, LinkedHashMap<String, String>> dictPage = new LinkedHashMap<>();
    private final String jsonData;

    public TranslationVariations(String responseJson) {
        jsonData = responseJson;
        try {
            buildFromJson(jsonData);
        } catch (JSONException e) {
            Log.e(App.tag(this), "TranslationVariations: исключения при парсинге JSON", e);
        }
    }

    public String getJson() {
        return jsonData;
    }

    private void buildFromJson(String json) throws JSONException {
        JSONObject jObject = new JSONObject(json);
        JSONArray definitions = jObject.getJSONArray("def");
        for (int i = 0; i < definitions.length(); i++) {
            JSONObject definition = definitions.getJSONObject(i);
            String text = definition.getString("text");
            JSONArray translations = definition.getJSONArray("tr");
            LinkedHashMap<String, String> translationStrings = new LinkedHashMap<>();
            for (int j = 0; j < translations.length(); j++) {
                JSONObject translation = translations.getJSONObject(j);
                String translationString = translation.getString("text");
                // берём первое значение переведённого слова, чтобы не наставить лишних запятых
                String meaning = translation.getJSONArray("mean").getJSONObject(0).getString("text");

                try {
                    JSONArray synonyms = translation.getJSONArray("syn");
                    for (int k = 0; k < synonyms.length(); k++) {
                        translationString += ", " + synonyms.getJSONObject(k).getString("text");
                    }
                } catch (JSONException ignored) {} // синонимов нет, можно игнорировать

                JSONArray meanings = translation.getJSONArray("mean");
                for (int k = 1; k < meanings.length(); k++) {
                    meaning += ", " + meanings.getJSONObject(k).getString("text");
                }
                translationStrings.put(translationString, meaning);
            }
            dictPage.put(text, translationStrings);
        }
    }

    public LinkedHashMap<String, LinkedHashMap<String, String>> getMap() {
        return dictPage;
    }

    public boolean isEmpty() {
        return dictPage.isEmpty();
    }

    @Override
    public String toString() {
        return dictPage.toString();
    }
}
