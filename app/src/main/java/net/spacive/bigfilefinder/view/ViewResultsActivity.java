package net.spacive.bigfilefinder.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import net.spacive.bigfilefinder.R;
import net.spacive.bigfilefinder.databinding.ActivityViewResultsBinding;
import net.spacive.bigfilefinder.persistence.SearchResultModel;
import net.spacive.bigfilefinder.viewmodel.ViewResultsActivityViewModel;

import java.io.File;
import java.util.List;

public class ViewResultsActivity extends AppCompatActivity {

    private ActivityViewResultsBinding binding;

    private ViewResultsActivityViewModel viewModel;

    private ViewResultsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewResultsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = ViewModelProviders.of(this).get(ViewResultsActivityViewModel.class);

        adapter = new ViewResultsAdapter(this::showResultDialog);

        binding.recyclerResults.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerResults.setAdapter(adapter);
        binding.recyclerResults.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            List<SearchResultModel> results = viewModel.getSearchResults();
            runOnUiThread(() -> adapter.setDataset(results));
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void showResultDialog(SearchResultModel model) {

        new AlertDialog.Builder(this)
                .setTitle(model.getName())
                .setItems(getResources().getStringArray(R.array.items_file_action), (dialogInterface, i) -> {
                    if (i == 0) {
                        onDeleteFile(model);
                    } else if (i == 1) {
                        onShowFullPath(model.getPath());
                    }
                })
                .setNeutralButton(R.string.button_cancel, null)
                .show();
    }

    private void onDeleteFile(SearchResultModel model) {
        File file = new File(model.getPath());

        boolean result = false;

        if (file.exists()) {
            result = file.delete();
        }

        if (result) {
            Snackbar.make(binding.getRoot(),
                    getString(R.string.snack_file_deleted), Snackbar.LENGTH_SHORT)
                    .show();

            new Thread(() -> {
                viewModel.deleteSearchResult(model);
                runOnUiThread(this::loadData);
            }).start();

        } else {
            Snackbar.make(binding.getRoot(),
                    getString(R.string.snack_file_error), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private void onShowFullPath(String path) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_result_detail)
                .setMessage(path)
                .setNeutralButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.button_copy, (dialogInterface, i) -> {
                    onCopyToClipboard(path);
                })
                .show();
    }

    private void onCopyToClipboard(String path) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.label_clipboard), path);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
    }
}
