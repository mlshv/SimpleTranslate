package me.mlshv.simpletranslate.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Map;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.data.model.TranslationVariations;


public class TranslationVariationsView extends ScrollView {
    // значения отступов в пикселях
    private static final int FIRST_LEVEL_MARGIN = getDimen(R.dimen.first_level_margin);
    private static final int SECOND_LEVEL_MARGIN = getDimen(R.dimen.second_level_text_margin);
    // размер текста в sp
    private static final int FIRST_LEVEL_TEXT_SIZE = 24;
    private static final int SECOND_LEVEL_TEXT_SIZE = 20;
    private static final int THIRD_LEVEL_TEXT_SIZE = 18;

    public TranslationVariationsView(Context context, TranslationVariations variations) {
        super(context);
        initView(variations);
        this.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackground));
    }

    private void initView(TranslationVariations variations) {
        LinearLayout llVariationsContainer = new LinearLayout(getContext());
        llVariationsContainer.setOrientation(LinearLayout.VERTICAL);

        // параметры для текстовых view
        LinearLayout.LayoutParams paramsFirstLevel = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsSecondLevel = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // устанавливаем отступы
        paramsFirstLevel.setMarginStart(FIRST_LEVEL_MARGIN);
        paramsSecondLevel.setMarginStart(SECOND_LEVEL_MARGIN);

        Map<String, Map<String, String>> vMap = variations.getAsMap();
        for (String variation : vMap.keySet()) {
            TextView tvVariation = new TextView(getContext());
            tvVariation.setTextSize(TypedValue.COMPLEX_UNIT_SP, FIRST_LEVEL_TEXT_SIZE);
            tvVariation.setText(variation);
            tvVariation.setLayoutParams(paramsFirstLevel);
            llVariationsContainer.addView(tvVariation);
            for (Map.Entry<String, String> translationMeaning : vMap.get(variation).entrySet()) {
                TextView tvTranslation = new TextView(getContext());
                tvTranslation.setText(translationMeaning.getKey());
                tvTranslation.setLayoutParams(paramsSecondLevel);
                tvTranslation.setTextSize(TypedValue.COMPLEX_UNIT_SP, SECOND_LEVEL_TEXT_SIZE);
                int secondaryTextColor = ContextCompat.getColor(getContext(), R.color.colorTextSecondary);
                llVariationsContainer.addView(tvTranslation);
                if (!translationMeaning.getValue().isEmpty()) {
                    TextView tvMeaning = new TextView(getContext());
                    tvMeaning.setText(translationMeaning.getValue());
                    tvMeaning.setLayoutParams(paramsSecondLevel);
                    tvMeaning.setTextSize(TypedValue.COMPLEX_UNIT_SP, THIRD_LEVEL_TEXT_SIZE);
                    tvMeaning.setTypeface(null, Typeface.ITALIC);
                    tvMeaning.setTextColor(secondaryTextColor);
                    llVariationsContainer.addView(tvMeaning);
                }
            }
        }
        if (!variations.isEmpty())
            llVariationsContainer.addView(makeApiNoticeTextView());
        this.addView(llVariationsContainer);
    }

    /**
     * Возвращает значение отступа в пикселях
     * @param resourceId id ресурса отступа
     * @return значение отступа
     */
    private static int getDimen(int resourceId) {
        return (int) (App.getInstance().getResources().getDimension(resourceId));
    }

    private TextView makeApiNoticeTextView() {
        TextView tv = new TextView(getContext());
        tv.setText(getContext().getString(R.string.dict_api_notice));
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://tech.yandex.ru/dictionary/"));
                getContext().startActivity(browserIntent);
            }
        });
        tv.setPadding(
                getDimen(R.dimen.horizontal_padding),
                getDimen(R.dimen.vertical_padding),
                getDimen(R.dimen.horizontal_padding),
                getDimen(R.dimen.vertical_padding));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, SECOND_LEVEL_TEXT_SIZE);
        tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        return tv;
    }

    public TranslationVariationsView(Context context) {
        super(context);
    }

    public TranslationVariationsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TranslationVariationsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
