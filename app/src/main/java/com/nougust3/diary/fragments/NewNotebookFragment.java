package com.nougust3.diary.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nougust3.diary.R;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.NotebookModel;

public class NewNotebookFragment extends DialogFragment implements View.OnClickListener {

    final String LOG_TAG = "myLogs";
    private Button btn;
    private EditText nameEdit;
    private EditText descEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Title!");
        View v = inflater.inflate(R.layout.form_new_notebook, null);
        v.findViewById(R.id.done).setOnClickListener(this);
        v.findViewById(R.id.cancel).setOnClickListener(this);
        btn = (Button) v.findViewById(R.id.done);
        nameEdit = (EditText) v.findViewById(R.id.nameView);
        descEdit = (EditText) v.findViewById(R.id.descriptionView);
        return v;
    }

    @Override
    public void onClick(View v) {
        if(v == btn) {
            DBHelper db = new DBHelper(getActivity());
            NotebookModel notebook = new NotebookModel();
            notebook.setName(nameEdit.getText().toString());
            notebook.setDescription(descEdit.getText().toString());

            db.updateNotebook(notebook);
            Log.i("D", "SAve notebook");
        }
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "Dialog 2: onDismiss");
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "Dialog 2: onCancel");
    }
}
