package me.mlshv.simpletranslate.data.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import me.mlshv.simpletranslate.App;


public class TranslationVariations {
    /* стуктура:
        {
          слово:
            { первод: значение, ... },
          ...
        }
    */
    private Map<String, Map<String, String>> mapValue;
    private final String jsonData;

    public TranslationVariations(String responseJson) {
        jsonData = responseJson;
    }

    /**
     * Ленивое свойство
     */
    public Map<String, Map<String, String>> getAsMap() {
        if (mapValue == null && jsonData != null) {
            try {
                initMapValue();
            } catch (Exception e) {
                Log.e(App.tag(this), "TranslationVariations: исключение при парсинге JSON. " +
                        "Варианты перевода не инициализированы", e);
                Log.e(App.tag(this), "jsonData=" + jsonData);
            }
        }
        return mapValue;
    }

    public String getJson() {
        return jsonData;
    }

    private void initMapValue() throws JSONException {
        mapValue = new LinkedHashMap<>();
        Log.d(App.tag(this), "initMapValue: " + jsonData);
        JSONObject jObject = new JSONObject(jsonData);
        JSONArray definitions = jObject.getJSONArray("def");
        for (int i = 0; i < definitions.length(); i++) {
            JSONObject definition = definitions.getJSONObject(i);
            String text = definition.getString("text");
            text += ", " + definition.getString("pos");
            JSONArray translations = definition.getJSONArray("tr");
            Map<String, String> translationStrings = new LinkedHashMap<>();
            for (int j = 0; j < translations.length(); j++) {
                JSONObject translation = translations.getJSONObject(j);
                String translationString = translation.getString("text");
                try {
                    JSONArray synonyms = translation.getJSONArray("syn");
                    for (int k = 0; k < synonyms.length(); k++) {
                        translationString += ", " + synonyms.getJSONObject(k).getString("text");
                    }
                } catch (JSONException ignored) {} // синонимов нет, это нормально, можно игнорировать

                String meaning = "";
                try {
                    // берём первое значение переведённого слова, чтобы не наставить лишних запятых
                    meaning = translation.getJSONArray("mean").getJSONObject(0).getString("text");
                    JSONArray meanings = translation.getJSONArray("mean");
                    for (int k = 1; k < meanings.length(); k++) {
                        meaning += ", " + meanings.getJSONObject(k).getString("text");
                    }

                } catch (JSONException ignored) {} // часто у объекта перевода нет поля mean, это нормально, пропускаем
                translationStrings.put(translationString, meaning);
            }
            mapValue.put(text, translationStrings);
        }
    }

    public boolean isEmpty() {
        return getAsMap() == null || getAsMap().isEmpty();
    }

    @Override
    public String toString() {
        return String.valueOf(getAsMap());
    }
}
