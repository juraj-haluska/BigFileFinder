package net.spacive.dirpickerdialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.spacive.dirpickerdialog.databinding.ItemBackBinding;
import net.spacive.dirpickerdialog.databinding.ItemDirectoryBinding;

import java.io.File;
import java.util.List;

public class DirItemModelAdapter extends RecyclerView.Adapter<DirItemModelAdapter.GenericBindingViewHolder> {

    private static final int VIEW_TYPE_FIRST = 0;
    private static final int VIEW_TYPE_SECOND = 1;

    private final int listOffset = 1;

    private List<DirItemModel> dataset;

    private DirExplorerLogicProvider dirExplorerLogicProvider;

    public interface DirExplorerLogicProvider {
        boolean canGoBack();

        void goBack();

        void onFolderEntered(File file);
    }

    static class GenericBindingViewHolder<T> extends RecyclerView.ViewHolder {

        private T binding;

        public GenericBindingViewHolder(@NonNull View itemView, T binding) {
            super(itemView);
            this.binding = binding;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < listOffset) {
            return VIEW_TYPE_FIRST;
        }

        return VIEW_TYPE_SECOND;
    }

    @NonNull
    @Override
    public GenericBindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_FIRST) {
            ItemBackBinding binding = ItemBackBinding.inflate(inflater, parent, false);
            return new GenericBindingViewHolder<>(binding.getRoot(), binding);
        } else {
            ItemDirectoryBinding binding = ItemDirectoryBinding.inflate(inflater, parent, false);
            return new GenericBindingViewHolder<>(binding.getRoot(), binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GenericBindingViewHolder holder, int position) {
        if (position < listOffset) {
            ItemBackBinding binding = (ItemBackBinding) holder.binding;

            if (dirExplorerLogicProvider != null && dirExplorerLogicProvider.canGoBack()) {
                binding.getRoot().setVisibility(View.VISIBLE);
                binding.layoutBack.setOnClickListener(view -> dirExplorerLogicProvider.goBack());
            } else {
                binding.getRoot().setVisibility(View.GONE);
            }
        } else {
            DirItemModel model = dataset.get(position - listOffset);

            ItemDirectoryBinding binding = (ItemDirectoryBinding) holder.binding;
            binding.setDirItem(model);

            if (dirExplorerLogicProvider != null) {
                binding.getRoot().setOnClickListener(view -> {
                    dirExplorerLogicProvider.onFolderEntered(model.getFile());
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (dataset == null) {
            return listOffset;
        }

        return dataset.size() + listOffset;
    }

    public void setDataset(List<DirItemModel> dataset) {
        this.dataset = dataset;
        this.notifyDataSetChanged();
    }

    public void setDirExplorerLogicProvider(DirExplorerLogicProvider dirExplorerLogicProvider) {
        this.dirExplorerLogicProvider = dirExplorerLogicProvider;
    }
}
