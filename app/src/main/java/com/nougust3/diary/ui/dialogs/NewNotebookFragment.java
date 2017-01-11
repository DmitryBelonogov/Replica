package com.nougust3.diary.ui.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.nougust3.diary.R;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Notebook;

public class NewNotebookFragment extends DialogFragment {

    private EditText nameEdit;
    private EditText descEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_new_notebook, container, false);

        getDialog().setTitle("New notebook");

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

    private void saveNotebook() {
        if(checkName()) {
            dismiss();
            return;
        }

        Notebook notebook = new Notebook();

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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}
