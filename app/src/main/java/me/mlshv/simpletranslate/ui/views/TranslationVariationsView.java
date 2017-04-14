package me.mlshv.simpletranslate.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.Map;

import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.data.model.TranslationVariations;


public class TranslationVariationsView extends ScrollView {

    public TranslationVariationsView(Context context, TranslationVariations variations) {
        super(context);
        initView(context, variations);
    }

    private void initView(Context context, TranslationVariations variations) {
        LinearLayout variationsContainer = new LinearLayout(context);
        variationsContainer.setOrientation(LinearLayout.VERTICAL);

        // параметры для текстовых view
        LinearLayout.LayoutParams paramsFirstLevel = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsSecondLevel = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // устанавливаем отступы
        int marginStartFirstLevel = 13;
        int marginStartSecondLevel = marginStartFirstLevel * 2;
        marginStartFirstLevel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, marginStartFirstLevel, getResources()
                        .getDisplayMetrics());
        marginStartSecondLevel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, marginStartSecondLevel, getResources()
                        .getDisplayMetrics());

        paramsFirstLevel.setMarginStart(marginStartFirstLevel);
        paramsSecondLevel.setMarginStart(marginStartSecondLevel);

        LinkedHashMap<String, LinkedHashMap<String, String>> vMap = variations.getMap();
        for (String variation : vMap.keySet()) {
            TextView variationView = new TextView(context);
            variationView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            variationView.setText(variation);
            variationView.setLayoutParams(paramsFirstLevel);
            variationsContainer.addView(variationView);
            for (Map.Entry<String, String> translationMeaning : vMap.get(variation).entrySet()) {
                TextView translationView = new TextView(context);
                TextView meaningView = new TextView(context);
                translationView.setText(translationMeaning.getKey());
                meaningView.setText(translationMeaning.getValue());
                translationView.setLayoutParams(paramsSecondLevel);
                meaningView.setLayoutParams(paramsSecondLevel);
                translationView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                meaningView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                meaningView.setTypeface(null, Typeface.ITALIC);
                int secondaryTextColor = ContextCompat.getColor(context, R.color.colorTextSecondary);
                meaningView.setTextColor(secondaryTextColor);
                variationsContainer.addView(translationView);
                variationsContainer.addView(meaningView);
            }
        }
        this.addView(variationsContainer);
    }
}
