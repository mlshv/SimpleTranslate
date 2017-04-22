package me.mlshv.simpletranslate.ui.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import me.mlshv.simpletranslate.data.model.TranslationVariations;
import me.mlshv.simpletranslate.network.TranslationRequest;
import me.mlshv.simpletranslate.network.TranslationVariationsRequest;
import me.mlshv.simpletranslate.ui.activities.LangChangeActivity;
import me.mlshv.simpletranslate.ui.views.TranslateInput;
import me.mlshv.simpletranslate.ui.views.TranslationVariationsView;
import me.mlshv.simpletranslate.util.LangUtil;
import me.mlshv.simpletranslate.util.SpHelper;

public class TranslateFragment extends Fragment implements PageableFragment {
    private TextView sourceLangLabel;
    private TextView targetLangLabel;
    private TranslateInput textInput;
    private TextView translationResultTextView;
    private DbManager dbManager;

    private LinearLayout variationsContainer;
    private ProgressBar translationProgress;
    private CheckBox favoriteCheckbox;
    private Button copyButton;

    private TranslationTask translationTask;
    private boolean textBeingEdited = false;
    private boolean translationTaskCompleted = false;
    private Translation currentVisibleTranslation;

    private static final int CHANGE_SOURCE_LANG = 0;
    private static final int CHANGE_TARGET_LANG = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        initView(view);
        initFragment(savedInstanceState);
        setRetainInstance(true);
        return view;
    }

    private void initView(View view) {
        Button changeLangButton = (Button) view.findViewById(R.id.change_language_button);
        changeLangButton.setOnClickListener(changeLangButtonOnClickListener);
        variationsContainer = (LinearLayout) view.findViewById(R.id.variations_container);
        sourceLangLabel = (TextView) view.findViewById(R.id.source_lang);
        sourceLangLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseLanguage(CHANGE_SOURCE_LANG);
            }
        });
        targetLangLabel = (TextView) view.findViewById(R.id.target_lang);
        targetLangLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseLanguage(CHANGE_TARGET_LANG);
            }
        });
        textInput = (TranslateInput) view.findViewById(R.id.translate_input);
        textInput.addTextChangedListener(translateInputWatcher);
        textInput.setOnFocusChangeListener(translateInputOnFocusChangeListener);
        textInput.setOnEditorActionListener(translateInputDoneButtonListener);
        textInput.setOnBackButtonPressListener(new Callable<Void>() {
            @Override public Void call() throws Exception {
                TranslateFragment.this.notifyTranslationResultStateChanged();
                return null;
            }
        });
        // ставим кнопку "готово" на клаве вместо кнопки новой строки и делаем её не fullscreen, убираем подсказки
        textInput.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        textInput.setRawInputType(InputType.TYPE_CLASS_TEXT);
        translationResultTextView = (TextView) view.findViewById(R.id.translation_result_text);
        translationResultTextView.setMovementMethod(new ScrollingMovementMethod());
        translationProgress = (ProgressBar) view.findViewById(R.id.translation_progress);
        favoriteCheckbox = (CheckBox) view.findViewById(R.id.favorite_checkbox);
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
        copyButton = (Button) view.findViewById(R.id.translation_copy_button);
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
            }
        });
    }

    private void initFragment(Bundle savedInstanceState) {
        String source = SpHelper.loadSourceLangCode();
        String target = SpHelper.loadTargetLangCode();
        setSourceLangCode(source);
        setTargetLangCode(target);
        dbManager = new DbManager(App.getInstance());
        dbManager.open();
        textInput.clearFocus();
        if (savedInstanceState != null) {
            currentVisibleTranslation =
                dbManager.tryGetFromCache(savedInstanceState.getString("SOURCE_STRING"));
        }

        if (currentVisibleTranslation != null) {
            textInput.setText(currentVisibleTranslation.getTerm());
            renderTranslation(currentVisibleTranslation);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentVisibleTranslation != null)
            outState.putString("SOURCE_STRING", currentVisibleTranslation.getTerm());
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
        String langName = data.getStringExtra("language");
        switch (requestCode) {
            case CHANGE_SOURCE_LANG:
                setSourceLangCode(LangUtil.getCodeByName(langName));
                break;
            case CHANGE_TARGET_LANG:
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
                // это значит, что можно сохранять перевод в историю
                textBeingEdited = false;
                notifyTranslationResultStateChanged();
            }
        }
    };

    private EditText.OnEditorActionListener translateInputDoneButtonListener =
            new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.d(App.tag(this), "onEditorAction: ввод закончен");
                textBeingEdited = false;
                notifyTranslationResultStateChanged();
            }
            return false;
        }
    };

    private TextWatcher translateInputWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(App.tag(this), "onTextChanged: пользователь вводит текст");
            performTranslationTask();
            textBeingEdited = true;
        }

        @Override public void afterTextChanged(Editable s) {}
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    };

    private void performTranslationTask() {
        // отменяем предыдущий перевод-таск, если есть, и запускаем новый
        if (translationTask != null) translationTask.cancel(false);
        translationTask = new TranslationTask();
        translationTask.execute(textInput.getText().toString());
    }

    private void notifyTranslationResultStateChanged() {
        if (currentVisibleTranslation == null ||
                currentVisibleTranslation.getTranslation() == null) return;
        if (!textBeingEdited && translationTaskCompleted &&
                !currentVisibleTranslation.getTranslation().trim().equals("")
                && !currentVisibleTranslation.getTerm().equals(currentVisibleTranslation.getTranslation())) {
            currentVisibleTranslation.addStoreOption(Translation.SAVED_HISTORY);
            if (dbManager != null)
                dbManager.updateOrInsertTranslation(currentVisibleTranslation);
        }
    }

    private class TranslationTask extends AsyncTask<Object, String, Translation> {
        private String textToTranslate;
        private TranslationRequest translationRequest;
        private TranslationVariationsRequest variationsRequest;

        @Override
        protected void onPreExecute() {
            Log.d(App.tag(this), "Запускаю TranslationTask");
            translationTaskCompleted = false;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    translationProgress.setVisibility(View.VISIBLE);
                    favoriteCheckbox.setVisibility(View.INVISIBLE);
                    copyButton.setVisibility(View.INVISIBLE);
                }
            });
        }

        @Override
        protected Translation doInBackground(Object... params) {
            Log.d(App.tag(this), "TranslationTask запущена");
            textToTranslate = (String) params[0];
            Translation t = dbManager.tryGetFromCache(textToTranslate);
            if (t != null) {
                Log.d(App.tag(this), "Достал из кэша " + t);
                return t;
            }
            String source = SpHelper.loadSourceLangCode();
            String target = SpHelper.loadTargetLangCode();
            translationRequest = new TranslationRequest();
            variationsRequest = new TranslationVariationsRequest();
            String result = translationRequest.getTranslation(source + "-" + target, textToTranslate);
            TranslationVariations variations = variationsRequest.getVariations(source + "-" + target, textToTranslate);
            t = new Translation(source, target, textToTranslate, result, variations);
            dbManager.updateOrInsertTranslation(t);
            return t;
        }

        @Override
        protected void onPostExecute(Translation result) {
            Log.d(App.tag(this), "TranslationTask завершена");
            translationProgress.setVisibility(View.GONE);
            if (result != null) {
                renderTranslation(result);
                translationTaskCompleted = true;
                currentVisibleTranslation = result;
                notifyTranslationResultStateChanged();
            } else {
                showToast("Не получилось перевести");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (translationRequest != null)
                translationRequest.cancel();
            if (variationsRequest != null)
                variationsRequest.cancel();
        }
    }

    private void renderTranslation(final Translation translation) {
        translationResultTextView.setText(
                String.format("%s\n%s",
                        translation.getTranslation(),
                        getResources().getString(R.string.translate_disclaimer)));
        translationResultTextView.setMovementMethod(LinkMovementMethod.getInstance());

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
            // показываем кнопки "Избранное" и "Скопировать"
            copyButton.setVisibility(View.VISIBLE);
            favoriteCheckbox.setVisibility(View.VISIBLE);
            favoriteCheckbox.setChecked(translation.hasOption(Translation.SAVED_FAVORITES));
        }
    }

    public void setVisibleTranslation(Translation translation) {
        currentVisibleTranslation = translation;
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
        dbManager.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        dbManager.close();
        if (translationTask != null)
            translationTask.cancel(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager = null;
        translationTask = null;
    }

    @Override
    public void notifyPaged() {
        Log.d(App.tag(this), "notifyPaged: currentVisibleTranslation " + String.valueOf(currentVisibleTranslation));
        if (currentVisibleTranslation != null) {
            setSourceLangCode(currentVisibleTranslation.getTermLang());
            setTargetLangCode(currentVisibleTranslation.getTranslationLang());
            this.textInput.setText(currentVisibleTranslation.getTerm());
            renderTranslation(currentVisibleTranslation);
        }
    }
}
