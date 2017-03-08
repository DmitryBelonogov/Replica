package com.nougust3.replica.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Tag implements Parcelable {

    private long id;
    private String name;

    public Tag() {
        id = new Date().getTime();
        name = "Tag";
    }

    private Tag(Parcel in) {
        id = in.readLong();
        name = in.readString();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(id);
        parcel.writeString(name);
    }
}
