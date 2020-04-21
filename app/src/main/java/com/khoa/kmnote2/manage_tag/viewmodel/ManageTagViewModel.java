package com.khoa.kmnote2.manage_tag.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.khoa.kmnote2.manage_tag.adapter.OnEditTagListener;
import com.khoa.kmnote2.manage_tag.adapter.TagRcvAdapter;
import com.khoa.kmnote2.model.Note;
import com.khoa.kmnote2.model.Tag;
import com.khoa.kmnote2.repository.AppRepository;
import com.khoa.kmnote2.repository.firebase.FirebaseDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;

public class ManageTagViewModel extends ViewModel {

    public MutableLiveData<ArrayList<Tag>> tagList;
    public List<Note> noteList;
    public TagRcvAdapter adapter;
    public CompositeDisposable disposable;
    public boolean isEdited = false;

    public void init(OnEditTagListener listener) {
        this.tagList = new MutableLiveData<>(new ArrayList<Tag>());
        adapter = new TagRcvAdapter(listener);
        this.disposable = new CompositeDisposable();
    }

    public Single<List<Tag>> loadTag() {
        return Single.create(new SingleOnSubscribe<List<Tag>>() {
            @Override
            public void subscribe(SingleEmitter<List<Tag>> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    noteList = AppRepository.getInstance().getAllNoteHasTag();
                    emitter.onSuccess(convertToTagList(noteList));
                }
            }
        });
    }

    public List<Tag> convertToTagList(List<Note> noteList) {
        List<Tag> tags = new ArrayList<>();
        List<String> nameTags = new ArrayList<>();
        for (Note note : noteList) {
            if (!note.tagList.isEmpty()) {
                List<String> noteTagList = note.tagList;
                for (String nameTag : noteTagList) {
                    if (nameTags.contains(nameTag)) {
                        int index = nameTags.indexOf(nameTag);
                        Tag tag = tags.get(index);
                        tag.amount = tag.amount + 1;
                        tags.set(index, tag);
                    } else {
                        nameTags.add(nameTag);
                        tags.add(new Tag(nameTag, 1));
                    }
                }
            }
        }
        return tags;
    }

    public void renameTag(final String oldName, final String newName) {
        if (noteList == null) return;
        for (Tag tag : tagList.getValue()) {
            if (tag.name.equals(oldName)) {
                tag.name = newName;
            }
        }
        adapter.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Note note : noteList) {
                    int index = note.tagList.indexOf(oldName);
                    if (index >= 0) {
                        isEdited = true;
                        note.tagList.set(index, newName);
                        note.updateAt = System.currentTimeMillis();
                        AppRepository.getInstance().updateNote(note);
                        FirebaseDatabaseHelper.getInstance().addNote(note);
                    }
                }
            }
        }).start();
    }

    public void deleteTag(final Tag tag) {
        tagList.getValue().remove(tag);
        adapter.notifyDataSetChanged();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Note note : noteList) {
                    int index = note.tagList.indexOf(tag.name);
                    if (index >= 0) {
                        isEdited = true;
                        note.tagList.remove(index);
                        note.updateAt = System.currentTimeMillis();
                        AppRepository.getInstance().updateNote(note);
                        FirebaseDatabaseHelper.getInstance().addNote(note);
                    }
                }
            }
        }).start();
    }

    public void deleteBothNote(final Tag tag){
        tagList.getValue().remove(tag);
        adapter.notifyDataSetChanged();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Note note : noteList) {
                    int index = note.tagList.indexOf(tag.name);
                    if (index >= 0) {
                        isEdited = true;
                        AppRepository.getInstance().deleteNote(note);
                        FirebaseDatabaseHelper.getInstance().deleteNote(note);
                    }
                }
            }
        }).start();
    }
}
