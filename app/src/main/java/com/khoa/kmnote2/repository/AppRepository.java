package com.khoa.kmnote2.repository;

import android.content.Context;

import com.khoa.kmnote2.MyApplication;
import com.khoa.kmnote2.util.Util;
import com.khoa.kmnote2.model.Note;
import com.khoa.kmnote2.repository.firebase.FirebaseDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.Single;

public class AppRepository {

    private AppDatabase appDatabase;
    private static AppRepository appRepository;

    public static AppRepository getInstance() {
        return new AppRepository(MyApplication.getContext());
    }

    private AppRepository(Context context) {
        appDatabase = AppDatabase.getInstance(context);
    }

    public List<Note> getAllNote() {
        return appDatabase.noteDAO().getAllNote();
    }

    public Note getNote(int id) {
        return appDatabase.noteDAO().getNote(id);
    }

    public void deleteNote(Note note) {
        appDatabase.noteDAO().deleteNote(note);
    }

    public void updateNote(Note note) {
        appDatabase.noteDAO().updateNote(note);
    }

    public Observable<List<Note>> searchNote(String key){
        return appDatabase.noteDAO().searchNote(key);
    }

    public List<Note> getAllNoteHasTag(){
        return appDatabase.noteDAO().getAllNoteHasTag();
    }

    public List<String> getAllTag(){
        List<String> result = new ArrayList<>();
        List<String> listlist = appDatabase.noteDAO().getAllTag();
        for(String listString : listlist) {
            List<String> list = Util.fromString(listString);
            for(String tag : list){
                if(!result.contains(tag)) result.add(tag);
            }
        }
        return result;
    }

    public long insertNote(Note note) {
        return appDatabase.noteDAO().insertNote(note);
    }

    public Completable compareAndUpdateNotes(final List<Note> newNotes) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                List<Note> oldNotes = getAllNote();
                List<Integer> matchList = new ArrayList<>();
                boolean isUpdate = false;

                for (int i = 0; i < oldNotes.size(); i++) {
                    Note oldNote = oldNotes.get(i);
                    boolean exist = false;
                    for (int j = 0; j < newNotes.size(); j++) {
                        Note newNote = newNotes.get(j);
                        if (oldNote.id == newNote.id) {
                            exist = true;
                            matchList.add(j);
                            if (newNote.updateAt > oldNote.updateAt) {
                                isUpdate = true;
                                updateNote(newNote);
                            } else if (newNote.updateAt < oldNote.updateAt) {
                                FirebaseDatabaseHelper.getInstance().addNote(oldNote);
                            }
                            break;
                        }
                    }
                    // không tồn tại trong list mới -> có thể là firebase chưa được đồng bộ
                    if (!exist) {
                        // lưu lên firebase
                        FirebaseDatabaseHelper.getInstance().addNote(oldNote);
                    }
                }
                // kiểm tra xem có item nào có trong firebase mà không có trong sqlite không
                for (int j = 0; j < newNotes.size(); j++) {
                    if (!matchList.contains(j)) {
                        isUpdate = true;
                        insertNote(newNotes.get(j));
                    }
                }
                if (isUpdate && !emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }
}
