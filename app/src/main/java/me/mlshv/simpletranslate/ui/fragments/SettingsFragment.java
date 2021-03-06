package me.mlshv.simpletranslate.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.Callable;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.data.db.DbManager;
import me.mlshv.simpletranslate.ui.activities.AboutActivity;
import me.mlshv.simpletranslate.ui.activities.LangsLoadActivity;
import me.mlshv.simpletranslate.ui.widgets.AssuranceDialog;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initFragment(view);
        return view;
    }

    private void initFragment(View view) {
        (view.findViewById(R.id.clear_cache)).setOnClickListener(itemClickListener);
        (view.findViewById(R.id.clear_history)).setOnClickListener(itemClickListener);
        (view.findViewById(R.id.clear_favorites)).setOnClickListener(itemClickListener);
        (view.findViewById(R.id.load_langs)).setOnClickListener(itemClickListener);
        (view.findViewById(R.id.about)).setOnClickListener(itemClickListener);
    }

    private View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String message = "";
            Callable<Void> action = null;
            switch (view.getId()) {
                case R.id.clear_cache:
                    message = "Очистить кэш?";
                    action = new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            clearCache();
                            return null;
                        }
                    };
                    break;

                case R.id.clear_history:
                    message = "Очистить историю?";
                    action = new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            clearHistory();
                            return null;
                        }
                    };
                    break;

                case R.id.clear_favorites:
                    message = "Очистить избранное?";
                    action = new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            clearFavorites();
                            return null;
                        }
                    };
                    break;

                case R.id.load_langs:
                    loadLangs();
                    break;

                case R.id.about:
                    showAboutActivity();
            }
            if (action != null)
                AssuranceDialog.show(getContext(), message, action);
        }
    };

    private void clearCache() {
        DbManager dbManager = new DbManager(App.getInstance()).open();
        dbManager.clearCache();
        dbManager.close();
    }

    private void clearHistory() {
        DbManager dbManager = new DbManager(App.getInstance()).open();
        dbManager.clearHistory();
        dbManager.close();
    }

    private void clearFavorites() {
        DbManager dbManager = new DbManager(App.getInstance()).open();
        dbManager.clearFavorites();
        dbManager.close();
    }

    private void loadLangs() {
        Intent intent = new Intent(getContext(), LangsLoadActivity.class);
        startActivity(intent);
    }

    private void showAboutActivity() {
        Intent intent = new Intent(getContext(), AboutActivity.class);
        startActivity(intent);
    }
}
