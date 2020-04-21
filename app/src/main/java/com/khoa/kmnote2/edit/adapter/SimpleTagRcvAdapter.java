package com.khoa.kmnote2.edit.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khoa.kmnote2.databinding.ItemSimpleTagBinding;

import java.util.List;

public class SimpleTagRcvAdapter extends RecyclerView.Adapter<SimpleTagRcvAdapter.TagViewHolder> {

    private List<String> tagList;

    public SimpleTagRcvAdapter() {
    }

    public SimpleTagRcvAdapter(List<String> tagList) {
        this.tagList = tagList;
    }

    public void setTagList(List<String> list) {
        tagList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSimpleTagBinding binding = ItemSimpleTagBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TagViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.bind(tagList.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return tagList == null ? 0 : tagList.size();
    }

    class TagViewHolder extends RecyclerView.ViewHolder {

        ItemSimpleTagBinding binding;

        public TagViewHolder(@NonNull ItemSimpleTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String name) {
            binding.txtName.setText(name);
        }
    }
}
