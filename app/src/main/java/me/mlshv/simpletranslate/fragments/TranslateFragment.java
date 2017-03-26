package me.mlshv.simpletranslate.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.network.TranslationRequest;
import me.mlshv.simpletranslate.util.SpHelper;

public class TranslateFragment extends Fragment {
    private static final String TAG = "TranslateFragment";
    private TextView sourceLangLabel;
    private TextView targetLangLabel;
    private EditText translateInput;
    private Button translateButton;
    private TextView translationResultTextView;

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
        sourceLangLabel = (TextView) view.findViewById(R.id.source_lang);
        targetLangLabel = (TextView) view.findViewById(R.id.target_lang);
        translateInput = (EditText) view.findViewById(R.id.translate_input);
        translateButton = (Button) view.findViewById(R.id.translate_button);
        translateButton.setOnClickListener(translateButtonOnClickListener);
        translationResultTextView = (TextView) view.findViewById(R.id.translation_result_text);
    }

    private void initFragment() {
        int source = SpHelper.getSourceLangId();
        int target = SpHelper.getTargetLangId();
        sourceLangLabel.setText(getString(source));
        targetLangLabel.setText(getString(target));
        SpHelper.saveSourceTargetLangIds(source, target);
    }

    private View.OnClickListener changeLangButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int source = SpHelper.getSourceLangId();
            int target = SpHelper.getTargetLangId();
            sourceLangLabel.setText(getString(target));
            targetLangLabel.setText(getString(source));
            SpHelper.saveSourceTargetLangIds(target, source);
            Log.d(TAG, "Changed language");
        }
    };

    private View.OnClickListener translateButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new TranslationTask().execute(translateInput.getText().toString());
        }
    };


    private class TranslationTask extends AsyncTask<Object, String, String> {
        @Override
        protected void onPreExecute() {
            translateButton.setEnabled(false);
        }

        @Nullable
        @Override
        protected String doInBackground(Object... params) {
            Log.d(TAG, "TranslationTask started");
            String textToTranslate = (String) params[0];
            return new TranslationRequest().getTranslation(getTranslationDirectionCodes(), textToTranslate);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                translationResultTextView.setText(result);
            } else {
                Toast.makeText(TranslateFragment.this.getContext(), "Can't translate", Toast.LENGTH_SHORT).show();
            }
            translateButton.setEnabled(true);
        }
    }

    private String getTranslationDirectionCodes() {
        String source = langResourceIdToLangCode.get(SpHelper.getSourceLangId());
        String target = langResourceIdToLangCode.get(SpHelper.getTargetLangId());
        Log.d(TAG, "getTranslationDirectionCodes: source " + source + " target " + target);
        return source + "-" + target;
    }

    private Map<Integer, String> langResourceIdToLangCode = new HashMap<Integer, String>() {{
        put(R.string.russian, "ru");
        put(R.string.english, "en");
    }};
}
