package net.spacive.bigfilefinder.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.spacive.bigfilefinder.databinding.ItemDirPathBinding;
import net.spacive.bigfilefinder.persistence.DirPathModel;

import java.util.List;

public class DirPathAdapter extends RecyclerView.Adapter<DirPathAdapter.DirPathViewHolder> {

    private List<DirPathModel> dataset;

    static class DirPathViewHolder extends RecyclerView.ViewHolder {

        private ItemDirPathBinding binding;

        DirPathViewHolder(@NonNull View itemView, ItemDirPathBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public DirPathViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemDirPathBinding binding = ItemDirPathBinding.inflate(inflater, parent, false);

        return new DirPathViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DirPathViewHolder holder, int position) {
        holder.binding.setDirPath(dataset.get(position));
    }

    @Override
    public int getItemCount() {

        if (dataset == null) {
            return 0;
        }

        return dataset.size();
    }

    public void setDataset(List<DirPathModel> dataset) {
        this.dataset = dataset;
        notifyDataSetChanged();
    }
}
