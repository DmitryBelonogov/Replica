package com.nougust3.diary.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Objects;

public class Note implements Parcelable {

    private long creation = 0;
    private long modification = 0;
    private String title = "";
    private String content = "";
    private String category = "";
    private int task = 0;
    private int archive = 0;

    public Note() {
        creation = new Date().getTime();
        modification = creation;
        title = "title";
        content = "content";
        category = "category";
        task = 0;
        archive = 0;
    }

    private Note(Parcel source) {
        String[] data = new String[6];

        source.readStringArray(data);

        creation = Long.parseLong(data[0]);
        modification = Long.parseLong(data[1]);
        title = data[2];
        content = data[3];
        category = data[4];
        task = Integer.parseInt(data[5]);
        archive = Integer.parseInt(data[6]);
    }

    public void setCreation(long creation) {
        this.creation = creation;
    }

    public long getCreation() {
        return creation;
    }

    public void setModification(long modification) {
        this.modification = modification;
    }

    public long getModification() {
        return modification;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public boolean isTask() {
        return task == 1;
    }

    public void setIsTask(int task) {
        this.task = task;
    }

    public boolean isArchive() {
        return archive == 1;
    }

    public void setArchive(int archive) {
        this.archive = archive;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                Objects.toString(creation, null),
                Objects.toString(modification, null),
                title,
                content,
                category,
                Objects.toString(task, null),
                Objects.toString(archive, null)
        });
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {

        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

}
