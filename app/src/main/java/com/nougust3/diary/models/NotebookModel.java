package com.nougust3.diary.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.nougust3.diary.utils.Constants;
import java.util.Date;

public class NotebookModel implements Parcelable {

    private float id;
    private float parent;
    private String name;
    private String description;

    public NotebookModel() {
        id = new Date().getTime();
        parent = 0;
        name = Constants.notebook_name_default;
        description = Constants.notebook_desc_default;
    }

    private NotebookModel(Parcel in) {
        id = in.readFloat();
        parent = in.readFloat();
        name = in.readString();
        description = in.readString();
    }

    public static final Creator<NotebookModel> CREATOR = new Creator<NotebookModel>() {
        @Override
        public NotebookModel createFromParcel(Parcel in) {
            return new NotebookModel(in);
        }

        @Override
        public NotebookModel[] newArray(int size) {
            return new NotebookModel[size];
        }
    };

    public float getId() {
        return id;
    }

    public void setId(float id) {
        this.id = id;
    }

    public float getParent() {
        return parent;
    }

    public void setParent(float parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(id);
        parcel.writeFloat(parent);
        parcel.writeString(name);
        parcel.writeString(description);
    }
}
