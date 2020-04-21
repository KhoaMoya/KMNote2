package com.khoa.kmnote2.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.khoa.kmnote2.databinding.ItemNoteBinding;
import com.khoa.kmnote2.edit.adapter.SimpleTagRcvAdapter;
import com.khoa.kmnote2.model.Note;
import com.khoa.kmnote2.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.khoa.kmnote2.MyApplication.getContext;

public class NoteRcvAdapter extends RecyclerView.Adapter<NoteRcvAdapter.NoteViewHolder> {

    private List<Note> noteList;
    private OnNoteClickListener listener;

    public NoteRcvAdapter() {
    }

    public void setNoteList(List<Note> list) {
        if(!Util.equalNoteList(list, this.noteList)) {
            this.noteList = new ArrayList<>(list);
            notifyDataSetChanged();
        }
    }

    public List<Note> getNoteList() {
        return noteList;
    }

    public void setListener(OnNoteClickListener listener) {
        this.listener = listener;
    }

    public int deleteNote(Note note) {
        int position = noteList.indexOf(note);
        if (position >= 0) {
            noteList.remove(note);
            notifyItemRemoved(position);
        }
        return position;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteBinding binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(noteList.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return noteList == null ? 0 : noteList.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        ItemNoteBinding binding;
        Note note;

        NoteViewHolder(@NonNull ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bind(final Note note) {
            this.note = note;
            binding.txtTime.setText(Util.convertToTime(note.updateAt));

            bindRecycleViewTag(note.tagList);

            if (note.title.isEmpty()) {
                binding.txtTitle.setVisibility(View.GONE);
            } else {
                binding.txtTitle.setVisibility(View.VISIBLE);
                binding.txtTitle.setText(note.title);
            }

            if (note.content.isEmpty()) {
                binding.txtContent.setVisibility(View.GONE);
            } else {
                binding.txtContent.setVisibility(View.VISIBLE);
                binding.txtContent.setText(note.content);
            }

            binding.cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onClickNote(note);
                    }
                }
            });
        }

        private void bindRecycleViewTag(List<String> list) {
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
            layoutManager.setFlexWrap(FlexWrap.WRAP);
            layoutManager.setFlexDirection(FlexDirection.ROW);

            binding.rcvTag.setLayoutManager(layoutManager);
            binding.rcvTag.setAdapter(new SimpleTagRcvAdapter(list));
        }

        public Note getNote() {
            return note;
        }
    }
}
