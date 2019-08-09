package net.spacive.bigfilefinder.view;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeLeftRightCallback extends ItemTouchHelper.SimpleCallback {

    public interface OnSwipedCallback {

        void onItemSwiped(int itemPosition);
    }

    private OnSwipedCallback callback;

    public SwipeLeftRightCallback(OnSwipedCallback callback) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.callback = callback;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (callback != null) {
            callback.onItemSwiped(viewHolder.getAdapterPosition());
        }
    }
}
