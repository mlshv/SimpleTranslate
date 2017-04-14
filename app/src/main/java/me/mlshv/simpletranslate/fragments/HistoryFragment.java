package me.mlshv.simpletranslate.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.db.DbHelper;
import me.mlshv.simpletranslate.db.DbManager;
import me.mlshv.simpletranslate.model.Translation;
import me.mlshv.simpletranslate.util.CursorRecyclerViewAdapter;

public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";
    private DbManager dbManager;
    RecyclerView historyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initFragment(view);
        return view;
    }

    private void initFragment(View view) {
        historyList = (RecyclerView) view.findViewById(R.id.history_list);
        historyList.setLayoutManager(new LinearLayoutManager(App.getInstance()));
        dbManager = new DbManager(App.getInstance());
        dbManager.open();
        historyList.setAdapter(new TranslationsAdapter(dbManager.fetchTable(DbHelper.HISTORY_TABLE)));
    }

    private class TranslationsAdapter extends CursorRecyclerViewAdapter<TranslationsAdapter.ViewHolder> {

        TranslationsAdapter(Cursor cursor) { super(cursor); }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
            Translation translation = Translation.fromCursor(cursor);
            Log.d(TAG, "onBindViewHolder: loaded translation: " + translation.toString());
            viewHolder.termLabel.setText(translation.getTerm());
            viewHolder.translationLabel.setText(translation.getTranslation());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.translations_list_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void changeCursor(Cursor cursor) {
            super.changeCursor(cursor);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView termLabel, translationLabel;
            ViewHolder(View itemView) {
                super(itemView);
                termLabel = (TextView) itemView.findViewById(R.id.list_term_label);
                translationLabel = (TextView) itemView.findViewById(R.id.list_translation_label);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Log.d(TAG, "onStart: updating recyclerView");
            if (historyList != null) {
                dbManager.open();
                ((TranslationsAdapter) historyList.getAdapter()).changeCursor(dbManager.fetchTable(DbHelper.HISTORY_TABLE));
                historyList.getAdapter().notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dbManager.close();
    }
}