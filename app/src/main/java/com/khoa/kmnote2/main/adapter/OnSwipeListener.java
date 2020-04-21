package com.khoa.kmnote2.main.adapter;

import com.khoa.kmnote2.model.Note;

public interface OnSwipeListener {
    void onSwipedLeftListener(Note note);
    void onSwipedRightListener(Note note);
}
