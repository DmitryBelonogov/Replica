package com.nougust3.diary.models.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.nougust3.diary.R;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Notebook;
import com.nougust3.diary.models.holders.NotebookHolder;

import java.util.ArrayList;
import java.util.List;

public class NotebookAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<Notebook> notebooks = new ArrayList<>();
    private int count;

    private Context context;

    public NotebookAdapter(Activity activity, List<Notebook> notebooks) {

        this.notebooks = notebooks;
        context = activity;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return notebooks.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notebook notebook = notebooks.get(position);
        NotebookHolder holder;
        View view = convertView;

        if(view == null) {
            view = inflater.inflate(R.layout.notebook_item, parent, false);
            holder = new NotebookHolder();
            holder.nameView = (TextView) view.findViewById(R.id.nameView);
            holder.descriptionView = (TextView) view.findViewById(R.id.descriptionView);
            holder.countView = (TextView) view.findViewById(R.id.countView);
            view.setTag(holder);
        }
        else {
            holder = (NotebookHolder) view.getTag();
        }

        DBHelper db = new DBHelper(context);
        count = db.getFromNotebook(notebook.getId()).size();

        holder.nameView.setText(notebook.getName());
        holder.descriptionView.setText(notebook.getDescription());
        holder.countView.setText(count + " notes");

        return view;
    }

    @Override
    public Notebook getItem(int position) {
        return notebooks.get(position);
    }

}
