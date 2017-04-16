package me.mlshv.simpletranslate.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import java.util.concurrent.Callable;


public class TranslateInput extends android.support.v7.widget.AppCompatEditText {
    Callable<Void> backButtonPressListener;

    public TranslateInput(Context context) {
        super(context);
    }

    public TranslateInput(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TranslateInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Пользователь нажал кнопку "назад", вызываем функцию-listener
            if (backButtonPressListener != null) {
                try {
                    backButtonPressListener.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public void setOnBackButtonPressListener(Callable<Void> listener) {
        this.backButtonPressListener = listener;
    }
}
