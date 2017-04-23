package me.mlshv.simpletranslate.ui.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.mlshv.simpletranslate.R;

public class TranslationErrorView extends LinearLayout {

    private void init(String message, View.OnClickListener onRetryButtonClick) {
        inflate(getContext(), R.layout.view_translations_error, this);
        ((TextView) findViewById(R.id.error_message)).setText(message);
        if (onRetryButtonClick != null)
            addRetryButton(onRetryButtonClick);
    }

    public void addRetryButton(OnClickListener onRetryButtonClick) {
        String retryButtonText = getContext().getResources().getString(R.string.retry_button_text);
        Button retryButton = new Button(getContext());
        retryButton.setOnClickListener(onRetryButtonClick);
        retryButton.setText(retryButtonText);
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        buttonParams.addRule(RelativeLayout.BELOW, R.id.error_message);
        retryButton.setLayoutParams(buttonParams);
        ((RelativeLayout) findViewById(R.id.error_root)).addView(retryButton);
    }

    public TranslationErrorView(Context context, String message, @Nullable View.OnClickListener onRetryButtonClick) {
        super(context);
        init(message, onRetryButtonClick);
    }

    public TranslationErrorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init("No message", null);
    }

    public TranslationErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init("No message", null);
    }
}
