package com.khoa.kmnote2.repository.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khoa.kmnote2.model.Note;
import com.khoa.kmnote2.model.Tag;
import com.khoa.kmnote2.util.Util;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class FirebaseDatabaseHelper {
    private static FirebaseDatabaseHelper instance;
    private FirebaseDatabase database;

    private FirebaseDatabaseHelper() {
        database = FirebaseDatabase.getInstance();
    }

    public static FirebaseDatabaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseDatabaseHelper();
        }
        return instance;
    }

    public void addNote(Note note) {
        database.getReference("notes/" + note.id).setValue(note);
    }

    public void deleteNote(Note note){
        database.getReference("notes/" + note.id).removeValue();
    }

    public void getAllNote(ValueEventListener listener) {
        DatabaseReference noteRef = database.getReference("notes");
        noteRef.addValueEventListener(listener);
    }

    public Single<List<Note>> downloadAllNote() {
        return Single.create(new SingleOnSubscribe<List<Note>>() {
            @Override
            public void subscribe(final SingleEmitter<List<Note>> emitter) throws Exception {
                DatabaseReference noteRef = database.getReference("notes");
                noteRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Note> list = new ArrayList<>();
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            Note note = data.getValue(Note.class);
                            list.add(note);
                        }
                        if(!emitter.isDisposed()) emitter.onSuccess(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Loi", "Failed to read value.", error.toException());
                    }
                });
            }
        });

    }
}
