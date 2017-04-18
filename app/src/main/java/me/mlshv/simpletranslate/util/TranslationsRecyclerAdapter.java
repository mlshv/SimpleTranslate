package me.mlshv.simpletranslate.util;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import me.mlshv.simpletranslate.App;
import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.data.model.Translation;


public class TranslationsRecyclerAdapter extends CursorRecyclerViewAdapter<TranslationsRecyclerAdapter.ViewHolder> {

    public TranslationsRecyclerAdapter(Cursor cursor) {
        super(cursor);
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

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
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
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(App.tag(this), "onClick " + getAdapterPosition() + " " + item.toString());
        }

        void setItem(Translation item) {
            this.item = item;
            termLabel.setText(item.getTerm());
            translationLabel.setText(item.getTranslation());
        }
    }
}