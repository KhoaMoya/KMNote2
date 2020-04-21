package com.khoa.kmnote2.select_tag.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.khoa.kmnote2.R;
import com.khoa.kmnote2.databinding.ActivitySelectTagBinding;
import com.khoa.kmnote2.model.Note;
import com.khoa.kmnote2.select_tag.viewmodel.SelectTagViewModel;
import com.khoa.kmnote2.util.OnTextChangeListener;
import com.khoa.kmnote2.util.Util;

import java.util.ArrayList;

public class SelectTagActivity extends AppCompatActivity {

    private ActivitySelectTagBinding mBinding;
    private SelectTagViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySelectTagBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mViewModel = new ViewModelProvider(this).get(SelectTagViewModel.class);
        if (savedInstanceState == null) {
            mViewModel.init(getDataIntent());
        }
        setupBinding();
        showTagList();
    }

    private void showTagList() {
        mViewModel.showTagList();
    }

    private Note getDataIntent() {
        Note note = (Note) getIntent().getSerializableExtra("note");
        if (note == null) note = new Note();
        return note;
    }

    private void setupBinding() {
        mBinding.rcvTag.setAdapter(mViewModel.tagRcvAdapter);
        mBinding.edtTag.addTextChangedListener(new OnTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newTag = charSequence.toString();
                mBinding.txtCreateTag.setText("Tạo nhãn '" + newTag + "'");
                mViewModel.showTagList(newTag);
            }
        });

        mBinding.btnCreateTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCreateTag();
            }
        });
    }

    private void onClickCreateTag() {
        mViewModel.createNewTag(mBinding.edtTag.getText().toString());
        mBinding.edtTag.setText("");
        mBinding.edtTag.clearFocus();
        Util.hideKeyboard(mBinding.edtTag);
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (!Util.equalTagList(mViewModel.note.tagList, mViewModel.selectedTagList)) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("tag_list", (ArrayList<String>) mViewModel.selectedTagList);
            setResult(Activity.RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
