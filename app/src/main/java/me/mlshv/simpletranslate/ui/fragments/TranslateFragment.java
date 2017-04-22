package me.mlshv.simpletranslate.ui.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Callable;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.data.db.DbManager;
import me.mlshv.simpletranslate.data.model.Translation;
import me.mlshv.simpletranslate.network.TranslationTask;
import me.mlshv.simpletranslate.ui.activities.LangChangeActivity;
import me.mlshv.simpletranslate.ui.widgets.TranslateInput;
import me.mlshv.simpletranslate.ui.widgets.TranslationVariationsView;
import me.mlshv.simpletranslate.util.LangUtil;
import me.mlshv.simpletranslate.util.SpHelper;

public class TranslateFragment extends Fragment {
    private TextView sourceLangLabel;
    private TextView targetLangLabel;
    private TranslateInput textInput;
    private TextView translationResultTextView;
    private DbManager dbManager;

    private LinearLayout variationsContainer;
    private ProgressBar translationProgress;
    private CheckBox favoriteCheckbox;
    private Button copyButton;
    private TextView apiNoticeText;

    private TranslationTask translationTask;
    private Translation currentVisibleTranslation;

    private static final int REQUEST_CHANGE_SOURCE_LANG = 0;
    private static final int REQUEST_CHANGE_TARGET_LANG = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        initUi(view);
        initListeners(view);

        dbManager = new DbManager(App.getInstance());
        dbManager.open();

        return view;
    }

    private void initUi(View view) {
        variationsContainer = (LinearLayout) view.findViewById(R.id.variations_container);
        sourceLangLabel = (TextView) view.findViewById(R.id.source_lang);
        targetLangLabel = (TextView) view.findViewById(R.id.target_lang);
        textInput = (TranslateInput) view.findViewById(R.id.translate_input);
        // ставим кнопку "готово" на клаве вместо кнопки новой строки и делаем её не fullscreen, убираем подсказки
        // почему-то через XML не ставится
        textInput.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        textInput.setRawInputType(InputType.TYPE_CLASS_TEXT);
        translationResultTextView = (TextView) view.findViewById(R.id.translation_result_text);
        translationProgress = (ProgressBar) view.findViewById(R.id.translation_progress);
        favoriteCheckbox = (CheckBox) view.findViewById(R.id.favorite_checkbox);
        copyButton = (Button) view.findViewById(R.id.translation_copy_button);
        apiNoticeText = (TextView) view.findViewById(R.id.yandex_api_notice);
    }

    private void initListeners(View view) {
        view.findViewById(R.id.root).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    saveTranslationToHistory();
                }
            }
        });
        Button changeLangButton = (Button) view.findViewById(R.id.change_language_button);
        changeLangButton.setOnClickListener(changeLangButtonOnClickListener);
        sourceLangLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseLanguage(REQUEST_CHANGE_SOURCE_LANG);
            }
        });
        targetLangLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseLanguage(REQUEST_CHANGE_TARGET_LANG);
            }
        });
        textInput.addTextChangedListener(translateInputWatcher);
        textInput.setOnFocusChangeListener(translateInputOnFocusChangeListener);
        textInput.setOnEditorActionListener(translateInputDoneButtonListener);
        textInput.setOnBackButtonPressListener(new Callable<Void>() {
            @Override public Void call() throws Exception {
                textInput.clearFocus();
                return null;
            }
        });
        translationResultTextView.setMovementMethod(new ScrollingMovementMethod());
        favoriteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    currentVisibleTranslation.addStoreOption(Translation.SAVED_FAVORITES);
                } else {
                    currentVisibleTranslation.removeStoreOption(Translation.SAVED_FAVORITES);
                }
                dbManager.insertTranslation(currentVisibleTranslation);
            }
        });
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipboardCopy(translationResultTextView.getText().toString());
                showToast("Перевод скопирован");
            }
        });
        Button translationClearButton = (Button) view.findViewById(R.id.input_clear_button);
        translationClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textInput.setText("");
                SpHelper.saveCurrentTextToTranslate("");
            }
        });
        apiNoticeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://translate.yandex.ru"));
                startActivity(browserIntent);
            }
        });
    }

    private void setSourceLangCode(String langCode) {
        String langName = LangUtil.getNameByCode(langCode);
        sourceLangLabel.setText(langName);
        SpHelper.saveSourceLangCode(langCode);
    }

    private void setTargetLangCode(String langCode) {
        String langName = LangUtil.getNameByCode(langCode);
        targetLangLabel.setText(langName);
        SpHelper.saveTargetLangCode(langCode);
    }

    private void chooseLanguage(int requestLangCode) {
        Intent intent = new Intent(getContext(), LangChangeActivity.class);
        startActivityForResult(intent, requestLangCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return; // пользователь нажал кнопку "назад" при выборе языка, игнорим
        String langName = data.getStringExtra("language");
        switch (requestCode) {
            case REQUEST_CHANGE_SOURCE_LANG:
                setSourceLangCode(LangUtil.getCodeByName(langName));
                break;
            case REQUEST_CHANGE_TARGET_LANG:
                setTargetLangCode(LangUtil.getCodeByName(langName));
                break;
        }
    }

    private View.OnClickListener changeLangButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // меняем местами source и target
            String newSourceLangCode = SpHelper.loadTargetLangCode();
            String newTargetLangCode = SpHelper.loadSourceLangCode();
            setSourceLangCode(newSourceLangCode);
            setTargetLangCode(newTargetLangCode);
            performTranslationTask();
        }
    };

    private View.OnFocusChangeListener translateInputOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (!hasFocus) {
                // говорим, что пользователь закончил ввод
                // убираем фокус с textInput'a (фокус переходи на корневой layout, где стоит listener)
                textInput.clearFocus();
            }
        }
    };

    private EditText.OnEditorActionListener translateInputDoneButtonListener =
            new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                textInput.clearFocus();
            }
            return false;
        }
    };

    private TextWatcher translateInputWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(App.tag(this), "onTextChanged: пользователь вводит текст");
            performTranslationTask();
        }

        @Override public void afterTextChanged(Editable s) {}
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    };

    private void performTranslationTask() {
        // отменяем предыдущий перевод-таск, если есть, и запускаем новый
        if (translationTask != null) translationTask.cancel(true);
        translationTask = new TranslationTask(onTranslationResultCallback);
        // показываем анимацию загрузки и прячем кнопки
        translationProgress.setVisibility(View.VISIBLE);
        favoriteCheckbox.setVisibility(View.INVISIBLE);
        copyButton.setVisibility(View.INVISIBLE);
        translationTask.execute(textInput.getText().toString());
    }

    private Callable<Void> onTranslationResultCallback = new Callable<Void>() {
        @Override
        public Void call() throws Exception {
            translationProgress.setVisibility(View.GONE);
            Translation result = translationTask.get();
            if (result != null) {
                renderTranslation(result);
                currentVisibleTranslation = result;
                saveTranslationToHistory();
            } else {
                showToast("Не получилось перевести");
            }
            return null;
        }
    };

    private void saveTranslationToHistory() {
        if (currentVisibleTranslation == null || currentVisibleTranslation.getTranslation() == null) return;
        Log.d(App.tag(this), "saveTranslationToHistory: translationTask.getStatus() == " + translationTask.getStatus());
        if (!textInput.hasFocus() || translationTask.getStatus() == AsyncTask.Status.FINISHED) {
            if (!currentVisibleTranslation.getTranslation().trim().isEmpty()
                    && !currentVisibleTranslation.getTerm().equals(currentVisibleTranslation.getTranslation())) {
                currentVisibleTranslation.addStoreOption(Translation.SAVED_HISTORY);
                if (dbManager != null) {
                    Log.d(App.tag(this), "saveTranslationToHistory: обновляем translation " + currentVisibleTranslation);
                    dbManager.updateOrInsertTranslation(currentVisibleTranslation);
                    SpHelper.saveCurrentTextToTranslate(currentVisibleTranslation.getTerm());
                }
            }
        }
    }

    private void renderTranslation(Translation translation) {
        translationResultTextView.setText(translation.getTranslation());

        if (translation.getVariations() != null) {
            Log.d(App.tag(this), "Варианты: " + translation.getVariations().toString());

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TranslationVariationsView variationsView =
                    new TranslationVariationsView(this.getActivity(), translation.getVariations());
            variationsView.setLayoutParams(params);

            variationsContainer.removeAllViews();
            variationsContainer.addView(variationsView);
        }

        if (!translation.getTerm().isEmpty()) {
            // показываем кнопки "Избранное" и "Скопировать" а также сообщение об использовании API
            copyButton.setVisibility(View.VISIBLE);
            favoriteCheckbox.setVisibility(View.VISIBLE);
            favoriteCheckbox.setChecked(translation.hasOption(Translation.SAVED_FAVORITES));
            apiNoticeText.setVisibility(View.VISIBLE);
        } else {
            apiNoticeText.setVisibility(View.GONE);
        }
    }

    private void clipboardCopy(String string) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(string, string);
        clipboard.setPrimaryClip(clip);
    }

    private void showToast(String string) {
        Toast.makeText(TranslateFragment.this.getContext(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // обновляем направление перевода и ставим в поле ввода последний переводимый текст
        String source = SpHelper.loadSourceLangCode();
        String target = SpHelper.loadTargetLangCode();
        setSourceLangCode(source);
        setTargetLangCode(target);
        textInput.setText(SpHelper.loadCurrentTextToTranslate());
        dbManager.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        dbManager.close();
        if (translationTask != null)
            translationTask.cancel(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager = null;
        translationTask = null;
    }
}
