package net.spacive.bigfilefinder.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.spacive.bigfilefinder.databinding.ItemResultBinding;
import net.spacive.bigfilefinder.persistence.SearchResultModel;

import java.util.List;

public class ViewResultsAdapter extends RecyclerView.Adapter<ViewResultsAdapter.ViewResultsHolder> {

    private List<SearchResultModel> dataset;

    static class ViewResultsHolder extends RecyclerView.ViewHolder {

        private ItemResultBinding binding;

        ViewResultsHolder(@NonNull View itemView, ItemResultBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewResultsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemResultBinding binding = ItemResultBinding.inflate(inflater, parent, false);

        return new ViewResultsHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewResultsHolder holder, int position) {
        holder.binding.setResult(dataset.get(position));
    }

    @Override
    public int getItemCount() {

        if (dataset == null) {
            return 0;
        }

        return dataset.size();
    }

    public void setDataset(List<SearchResultModel> dataset) {
        this.dataset = dataset;
        notifyDataSetChanged();
    }
}
