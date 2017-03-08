package com.nougust3.replica.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nougust3.replica.Model.Database.DBHelper;
import com.nougust3.replica.Model.Note;
import com.nougust3.replica.R;
import com.nougust3.replica.Utils.ContentUtils;
import com.nougust3.replica.Utils.DateUtils;
import com.nougust3.replica.View.Holder.NoteHolder;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends BaseAdapter {

    private static final int TASK_ITEM = 0;
    private static final int NOTE_ITEM = 1;

    private LayoutInflater inflater;

    private SparseBooleanArray selectedItemsId;

    private List<Note> notes = new ArrayList<>();
    private Activity activity;

    public NoteAdapter(Activity activity, List<Note> notes) {
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.notes = notes;
        selectedItemsId = new SparseBooleanArray();

        for(Note note : this.notes) {
            note.setContent(ContentUtils.htmlToText(note.getContent()));
        }
    }

    public void setNotes(ArrayList<Note> notesList) {
        this.notes = notesList;
        for (Note note: notes) {
            note.setContent(ContentUtils.htmlToText(note.getContent()));
        }
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
        NoteHolder holder;
        View view = convertView;

        if(view == null) {
            if(note.isTask()) {
                view = inflater.inflate(R.layout.task_view, parent, false);
            }
            else {
                view = inflater.inflate(R.layout.list_item, parent, false);
            }
            holder = new NoteHolder();
            holder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            holder.dateView = (TextView) view.findViewById(R.id.dateView);
            holder.dateView.setVisibility(View.GONE);
            holder.contentView = (TextView) view.findViewById(R.id.contentView);
            holder.titleView = (TextView) view.findViewById(R.id.titleView);
            holder.notebookView = (TextView) view.findViewById(R.id.notebookView);
            holder.notebookView.setVisibility(View.GONE);
            view.setTag(holder);
        }
        else {
            holder = (NoteHolder) view.getTag();
        }

        if(note.isTask()) {
            holder.checkBox.setText(note.getContent());
        }
        else {
            String date = DateUtils.parseDate(note.getModification());
            String notebook = (note.getNotebook() == 0 ? "Inbox" : DBHelper.getInstance().
                    getNotebook(note.getNotebook()).getName());

            Spannable string = new SpannableString(String.format("%s %s %s  ", date, notebook, note.getContent()));

            string.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.primary_dark)), 0, date.length() + notebook.length() + 2
                    , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //string.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, date.length() + notebook.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            holder.dateView.setText(DateUtils.parseDate(note.getModification()));
           // holder.contentView.setText(note.getContent());

            holder.contentView.setText(string);

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
    public void toggleSelection(int position) {
        selectView(position, !selectedItemsId.get(position));
    }

    public void removeSelection() {
        selectedItemsId = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedItemsId.size();
    }

    public SparseBooleanArray getSelectedId() {
        return selectedItemsId;
    }

    public void selectView(int position, boolean value) {
        if(value) {
            selectedItemsId.put(position, value);
        }
        else {
            selectedItemsId.delete(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public Note getItem(int position) {
        return notes.get(position);
    }

}
