package com.khoa.kmnote2.edit;

import com.khoa.kmnote2.model.Note;

public class NoteAction{

    public enum ACTION {
        NONE,
        ADD,
        DELETE,
        UPDATE
    }

    public ACTION action;
    public Note note;

    public NoteAction(ACTION action, Note note) {
        this.action = action;
        this.note = note;
    }
}

