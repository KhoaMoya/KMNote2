package com.khoa.kmnote2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.khoa.kmnote2.MyApplication;
import com.khoa.kmnote2.model.Note;
import com.khoa.kmnote2.model.Tag;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Util {

    public static boolean availableNetwork() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static String convertToTime(long timestamp) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String date = df.format(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        date = getDayOfWeek(i) + ",  " + date;
        return date;
    }

    public static String getDayOfWeek(int i) {
        switch (i) {
            case 1:
                return "Chủ nhật";
            case 2:
                return "Thứ hai";
            case 3:
                return "Thứ ba";
            case 4:
                return "Thứ tư";
            case 5:
                return "Thứ năm";
            case 6:
                return "Thứ sáu";
            case 7:
                return "Thứ bảy";
        }
        return "";
    }

    public static boolean isEmptyNote(Note note) {
        if (note == null) return true;
        boolean emptyTitle = note.title == null || note.title.isEmpty();
        boolean emptyContent = note.content == null || note.content.isEmpty();
        boolean emptyTag = note.tagList == null || note.tagList.isEmpty();
        return emptyTitle && emptyContent && emptyTag;
    }

    public static boolean equalNote(Note newNote, Note oldNote) {
        if (newNote.id != oldNote.id) return false;
        if (!newNote.title.equals(oldNote.title)) return false;
        if (!newNote.content.equals(oldNote.content)) return false;
        if (!equalTagList(newNote.tagList, oldNote.tagList)) return false;
        return true;
    }

    public static boolean equalNoteList(List<Note> list1, List<Note> list2){
        String str1 = fromList(list1);
        String str2 = fromList(list2);
        return str1.equals(str2);
    }

    public static boolean equalTagList(List<String> list1, List<String> list2) {
        String str1 = fromList(list1);
        String str2 = fromList(list2);
        return str1.equals(str2);
    }

    @TypeConverter
    public static <T> String fromList(List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static <T> List<T> fromString(String value) {
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static List<Tag> fromStringTagList(String value) {
        Type listType = new TypeToken<List<Tag>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
