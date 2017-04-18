package me.mlshv.simpletranslate.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.data.db.DbHelper;
import me.mlshv.simpletranslate.data.db.DbManager;
import me.mlshv.simpletranslate.data.model.Translation;
import me.mlshv.simpletranslate.ui.activities.MainActivity;
import me.mlshv.simpletranslate.util.TranslationsRecyclerAdapter;

public class HistoryFragment extends Fragment {
    private DbManager dbManager;
    private RecyclerView historyList;

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
        historyList.setAdapter(new TranslationsRecyclerAdapter(
                        dbManager.fetchHistory(),
                        ((MainActivity) this.getActivity())));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Log.d(App.tag(this), "onStart: updating recyclerView");
            if (historyList != null) {
                dbManager.open();
                ((TranslationsRecyclerAdapter) historyList.getAdapter())
                        .changeCursor(dbManager.fetchHistory());
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