package com.nougust3.diary.Model.Repository;

import com.nougust3.diary.Model.Note;

public interface NoteSpecification {
    boolean specified(Note account);
}
