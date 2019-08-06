package net.spacive.bigfilefinder.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;

import net.spacive.bigfilefinder.BigFileFinderApp;
import net.spacive.bigfilefinder.MainActivityViewModel;
import net.spacive.bigfilefinder.R;
import net.spacive.bigfilefinder.databinding.ActivityMainBinding;
import net.spacive.bigfilefinder.persistence.DirPathDao;
import net.spacive.bigfilefinder.persistence.DirPathModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private DirPathAdapter dirPathAdapter;

    private MainActivityViewModel viewModel;

    private DirPathDao dirPathDao;

    static int itemCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        BigFileFinderApp app = (BigFileFinderApp) getApplication();
        dirPathDao = app.getAppDatabase().dirPathDao();

        dirPathAdapter = new DirPathAdapter();

        binding.recyclerDirPaths.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerDirPaths.setAdapter(dirPathAdapter);
        binding.recyclerDirPaths.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        viewModel.getDirPaths().observe(this, dirPathAdapter::setDataset);

        binding.fab.setOnClickListener(view -> {
            new Thread(() -> {
                dirPathDao.addDirPathModel(new DirPathModel("item " + itemCounter++));
            }).start();
        });

        binding.toolbarSecondary.inflateMenu(R.menu.secondary_toolbar_menu);
        binding.toolbarSecondary.setOnMenuItemClickListener(this::onMenuItemClicked);
    }

    private boolean onMenuItemClicked(MenuItem item) {
        if (item.getItemId() == R.id.menu_clear) {
            new Thread(() -> dirPathDao.deleteAllDirPaths()).start();
            return true;
        }

        return false;
    }
}
