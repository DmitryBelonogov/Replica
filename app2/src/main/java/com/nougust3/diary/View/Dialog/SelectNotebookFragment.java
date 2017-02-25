package com.nougust3.diary.View.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.nougust3.diary.R;
import com.nougust3.diary.Model.Database.DBHelper;
import com.nougust3.diary.Model.Notebook;
import com.nougust3.diary.View.Adapter.NotebookAdapter;

import java.util.List;

public class SelectNotebookFragment extends DialogFragment {

    OnCompleteListener listener;

    private ListView notebooksList;
    private Button cancelBtn;
    private Button newBtn;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnCompleteListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnCompleteListener");
        }
    }

    @Override
    public
    View onCreateView(LayoutInflater inflater, ViewGroup container,
                      Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_select_notebook, container, false);

        getDialog().setTitle("New notebook");

        notebooksList = (ListView) v.findViewById(R.id.notebooksList);
        cancelBtn = (Button) v.findViewById(R.id.cancelBtn);
        newBtn = (Button) v.findViewById(R.id.newBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                listener.onComplete();
            }
        });

        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                listener.onComplete();
            }
        });

        List<Notebook> notebooks = DBHelper.getInstance().getAllNotebooks();
        final NotebookAdapter adapter = new NotebookAdapter(getActivity(), notebooks);

        notebooksList.setAdapter(adapter);

        notebooksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dismiss();
                listener.onSelect(adapter.getItem(i).getId());
            }
        });

        return v;
    }
}
