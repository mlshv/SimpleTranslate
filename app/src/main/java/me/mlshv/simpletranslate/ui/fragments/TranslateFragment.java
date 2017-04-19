package me.mlshv.simpletranslate.ui.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.data.db.DbManager;
import me.mlshv.simpletranslate.data.model.Translation;
import me.mlshv.simpletranslate.data.model.TranslationVariations;
import me.mlshv.simpletranslate.network.TranslationRequest;
import me.mlshv.simpletranslate.network.TranslationVariationsRequest;
import me.mlshv.simpletranslate.ui.views.TranslateInput;
import me.mlshv.simpletranslate.ui.views.TranslationVariationsView;
import me.mlshv.simpletranslate.util.SpHelper;

public class TranslateFragment extends Fragment {
    private RelativeLayout rootLayout;
    private TextView sourceLangLabel;
    private TextView targetLangLabel;
    private TranslateInput translateInput;
    private ProgressBar translationProgress;
    private TextView translationResultTextView;
    private DbManager dbManager;

    private boolean textBeingEdited = false;
    private boolean translationTaskCompleted = false;
    private Translation currentVisibleTranslation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        initView(view);
        initFragment();
        return view;
    }

    private void initView(View view) {
        Button changeLangButton = (Button) view.findViewById(R.id.change_language_button);
        changeLangButton.setOnClickListener(changeLangButtonOnClickListener);
        rootLayout = (RelativeLayout) view.findViewById(R.id.root);
        sourceLangLabel = (TextView) view.findViewById(R.id.source_lang);
        targetLangLabel = (TextView) view.findViewById(R.id.target_lang);
        translateInput = (TranslateInput) view.findViewById(R.id.translate_input);
        translateInput.addTextChangedListener(translateInputWatcher);
        translateInput.setOnFocusChangeListener(translateInputOnFocusChangeListener);
        translateInput.setOnEditorActionListener(translateInputDoneButtonListener);
        translateInput.setOnBackButtonPressListener(new Callable<Void>() {
            @Override public Void call() throws Exception {
                TranslateFragment.this.notifyTranslationResultStateChanged();
                return null;
            }
        });
        // ставим кнопку "готово" на клаве вместо кнопки новой строки
        translateInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        translateInput.setRawInputType(InputType.TYPE_CLASS_TEXT);
        translationProgress = (ProgressBar) view.findViewById(R.id.translation_progress);
        translationResultTextView = (TextView) view.findViewById(R.id.translation_result_text);
        translationResultTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipboardCopy(((TextView) view).getText().toString());
                showToast("Перевод скопирован в буфер обмена");
            }
        });
    }

    private void initFragment() {
        String source = SpHelper.getSourceLangCode();
        String target = SpHelper.getTargetLangCode();
        setLanguages(source, target);
        dbManager = new DbManager(App.getInstance());
        if (currentVisibleTranslation != null) {
            renderTranslation(currentVisibleTranslation);
        }
    }

    private void setLanguages(String source, String target) {
        String sourceName = getResources().getString(langCodeToResourceId.get(source));
        String targetName = getResources().getString(langCodeToResourceId.get(target));
        sourceLangLabel.setText(sourceName);
        targetLangLabel.setText(targetName);
        SpHelper.saveSourceTargetLangs(source, target);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Log.d(App.tag(this), "onHiddenChanged: currentVisibleTranslation " + String.valueOf(currentVisibleTranslation));
            if (currentVisibleTranslation != null) {
                setLanguages(currentVisibleTranslation.getTermLang(), currentVisibleTranslation.getTranslationLang());
                this.translateInput.setText(currentVisibleTranslation.getTerm());
                renderTranslation(currentVisibleTranslation);
            }
        }
    }

    private View.OnClickListener changeLangButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // меняем местами source и target
            String source = SpHelper.getTargetLangCode();
            String target = SpHelper.getSourceLangCode();
            setLanguages(source, target);
            Log.d(App.tag(this), "Changed language");
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
        private TranslationTask translationTask;
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // отменяем предыдущий перевод-таск, если есть, и запускаем новый
            Log.d(App.tag(this), "onTextChanged: пользователь вводит текст");
            if (translationTask != null) translationTask.cancel(false);
            translationTask = new TranslationTask();
            translationTask.execute(translateInput.getText().toString());
            textBeingEdited = true;
        }

        @Override public void afterTextChanged(Editable s) {}
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    };

    private void notifyTranslationResultStateChanged() {
        if (currentVisibleTranslation == null ||
                currentVisibleTranslation.getTranslation() == null) return;
        if (!textBeingEdited && translationTaskCompleted &&
                !currentVisibleTranslation.getTranslation().trim().equals("")) {
            currentVisibleTranslation.addStoreOption(Translation.SAVED_HISTORY);
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
            translationProgress.setVisibility(View.VISIBLE);
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
            String source = SpHelper.getSourceLangCode();
            String target = SpHelper.getTargetLangCode();
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

    private void renderTranslation(Translation translation) {
        translationResultTextView.setText(translation.getTranslation());
        Log.d(App.tag(this), translation.toString());
        if (translation.getVariations() != null) {
            Log.d(App.tag(this), "Варианты: " + translation.getVariations().toString());
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.BELOW, R.id.translation_result_text);
        TranslationVariationsView variationsView =
                new TranslationVariationsView(this.getActivity(), translation.getVariations());
        variationsView.setLayoutParams(params);

        rootLayout.addView(variationsView);
    }

    public final Map<String, Integer> langCodeToResourceId = new HashMap<String, Integer>() {{
        put("ru", R.string.russian);
        put("en", R.string.english);
    }};

    public void setVisibleTranslation(Translation translation) {
        currentVisibleTranslation = translation;
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
    }

    private void clipboardCopy(String string) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(string, string);
        clipboard.setPrimaryClip(clip);
    }

    private void showToast(String string) {
        Toast.makeText(TranslateFragment.this.getContext(), string, Toast.LENGTH_SHORT).show();
    }
}
