package com.khoa.kmnote2.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.khoa.kmnote2.model.Note;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface NoteDAO {
    @Query("select * from note order by update_at desc")
    List<Note> getAllNote();

    @Query("select * from note where id=:id")
    Note getNote(int id);

    @Query("select tag_list from note")
    List<String> getAllTag();

    @Query("select * from note where (content like :key) or (title like :key) or (tag_list like :key) order by update_at desc")
    Observable<List<Note>> searchNote(String key);

    @Query("select * from note where tag_list != '[]'")
    List<Note> getAllNoteHasTag();

    @Delete
    void deleteNote(Note note);

    @Update
    void updateNote(Note note);

    @Insert
    long insertNote(Note note);
}
