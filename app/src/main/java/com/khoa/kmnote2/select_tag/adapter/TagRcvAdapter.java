package com.khoa.kmnote2.select_tag.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khoa.kmnote2.databinding.ItemSelectTagBinding;

import java.util.List;

public class TagRcvAdapter extends RecyclerView.Adapter<TagRcvAdapter.TagViewHolder> {

    private List<String> allTagList;
    private List<String> selectedTagList;

    public TagRcvAdapter() {
    }

    public void setTagList(List<String> allTag, List<String> selectedTag) {
        this.allTagList = allTag;
        this.selectedTagList = selectedTag;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSelectTagBinding binding = ItemSelectTagBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TagViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.bind(allTagList.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return allTagList == null ? 0 : allTagList.size();
    }

    class TagViewHolder extends RecyclerView.ViewHolder {

        ItemSelectTagBinding binding;

        public TagViewHolder(@NonNull ItemSelectTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final String tagName) {
            binding.txtTagName.setText(tagName);
            if(selectedTagList.contains(tagName)) binding.checkbox.setChecked(true);

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean selected = !binding.checkbox.isChecked();
                    binding.checkbox.setChecked(selected);
                    if(!selected) {
                        selectedTagList.remove(tagName);
                    }
                    else {
                        selectedTagList.add(tagName);
                    }
                }
            });
        }
    }
}
