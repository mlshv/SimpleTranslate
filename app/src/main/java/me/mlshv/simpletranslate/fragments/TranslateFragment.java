package me.mlshv.simpletranslate.fragments;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.db.DbHelper;
import me.mlshv.simpletranslate.db.DbManager;
import me.mlshv.simpletranslate.model.Translation;
import me.mlshv.simpletranslate.model.TranslationVariations;
import me.mlshv.simpletranslate.network.TranslationRequest;
import me.mlshv.simpletranslate.network.TranslationVariationsRequest;
import me.mlshv.simpletranslate.util.SpHelper;

public class TranslateFragment extends Fragment {
    private RelativeLayout rootLayout;
    private TextView sourceLangLabel;
    private TextView targetLangLabel;
    private EditText translateInput;
    private TextView translationResultTextView;
    private DbManager dbManager;

    private boolean textBeingEdited = false;
    private boolean translationTaskCompleted = false;
    private Translation lastTranslationResult;


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
        translateInput = (EditText) view.findViewById(R.id.translate_input);
        translateInput.addTextChangedListener(translateInputWatcher);
        translateInput.setOnFocusChangeListener(translateInputOnFocusChangeListener);
        // ставим кнопку "готово" на клаве вместо кнопки новой строки
        translateInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        translateInput.setRawInputType(InputType.TYPE_CLASS_TEXT);
        translationResultTextView = (TextView) view.findViewById(R.id.translation_result_text);
    }

    private void initFragment() {
        int source = SpHelper.getSourceLangId();
        int target = SpHelper.getTargetLangId();
        sourceLangLabel.setText(getString(source));
        targetLangLabel.setText(getString(target));
        SpHelper.saveSourceTargetLangIds(source, target);
        dbManager = new DbManager(App.getInstance());
    }

    private View.OnClickListener changeLangButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int source = SpHelper.getSourceLangId();
            int target = SpHelper.getTargetLangId();
            sourceLangLabel.setText(getString(target));
            targetLangLabel.setText(getString(source));
            SpHelper.saveSourceTargetLangIds(target, source);
            Log.d(App.tag(this), "Changed language");
        }
    };

    private View.OnFocusChangeListener translateInputOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (!hasFocus) {
                // говорим, что пользователь закончил ввод
                // это значит, что можно сохранять перевод в кэш
                textBeingEdited = false;
                notifyTranslationResultStateChanged();
            }
        }
    };

    private TextWatcher translateInputWatcher = new TextWatcher() {
        private TranslationTask translationTask;
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // отменяем предыдущий перевод-таск, если есть, и запускаем новый
            if (translationTask != null) translationTask.cancel(false);
            translationTask = new TranslationTask();
            translationTask.execute(translateInput.getText().toString());
            textBeingEdited = true;
        }

        @Override public void afterTextChanged(Editable s) {}
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    };

    private void notifyTranslationResultStateChanged() {
        if (!textBeingEdited && translationTaskCompleted &&
                !lastTranslationResult.getTerm().trim().equals("")) {
            dbManager.open();
            dbManager.saveTranslation(lastTranslationResult, DbHelper.HISTORY_TABLE);
            dbManager.close();
        }
    }


    private class TranslationTask extends AsyncTask<Object, String, Translation> {
        private String textToTranslate;
        private TranslationRequest translationRequest;
        private TranslationVariationsRequest variationsRequest;

        @Override
        protected void onPreExecute() {
            Log.d(App.tag(this), "Starting TranslationTask");
            translationTaskCompleted = false;
        }

        @Override
        protected Translation doInBackground(Object... params) {
            Log.d(App.tag(this), "TranslationTask started");
            textToTranslate = (String) params[0];
            translationRequest = new TranslationRequest();
            variationsRequest = new TranslationVariationsRequest();
            String result = translationRequest.getTranslation(getTranslationDirectionCodes(), textToTranslate);
            TranslationVariations variations = variationsRequest.getVariations(getTranslationDirectionCodes(), textToTranslate);
            return new Translation(textToTranslate, result, variations);
        }

        @Override
        protected void onPostExecute(Translation result) {
            if (result != null) {
                renderTranslation(result);
                translationTaskCompleted = true;
                lastTranslationResult = result;
                notifyTranslationResultStateChanged();
            } else {
                Toast.makeText(TranslateFragment.this.getContext(), "Не получилось перевести", Toast.LENGTH_SHORT).show();
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
        Log.d(App.tag(this), "TranslationTask finished.");

        translationResultTextView.setText(translation.getTranslation());
        Log.d(App.tag(this), translation + " => " + translation.getTranslation());
        if (translation.getVariations() != null) {
            Log.d(App.tag(this), "Variations: " + translation.getVariations().toString());
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.BELOW, R.id.translation_result_text);
        ScrollView variationsView = renderVariationsView(translation.getVariations());
        variationsView.setLayoutParams(params);

        rootLayout.addView(variationsView);
    }

    private ScrollView renderVariationsView(TranslationVariations variations) {

        ScrollView resultView = new ScrollView(this.getActivity());
        LinearLayout container = new LinearLayout(this.getActivity());
        container.setOrientation(LinearLayout.VERTICAL);

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
            TextView variationView = new TextView(getActivity());
            variationView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            variationView.setText(variation);
            variationView.setLayoutParams(paramsFirstLevel);
            container.addView(variationView);
            for (Map.Entry<String, String> translationMeaning : vMap.get(variation).entrySet()) {
                TextView translationView = new TextView(getActivity());
                TextView meaningView = new TextView(getActivity());
                translationView.setText(translationMeaning.getKey());
                meaningView.setText(translationMeaning.getValue());
                translationView.setLayoutParams(paramsSecondLevel);
                meaningView.setLayoutParams(paramsSecondLevel);
                translationView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                meaningView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                meaningView.setTypeface(null, Typeface.ITALIC);
                int secondaryTextColor = ContextCompat.getColor(this.getActivity(), R.color.colorTextSecondary);
                meaningView.setTextColor(secondaryTextColor);
                container.addView(translationView);
                container.addView(meaningView);
            }
        }
        resultView.addView(container);
        return resultView;
    }

    private String getTranslationDirectionCodes() {
        String source = langResourceIdToLangCode.get(SpHelper.getSourceLangId());
        String target = langResourceIdToLangCode.get(SpHelper.getTargetLangId());
        Log.d(App.tag(this), "getTranslationDirectionCodes: source " + source + " target " + target);
        return source + "-" + target;
    }

    private Map<Integer, String> langResourceIdToLangCode = new HashMap<Integer, String>() {{
        put(R.string.russian, "ru");
        put(R.string.english, "en");
    }};
}
