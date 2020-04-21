package com.khoa.kmnote2.main.viewmodel;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.khoa.kmnote2.main.adapter.NoteRcvAdapter;
import com.khoa.kmnote2.main.adapter.OnNoteClickListener;
import com.khoa.kmnote2.main.adapter.OnTagClickListener;
import com.khoa.kmnote2.main.adapter.TagRcvAdapter;
import com.khoa.kmnote2.main.view.MainActivity;
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

public class MainViewModel extends ViewModel {

    public MutableLiveData<List<Note>> noteList;
    public MutableLiveData<List<Note>> currentNoteList;

    public MutableLiveData<List<Tag>> tagList;
    public MutableLiveData<Tag> currentTag;

    public TagRcvAdapter tagRcvAdapter;
    public NoteRcvAdapter noteRcvAdapter;

    public MutableLiveData<Boolean> isLoading;
    public CompositeDisposable disposable;
    public String textSearch = "";
    private MainActivity activity;

    public void init(Activity activity) {
        this.activity = (MainActivity) activity;
        noteList = new MutableLiveData<>();
        currentNoteList = new MutableLiveData<>();
        tagList = new MutableLiveData<>();
        currentTag = new MutableLiveData<>(new Tag("Tất cả", 0));

        isLoading = new MutableLiveData<>();
        disposable = new CompositeDisposable();

        tagRcvAdapter = new TagRcvAdapter();
        tagRcvAdapter.setListener((OnTagClickListener) activity);

        noteRcvAdapter = new NoteRcvAdapter();
        noteRcvAdapter.setListener((OnNoteClickListener) activity);

    }

    public Single<List<Note>> loadAllNote() {
        return Single.create(new SingleOnSubscribe<List<Note>>() {
            @Override
            public void subscribe(SingleEmitter<List<Note>> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    List<Note> list = AppRepository.getInstance().getAllNote();
                    emitter.onSuccess(list);
                }
            }
        });
    }

    public void setCurrentTag(Tag tag) {
        if (noteList.getValue() != null) {
            if (tag.name.equals("Tất cả")) {
                List<Note> list = new ArrayList<>(noteList.getValue());
                currentNoteList.postValue(list);
            } else {
                List<Note> list = new ArrayList<>();
                for (Note note : noteList.getValue()) {
                    if (note.tagList.contains(tag.name)) {
                        list.add(note);
                    }
                }
                currentNoteList.postValue(list);
            }
        }
    }

    public void onNoteListChange(List<Note> newNoteList) {
        List<Tag> tags = new ArrayList<>();
        List<String> nameTags = new ArrayList<>();

        for (Note note : newNoteList) {
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
        tags.add(0, new Tag("Tất cả", newNoteList.size()));
        tagList.postValue(tags);

        int index = nameTags.indexOf(currentTag.getValue().name);
        if (index >= 0) {
            Tag tag = tags.get(index + 1);
            currentTag.postValue(tag);
        } else {
            currentTag.postValue(tags.get(0));
        }
    }

    public void deleteNote(final Note note) {
        int index = noteRcvAdapter.deleteNote(note);
        if (index >= 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppRepository.getInstance().deleteNote(note);
                    FirebaseDatabaseHelper.getInstance().deleteNote(note);

                    activity.loadAllNote();
                }
            }).start();
        }
    }
}
