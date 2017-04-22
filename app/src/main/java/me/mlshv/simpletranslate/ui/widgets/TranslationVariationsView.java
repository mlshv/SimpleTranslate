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
        LinearLayout variationsContainer = new LinearLayout(getContext());
        variationsContainer.setOrientation(LinearLayout.VERTICAL);

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
            TextView variationView = new TextView(getContext());
            variationView.setTextSize(TypedValue.COMPLEX_UNIT_SP, FIRST_LEVEL_TEXT_SIZE);
            variationView.setText(variation);
            variationView.setLayoutParams(paramsFirstLevel);
            variationsContainer.addView(variationView);
            for (Map.Entry<String, String> translationMeaning : vMap.get(variation).entrySet()) {
                TextView translationView = new TextView(getContext());
                translationView.setText(translationMeaning.getKey());
                translationView.setLayoutParams(paramsSecondLevel);
                translationView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SECOND_LEVEL_TEXT_SIZE);
                int secondaryTextColor = ContextCompat.getColor(getContext(), R.color.colorTextSecondary);
                variationsContainer.addView(translationView);
                if (!translationMeaning.getValue().isEmpty()) {
                    TextView meaningView = new TextView(getContext());
                    meaningView.setText(translationMeaning.getValue());
                    meaningView.setLayoutParams(paramsSecondLevel);
                    meaningView.setTextSize(TypedValue.COMPLEX_UNIT_SP, THIRD_LEVEL_TEXT_SIZE);
                    meaningView.setTypeface(null, Typeface.ITALIC);
                    meaningView.setTextColor(secondaryTextColor);
                    variationsContainer.addView(meaningView);
                }
            }
        }
        if (!variations.isEmpty())
            variationsContainer.addView(makeApiNoticeTextView());
        this.addView(variationsContainer);
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
                getDimen(R.dimen.activity_horizontal_padding),
                getDimen(R.dimen.activity_vertical_padding),
                getDimen(R.dimen.activity_horizontal_padding),
                getDimen(R.dimen.activity_vertical_padding));
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
