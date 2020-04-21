package com.khoa.kmnote2.manage_tag.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khoa.kmnote2.databinding.ItemManageTagBinding;
import com.khoa.kmnote2.model.Tag;

import java.util.List;

public class TagRcvAdapter extends RecyclerView.Adapter<TagRcvAdapter.TagViewHolder> {

    private List<Tag> tagList;
    private OnEditTagListener listener;

    public TagRcvAdapter(OnEditTagListener listener) {
        this.listener = listener;
    }

    public void setTagList(List<Tag> list){
        this.tagList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemManageTagBinding binding = ItemManageTagBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        ItemManageTagBinding binding;

        TagViewHolder(@NonNull ItemManageTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final Tag tag) {
            binding.txtTagName.setText(tag.name);
            binding.txtAmount.setText(tag.amount+"");
            binding.imgRename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null)
                    listener.onClickRename(tag);
                }
            });
            binding.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null)
                        listener.onDeleteTag(tag);
                }
            });
        }
    }
}
