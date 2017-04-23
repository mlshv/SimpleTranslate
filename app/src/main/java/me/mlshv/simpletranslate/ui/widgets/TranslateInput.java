package me.mlshv.simpletranslate.ui.widgets;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import java.util.concurrent.Callable;

import me.mlshv.simpletranslate.App;


public class TranslateInput extends android.support.v7.widget.AppCompatEditText {

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
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        Log.d(App.tag(this), "setText: " + text);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Пользователь нажал кнопку "назад", снимаем фокус
            this.clearFocus();
        }
        return false;
    }
}
