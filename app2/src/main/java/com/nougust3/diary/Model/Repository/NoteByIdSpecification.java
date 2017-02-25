package com.nougust3.diary.Model.Repository;

import android.annotation.SuppressLint;

import com.nougust3.diary.Utils.Constants;

public class NoteByIdSpecification implements SqlSpecification {

    private final long id;

    public NoteByIdSpecification(final long id) {
        this.id = id;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toSqlQuery() {
        return String.format(
                "SELECT * FROM %1$s WHERE `%2$s` = %3$d';",
                Constants.TABLE_NOTES,
                Constants.KEY_CREATION,
                id
        );
    }
}