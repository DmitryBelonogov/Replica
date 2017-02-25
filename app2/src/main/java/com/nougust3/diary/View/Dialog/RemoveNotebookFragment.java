package com.nougust3.diary.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.nougust3.diary.Model.Database.DBHelper;
import com.nougust3.diary.Model.Notebook;

public class RemoveNotebookFragment extends DialogFragment {

    private Notebook notebook;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Remove notebook?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        remove();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    public void setNotebook(String name) {
        notebook = DBHelper.getInstance().getNotebook(
                DBHelper.getInstance().getNotebookId(name)
        );
    }

    private void remove() {
        DBHelper.getInstance().removeNotebook(notebook.getName());

        listener.onComplete();
    }
}
