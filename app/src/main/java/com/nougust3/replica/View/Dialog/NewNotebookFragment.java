package com.nougust3.replica.View.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nougust3.replica.Model.Database.DBHelper;
import com.nougust3.replica.Model.Notebook;
import com.nougust3.replica.R;

public class NewNotebookFragment extends DialogFragment {

    private EditText nameEdit;
    private EditText descEdit;

    OnCompleteListener listener;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_new_notebook, container, false);

        getDialog().setTitle("New notebook");

        Button doneBtn = (Button) v.findViewById(R.id.done);
        Button cancelBtn = (Button) v.findViewById(R.id.cancel);
        nameEdit = (EditText) v.findViewById(R.id.nameView);
        descEdit = (EditText) v.findViewById(R.id.descriptionView);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNotebook();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
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
        listener.onComplete();
    }

    // TODO Add toast
    private boolean checkName() {
        if(nameEdit.getText().toString().equals("")) {
            return true;
        }
        return DBHelper.getInstance().checkNotebook(nameEdit.getText().toString());

    }

    private void clearViews() {
        nameEdit.setText("");
        descEdit.setText("");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        clearViews();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        clearViews();
    }
}
