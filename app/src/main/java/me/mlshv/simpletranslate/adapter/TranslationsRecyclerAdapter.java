package me.mlshv.simpletranslate.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.data.db.DbManager;
import me.mlshv.simpletranslate.data.model.Translation;
import me.mlshv.simpletranslate.ui.activities.MainActivity;
import me.mlshv.simpletranslate.util.SpHelper;


public class TranslationsRecyclerAdapter extends CursorRecyclerViewAdapter<TranslationsRecyclerAdapter.ViewHolder> {
    private MainActivity mainActivity;

    public TranslationsRecyclerAdapter(Cursor cursor, MainActivity mainActivity) {
        super(cursor);
        this.mainActivity = mainActivity;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Translation translation = Translation.fromCursor(cursor);
        Log.d(App.tag(this), "onBindViewHolder: loaded translation: " + translation.toString());
        viewHolder.setItem(translation);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.translations_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Translation item;
        private TextView termLabel, translationLabel;
        private CheckBox favoriteCheckbox;

        ViewHolder(View itemView) {
            super(itemView);
            termLabel = (TextView) itemView.findViewById(R.id.list_term_label);
            translationLabel = (TextView) itemView.findViewById(R.id.list_translation_label);
            favoriteCheckbox = (CheckBox) itemView.findViewById(R.id.favorite_checkbox);
            favoriteCheckbox.setOnCheckedChangeListener(favoriteCheckListener);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(App.tag(this), "onClick " + getAdapterPosition() + " " + item.toString());
            SpHelper.saveCurrentTextToTranslate(item.getTerm());
            SpHelper.saveSourceLangCode(item.getSourceLangCode());
            SpHelper.saveTargetLangCode(item.getTargetLangCode());
            mainActivity.goToTranslationFragment();
        }

        void setItem(Translation item) {
            this.item = item;
            termLabel.setText(item.getTerm());
            translationLabel.setText(item.getTranslation());
            favoriteCheckbox.setOnCheckedChangeListener(null); // убираем listener, чтобы он не вызывался при отрисовке
            if (item.hasOption(Translation.SAVED_FAVORITES)) {
                favoriteCheckbox.setChecked(true);
            } else {
                favoriteCheckbox.setChecked(false);
            }
            favoriteCheckbox.setOnCheckedChangeListener(favoriteCheckListener);
        }

        private CompoundButton.OnCheckedChangeListener favoriteCheckListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Log.d(App.tag(this), "onCheckedChanged на " + isChecked);
                Translation t = ViewHolder.this.item;
                if (isChecked) {
                    t.addStoreOption(Translation.SAVED_FAVORITES);
                } else {
                    t.removeStoreOption(Translation.SAVED_FAVORITES);
                }
                DbManager dbManager = new DbManager(App.getInstance()).open();
                dbManager.insertTranslation(t);
                dbManager.close();
            }
        };
    }
}