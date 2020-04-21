package com.khoa.kmnote2.select_tag.viewmodel;

import androidx.lifecycle.ViewModel;

import com.khoa.kmnote2.model.Note;
import com.khoa.kmnote2.repository.AppRepository;
import com.khoa.kmnote2.select_tag.adapter.TagRcvAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectTagViewModel extends ViewModel {

    public Note note;
    public List<String> allTagList;
    public List<String> selectedTagList;
    public List<String> currentList;
    public TagRcvAdapter tagRcvAdapter;

    public void init(Note note) {
        this.note = note;
        this.allTagList = new ArrayList<>();
        this.currentList = new ArrayList<>();
        this.selectedTagList = new ArrayList<>(note.tagList);
        tagRcvAdapter = new TagRcvAdapter();
    }

    public void showTagList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                allTagList = AppRepository.getInstance().getAllTag();
                currentList = new ArrayList<>(allTagList);
                tagRcvAdapter.setTagList(currentList, selectedTagList);
            }
        }).start();
    }

    public void showTagList(String tag) {
        currentList = new ArrayList<>();
        for (String str : allTagList) {
            if (str.contains(tag) || tag.contains(str)) {
                currentList.add(str);
            }
        }
        tagRcvAdapter.setTagList(currentList, selectedTagList);
    }

    public void createNewTag(String string) {
        if (!string.isEmpty() && !allTagList.contains(string)) {
            allTagList.add(0, string);
            selectedTagList.add(0, string);
            tagRcvAdapter.setTagList(currentList, selectedTagList);
        }
    }
}
