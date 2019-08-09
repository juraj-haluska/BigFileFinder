package net.spacive.bigfilefinder.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.spacive.bigfilefinder.databinding.ActivityViewResultsBinding;
import net.spacive.bigfilefinder.persistence.SearchResultModel;
import net.spacive.bigfilefinder.viewmodel.ViewResultsActivityViewModel;

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

        adapter = new ViewResultsAdapter();

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
}