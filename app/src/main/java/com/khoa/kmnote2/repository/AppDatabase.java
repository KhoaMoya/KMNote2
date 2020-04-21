package com.khoa.kmnote2.repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.khoa.kmnote2.model.Note;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
@TypeConverters({ConvertStringListToString.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract NoteDAO noteDAO();

    private static AppDatabase appDatabase;

    public static AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, "kmnote2_database").build();
        }
        return appDatabase;
    }
}
