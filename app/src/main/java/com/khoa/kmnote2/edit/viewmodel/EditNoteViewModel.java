package com.khoa.kmnote2.edit.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.khoa.kmnote2.util.Util;
import com.khoa.kmnote2.edit.NoteAction;
import com.khoa.kmnote2.edit.adapter.SimpleTagRcvAdapter;
import com.khoa.kmnote2.model.Note;
import com.khoa.kmnote2.repository.AppRepository;
import com.khoa.kmnote2.repository.firebase.FirebaseDatabaseHelper;

import org.greenrobot.eventbus.EventBus;

public class EditNoteViewModel extends ViewModel {

    public Note currentNote;
    public Note mNote;
    public SimpleTagRcvAdapter tagRcvAdapter;
    public NoteAction noteAction;

    public void init() {
        tagRcvAdapter = new SimpleTagRcvAdapter();
    }

    public void saveChange() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mNote == null) {
                    // create new note
                    if (Util.isEmptyNote(currentNote)) {
                        noteAction = new NoteAction(NoteAction.ACTION.NONE, null);
                    } else {
                        if (currentNote.id > 0) {
                            AppRepository.getInstance().updateNote(currentNote);
                        } else {
                            long id = AppRepository.getInstance().insertNote(currentNote);
                            currentNote.id = (int) id;
                        }
                        FirebaseDatabaseHelper.getInstance().addNote(currentNote);
                        noteAction = new NoteAction(NoteAction.ACTION.ADD, currentNote);
                    }
                } else {
                    // edit note
                    if (Util.isEmptyNote(currentNote)) {
                        AppRepository.getInstance().deleteNote(mNote);
                        noteAction = new NoteAction(NoteAction.ACTION.DELETE, mNote);
                        FirebaseDatabaseHelper.getInstance().deleteNote(mNote);
                    } else if (!Util.equalNote(mNote, currentNote)) {
                        AppRepository.getInstance().updateNote(currentNote);
                        noteAction = new NoteAction(NoteAction.ACTION.UPDATE, currentNote);
                        FirebaseDatabaseHelper.getInstance().addNote(currentNote);
                    } else {
                        noteAction = new NoteAction(NoteAction.ACTION.NONE, null);
                    }
                }
                EventBus.getDefault().post(noteAction);
            }
        }).start();
    }
}
