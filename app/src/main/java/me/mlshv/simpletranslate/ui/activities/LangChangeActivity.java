package me.mlshv.simpletranslate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.Util;

public class LangChangeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lang_change);

        final List<String> langNames = Util.Langs.getLangNames();
        ListView lvLanguages = (ListView) findViewById(R.id.list_languages);
        lvLanguages.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.row_languages_list, langNames));
        lvLanguages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                finishWithResult(langNames.get(position));
            }
        });
    }

    private void finishWithResult(final String language) {
        Log.d(App.tag(this), "finishWithResult: " + language);
        Intent intent = new Intent();
        intent.putExtra("language", language);
        setResult(RESULT_OK, intent);
        finish();
    }
}
