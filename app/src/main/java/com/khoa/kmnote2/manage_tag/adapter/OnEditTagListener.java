package com.khoa.kmnote2.manage_tag.adapter;

import com.khoa.kmnote2.model.Tag;

public interface OnEditTagListener {
    void onClickRename(Tag tag);
    void onDeleteTag(Tag tag);
}
