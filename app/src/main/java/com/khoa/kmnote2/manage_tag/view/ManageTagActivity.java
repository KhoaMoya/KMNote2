package com.khoa.kmnote2.manage_tag.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.khoa.kmnote2.R;
import com.khoa.kmnote2.databinding.ActivityManageTagBinding;
import com.khoa.kmnote2.databinding.EditextDialogBinding;
import com.khoa.kmnote2.manage_tag.adapter.OnEditTagListener;
import com.khoa.kmnote2.manage_tag.viewmodel.ManageTagViewModel;
import com.khoa.kmnote2.model.Tag;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ManageTagActivity extends AppCompatActivity implements OnEditTagListener {

    private ActivityManageTagBinding mBinding;
    private ManageTagViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityManageTagBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mViewModel = new ViewModelProvider(this).get(ManageTagViewModel.class);
        if (savedInstanceState == null) {
            mViewModel.init(this);
            loadTagList();
        } else {
            mViewModel.disposable = new CompositeDisposable();
        }
        setupBinding();
    }

    private void loadTagList() {
        mViewModel.disposable.add(mViewModel.loadTag()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Tag>>() {
                    @Override
                    public void onSuccess(List<Tag> list) {
                        mViewModel.tagList.postValue((ArrayList<Tag>) list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(ManageTagActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void setupBinding() {
        mBinding.rcvTag.setAdapter(mViewModel.adapter);
        mViewModel.tagList.observe(this, new Observer<List<Tag>>() {
            @Override
            public void onChanged(List<Tag> list) {
                mViewModel.adapter.setTagList(list);
            }
        });
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    @Override
    public void onClickRename(Tag tag) {
        showRenameDialog(tag.name);
    }

    @Override
    public void onDeleteTag(final Tag tag) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa nhãn '" + tag.name + "' ?")
                .setMessage("Nếu muốn xóa tất cả ghi chú chứa nhãn này thì chọn 'Xóa cả ghi chú'.")
                .setPositiveButton("Xóa mỗi nhãn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mViewModel.deleteTag(tag);
                    }
                })
                .setNegativeButton("Xóa cả ghi chú", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mViewModel.deleteBothNote(tag);
                    }
                })
                .setNeutralButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
    }

    private void showRenameDialog(final String string) {
        final EditextDialogBinding binding = EditextDialogBinding.inflate(LayoutInflater.from(this));
        binding.edt.append(string);

        new AlertDialog.Builder(this)
                .setTitle("Chỉnh sửa nhãn")
                .setView(binding.getRoot())
                .setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newName = binding.edt.getText().toString();
                        if (!newName.equals(string)) {
                            mViewModel.renameTag(string, newName);
                        }
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        if (mViewModel.isEdited) {
            setResult(Activity.RESULT_OK);
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
        mViewModel.disposable.dispose();
    }
}
