package com.khoa.kmnote2.edit.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.khoa.kmnote2.R;
import com.khoa.kmnote2.util.Util;
import com.khoa.kmnote2.databinding.ActivityEditNoteBinding;
import com.khoa.kmnote2.edit.viewmodel.EditNoteViewModel;
import com.khoa.kmnote2.model.Note;
import com.khoa.kmnote2.select_tag.view.SelectTagActivity;

import java.util.List;

import static com.khoa.kmnote2.MyApplication.getContext;

public class EditNoteActivity extends AppCompatActivity {

    private final int EDIT_TAG_REQUEST_CODE = 123;
    private ActivityEditNoteBinding mBinding;
    private EditNoteViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityEditNoteBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mViewModel = new ViewModelProvider(this).get(EditNoteViewModel.class);
        if (savedInstanceState == null) {
            mViewModel.init();
            getDataIntent();
        }
        setupBinding();
        startAnimation();
    }

    private void getDataIntent() {
        Note note = (Note) getIntent().getSerializableExtra("note");
        if (note != null) {
            mViewModel.mNote = note;
            mViewModel.currentNote = note.clone();
            showNote(mViewModel.currentNote);
        } else {
            mViewModel.currentNote = new Note();
            showNote(mViewModel.currentNote);
            mBinding.edtContent.requestFocus();
        }
    }

    private void setupBinding() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setFlexDirection(FlexDirection.ROW);

        mBinding.rcvTag.setLayoutManager(layoutManager);
        mBinding.rcvTag.setAdapter(mViewModel.tagRcvAdapter);
    }

    private void showNote(Note note) {
        mBinding.txtUpdateTime.setText(Util.convertToTime(note.updateAt));
        mBinding.txtCreatedTime.setText(Util.convertToTime(note.createAt));
        mBinding.edtTitle.setText("");
        mBinding.edtTitle.append(note.title);
        mBinding.edtContent.setText("");
        mBinding.edtContent.append(note.content);
        mViewModel.tagRcvAdapter.setTagList(note.tagList);
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickTag(View view) {
        Intent intent = new Intent(EditNoteActivity.this, SelectTagActivity.class);
        intent.putExtra("note", mViewModel.currentNote);
        startActivityForResult(intent, EDIT_TAG_REQUEST_CODE, ActivityOptions.makeCustomAnimation(this, R.anim.slide_to_left, R.anim.none).toBundle());
    }

    public void onClickRemind(View view) {

    }

    private void updateCurrentNote() {
        mViewModel.currentNote.title = mBinding.edtTitle.getText().toString();
        mViewModel.currentNote.content = mBinding.edtContent.getText().toString();
        mViewModel.currentNote.updateAt = System.currentTimeMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode== EDIT_TAG_REQUEST_CODE){
            List<String> tagList = data.getStringArrayListExtra("tag_list");
            if(tagList!=null){
                mViewModel.currentNote.tagList = tagList;
                mViewModel.tagRcvAdapter.setTagList(tagList);
            }
        }
    }

    private void startAnimation(){
        mBinding.imgBack.setVisibility(View.INVISIBLE);
        mBinding.imgTag.setVisibility(View.INVISIBLE);
        mBinding.imgRemind.setVisibility(View.INVISIBLE);
        mBinding.txtCreatedTime.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.imgBack.setVisibility(View.VISIBLE);
                mBinding.imgBack.startAnimation(getAnimationZoomOutAppear());
            }
        }, 200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.imgTag.setVisibility(View.VISIBLE);
                mBinding.imgTag.startAnimation(getAnimationZoomOutAppear());
            }
        }, 300);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.imgRemind.setVisibility(View.VISIBLE);
                mBinding.imgRemind.startAnimation(getAnimationZoomOutAppear());
            }
        }, 400);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.txtCreatedTime.setVisibility(View.VISIBLE);
                mBinding.txtCreatedTime.startAnimation(AnimationUtils.loadAnimation(EditNoteActivity.this, R.anim.slide_up_appear));
            }
        }, 300);

    }

    private Animation getAnimationZoomOutAppear(){
        return AnimationUtils.loadAnimation(EditNoteActivity.this, R.anim.zoom_out_appear);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateCurrentNote();
        mViewModel.saveChange();
        if (isFinishing()) {
            overridePendingTransition(R.anim.none, R.anim.slide_to_right);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }
}
