package com.khoa.kmnote2.main.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khoa.kmnote2.R;
import com.khoa.kmnote2.databinding.ActivityMainBinding;
import com.khoa.kmnote2.edit.NoteAction;
import com.khoa.kmnote2.edit.view.EditNoteActivity;
import com.khoa.kmnote2.main.adapter.OnNoteClickListener;
import com.khoa.kmnote2.main.adapter.OnSwipeListener;
import com.khoa.kmnote2.main.adapter.OnTagClickListener;
import com.khoa.kmnote2.main.adapter.SwipeController;
import com.khoa.kmnote2.main.viewmodel.MainViewModel;
import com.khoa.kmnote2.manage_tag.view.ManageTagActivity;
import com.khoa.kmnote2.model.Note;
import com.khoa.kmnote2.model.Tag;
import com.khoa.kmnote2.repository.AppRepository;
import com.khoa.kmnote2.repository.firebase.FirebaseDatabaseHelper;
import com.khoa.kmnote2.util.OnTextChangeListener;
import com.khoa.kmnote2.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity implements OnTagClickListener, OnNoteClickListener, OnSwipeListener {

    private final int MANAGE_TAG_REQUEST_CODE = 345;
    private ActivityMainBinding mBinding;
    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        if (savedInstanceState == null) {
            mViewModel.init(this);
            loadAllNote();
            downloadAllNote();
        } else {
            mViewModel.disposable = new CompositeDisposable();
        }
        setupBinding();
        startNoteAnimation();
        EventBus.getDefault().register(this);
    }

    public void loadAllNote() {
        final String str = mBinding.edtSearch.getText().toString();
        if (str.isEmpty()) {
            mViewModel.isLoading.postValue(true);
            mViewModel.disposable.add(mViewModel.loadAllNote()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<List<Note>>() {
                        @Override
                        public void onSuccess(List<Note> notes) {
                            mViewModel.isLoading.postValue(false);
                            mViewModel.noteList.postValue(notes);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mViewModel.isLoading.postValue(false);
                            showToast(e);
                            e.printStackTrace();
                        }
                    }));
        } else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mBinding.edtSearch.setText("");
                    mBinding.edtSearch.append(str);
                }
            });
        }
    }

    private void downloadAllNote() {
        if (Util.availableNetwork()) {
            mViewModel.disposable.add(FirebaseDatabaseHelper.getInstance()
                    .downloadAllNote()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<List<Note>>() {
                        @Override
                        public void onSuccess(List<Note> noteList) {
                            onDownloadedNotes(noteList);
                        }

                        @Override
                        public void onError(Throwable e) {
                            showToast(e);
                            e.printStackTrace();
                        }
                    }));
        }
    }

    private void onDownloadedNotes(List<Note> noteList) {
        mViewModel.disposable.add(AppRepository.getInstance().compareAndUpdateNotes(noteList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        loadAllNote();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(e);
                        e.printStackTrace();
                    }
                }));
    }

    private void setupBinding() {
        RecyclerView.LayoutManager tagLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mBinding.rcvTag.setLayoutManager(tagLayoutManager);
        mBinding.rcvTag.setAdapter(mViewModel.tagRcvAdapter);

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(new SwipeController(this));
        itemTouchhelper.attachToRecyclerView(mBinding.rcvNote);
        mBinding.rcvNote.setAdapter(mViewModel.noteRcvAdapter);

        mBinding.rcvNote.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    mBinding.fabCreateNote.hide();
                } else {
                    mBinding.fabCreateNote.show();
                }
            }
        });

        mBinding.edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Util.hideKeyboard(mBinding.edtSearch);
                    return true;
                }
                return false;
            }
        });

        mViewModel.disposable.add(fromEditText(mBinding.edtSearch)
                .debounce(200, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String string) throws Exception {
                        return !mViewModel.textSearch.equals(string);
                    }
                })
                .switchMap(new Function<String, ObservableSource<List<Note>>>() {
                    @Override
                    public ObservableSource<List<Note>> apply(String s) throws Exception {
                        mViewModel.textSearch = s;
                        return AppRepository.getInstance().searchNote("%" + s + "%");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<Note>>() {
                    @Override
                    public void onNext(List<Note> notes) {
                        mViewModel.noteList.postValue(notes);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }
                }));

        mViewModel.isLoading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading) {
                    mBinding.progressLoading.setVisibility(View.VISIBLE);
                } else {
                    mBinding.progressLoading.setVisibility(View.GONE);
                }
            }
        });

        mViewModel.tagList.observe(this, new Observer<List<Tag>>() {
            @Override
            public void onChanged(List<Tag> tags) {
                Tag currentTag = mViewModel.currentTag.getValue();
                mViewModel.tagRcvAdapter.setTagList(tags, currentTag.name);
            }
        });

        mViewModel.noteList.observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                mViewModel.onNoteListChange(notes);
            }
        });

        mViewModel.currentNoteList.observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                mViewModel.noteRcvAdapter.setNoteList(notes);
            }
        });

        mViewModel.currentTag.observe(this, new Observer<Tag>() {
            @Override
            public void onChanged(Tag tag) {
                mViewModel.setCurrentTag(tag);
            }
        });

    }

    @Override
    public void onClickNote(Note note) {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra("note", note);
        startActivity(intent, ActivityOptions.makeCustomAnimation(this, R.anim.slide_to_left, R.anim.none).toBundle());
    }

    @Override
    public void onClickTag(Tag tag) {
        Tag currentTag = mViewModel.currentTag.getValue();
        if (currentTag != null && currentTag.name.equals(tag.name))
            return;
        mViewModel.currentTag.setValue(tag);
    }

    public void onClickCreateNote(View view) {
        Intent intent = new Intent(this, EditNoteActivity.class);
        startActivity(intent);
    }

    public void onClickCancelSearch(View view) {
        mBinding.edtSearch.setText("");
        mBinding.edtSearch.clearFocus();
        mBinding.actionbar.requestLayout();
        Util.hideKeyboard(mBinding.edtSearch);
    }

    public void onClickManageTag(View view) {
        Intent intent = new Intent(this, ManageTagActivity.class);
        startActivityForResult(intent, MANAGE_TAG_REQUEST_CODE, ActivityOptions.makeCustomAnimation(this, R.anim.slide_to_left, R.anim.none).toBundle());
    }

    private void showToast(Throwable e) {
        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void startFabAnimation() {
        mBinding.fabCreateNote.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.fabCreateNote.setVisibility(View.VISIBLE);
                mBinding.fabCreateNote.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_out_appear));
            }
        }, 200);
    }

    private void startNoteAnimation() {
        mBinding.rcvNote.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.rcvNote.setVisibility(View.VISIBLE);
                mBinding.rcvNote.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_up_appear));
            }
        }, 200);
    }

    @Subscribe
    public void onReciveNoteAction(NoteAction noteAction) {
        if (noteAction == null) return;
        if (noteAction.action != NoteAction.ACTION.NONE) {
            loadAllNote();
        }
    }

    public Observable<String> fromEditText(EditText editText) {
        final PublishSubject<String> subject = PublishSubject.create();

        editText.addTextChangedListener(new OnTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String queryString = charSequence.toString();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (!queryString.isEmpty()) {
                            mBinding.imgCancelSearch.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.imgCancelSearch.setVisibility(View.GONE);
                        }
                    }
                });
                subject.onNext(queryString);
            }
        });

        return subject;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MANAGE_TAG_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            loadAllNote();
        }
    }

    public void onClickRefresh(View view) {
        loadAllNote();
    }

    @Override
    public void onSwipedLeftListener(Note note) {
        showDeleteNoteDialog(note);
    }

    @Override
    public void onSwipedRightListener(Note note) {
        int index = mViewModel.noteRcvAdapter.getNoteList().indexOf(note);
        if (index >= 0) mViewModel.noteRcvAdapter.notifyItemChanged(index);
    }

    private void showDeleteNoteDialog(final Note note) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Xóa ghi chú này ?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mViewModel.deleteNote(note);
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                int index = mViewModel.noteRcvAdapter.getNoteList().indexOf(note);
                if (index >= 0) mViewModel.noteRcvAdapter.notifyItemChanged(index);
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startFabAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mBinding = null;
        mViewModel.disposable.dispose();
    }
}
