package com.nougust3.diary.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nougust3.diary.R;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Content;
import com.nougust3.diary.models.Notebook;

public class RenameNotebookFragment extends DialogFragment {

    private EditText nameEdit;
    private EditText descEdit;

    private Notebook notebook;

    public interface NoticeDialogListener {
        public void onDoneClick(DialogFragment dialog);
    }

    NoticeDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_new_notebook, container, false);

        getDialog().setTitle("Rename notebook");

        Button doneBtn = (Button) v.findViewById(R.id.done);
        nameEdit = (EditText) v.findViewById(R.id.nameView);
        descEdit = (EditText) v.findViewById(R.id.descriptionView);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNotebook();
            }
        });

        return v;
    }

    public void setName(String name) {
        notebook = DBHelper.getInstance().getNotebook(
                DBHelper.getInstance().getNotebookId(name)
        );

        nameEdit.setText(notebook.getName());
        descEdit.setText(notebook.getDescription());
    }

    private void saveNotebook() {
        if(checkName()) {
            dismiss();
            return;
        }

        notebook.setName(nameEdit.getText().toString());
        notebook.setDescription(descEdit.getText().toString());

        DBHelper.getInstance().updateNotebook(notebook);

        dismiss();
    }

    // TODO Add toast
    private boolean checkName() {
        if(nameEdit.getText().toString().equals("")) {
            return false;
        }
        if(DBHelper.getInstance().checkNotebook(nameEdit.getText().toString())) {
            return false;
        }

        return true;
    }
}
