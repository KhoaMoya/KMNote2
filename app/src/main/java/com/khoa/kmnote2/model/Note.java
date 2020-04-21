package com.khoa.kmnote2.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;
    @ColumnInfo(name = "title")
    public String title = "";
    @ColumnInfo(name = "content")
    public String content = "";
    @ColumnInfo(name = "tag_list")
    public List<String> tagList;
    @ColumnInfo(name = "create_at")
    public long createAt;
    @ColumnInfo(name = "update_at")
    public long updateAt;

    public Note() {
        createAt = updateAt = System.currentTimeMillis();
        tagList = new ArrayList<>();
    }


    @Ignore
    public Note(String title, String content, long createAt, long updateAt, List<String> tags) {
        this.title = title;
        this.content = content;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.tagList = tags;
    }

    @Ignore
    public Note clone(){
        List<String> list = new ArrayList<>();
        if(tagList!=null){
            for(String tag : tagList) list.add(tag);
        }
        Note newNote =  new Note(title, content, createAt, updateAt, list);
        newNote.id = id;
        return newNote;
    }
}
