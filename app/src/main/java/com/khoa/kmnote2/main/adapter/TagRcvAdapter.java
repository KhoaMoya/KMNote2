package com.khoa.kmnote2.main.adapter;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.khoa.kmnote2.MyApplication;
import com.khoa.kmnote2.R;
import com.khoa.kmnote2.databinding.ItemTagAmountBinding;
import com.khoa.kmnote2.model.Tag;

import java.util.List;

public class TagRcvAdapter extends RecyclerView.Adapter<TagRcvAdapter.TagViewHolder> {

    private List<Tag> tagList;
    private OnTagClickListener listener;
    private String selectedTag;

    public TagRcvAdapter() {
    }

    public void setTagList(List<Tag> list, String selectedTag) {
        this.tagList = list;
        this.selectedTag = selectedTag;
        notifyDataSetChanged();
    }

    public void setListener(OnTagClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTagAmountBinding binding = ItemTagAmountBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TagViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final TagViewHolder holder, int position) {
        holder.bind(tagList.get(holder.getAdapterPosition()));
//        if(position != 0) {
//            holder.binding.getRoot().setVisibility(View.INVISIBLE);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    holder.binding.getRoot().setVisibility(View.VISIBLE);
//                    holder.binding.getRoot().startAnimation(AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.slide_to_left));
//                }
//            }, position * 70);
//        }
    }

    @Override
    public int getItemCount() {
        return tagList == null ? 0 : tagList.size();
    }

    class TagViewHolder extends RecyclerView.ViewHolder {
        ItemTagAmountBinding binding;

        TagViewHolder(@NonNull ItemTagAmountBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bind(final Tag tag) {
            binding.txtName.setText(tag.name);
            binding.txtAmount.setText(tag.amount + "");

            if (tag.name.equals(selectedTag)) {
                binding.getRoot().setSelected(true);
                binding.txtName.setTextColor(ContextCompat.getColor(MyApplication.getContext(), R.color.colorWhite));
            } else {
                binding.getRoot().setSelected(false);
                binding.txtName.setTextColor(ContextCompat.getColor(MyApplication.getContext(), R.color.colorBlack));
            }
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        setSelectedTag(tag.name);
                        listener.onClickTag(tag);
                    }
                }
            });
        }
    }

    private void setSelectedTag(String name) {
        selectedTag = name;
        notifyDataSetChanged();
    }
}
