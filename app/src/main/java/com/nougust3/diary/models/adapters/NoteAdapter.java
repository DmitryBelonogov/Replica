package com.nougust3.diary.models.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.nougust3.diary.R;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Note;
import com.nougust3.diary.models.holders.NoteViewHolder;
import com.nougust3.diary.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends BaseAdapter {

    private static final int TASK_ITEM = 0;
    private static final int NOTE_ITEM = 1;

    private LayoutInflater inflater;

    private List<Note> notes = new ArrayList<>();

    public NoteAdapter(Activity activity, List<Note> notes) {

        this.notes = notes;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(notes.get(position).isTask()) {
            return TASK_ITEM;
        }
        else {
            return NOTE_ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = notes.get(position);
        NoteViewHolder holder;
        View view = convertView;

        if(view == null) {
            if(note.isTask()) {
                view = inflater.inflate(R.layout.task_view, parent, false);
            }
            else {
                view = inflater.inflate(R.layout.list_item, parent, false);
            }
            holder = new NoteViewHolder();
            holder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            holder.dateView = (TextView) view.findViewById(R.id.dateView);
            holder.contentView = (TextView) view.findViewById(R.id.contentView);
            holder.titleView = (TextView) view.findViewById(R.id.titleView);
            holder.notebookView = (TextView) view.findViewById(R.id.notebookView);
            view.setTag(holder);
        }
        else {
            holder = (NoteViewHolder) view.getTag();
        }

        if(note.isTask()) {
            holder.checkBox.setText(note.getContent());
        }
        else {
            holder.dateView.setText(DateUtils.parseDate(note.getModification()));
            holder.contentView.setText(note.getContent());
            holder.titleView.setText((!note.getTitle().equals("")
                    && !note.getTitle().equals("title"))
                    ? note.getTitle() : "Без названия"
            );
            if(note.getNotebook() == 0) {
                holder.notebookView.setText("Inbox");
            }
            else {
                holder.notebookView.setText(DBHelper.getInstance().
                        getNotebook(note.getNotebook()).getName());
            }
        }

        return view;
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

}
