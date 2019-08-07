package net.spacive.bigfilefinder.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;

import net.spacive.bigfilefinder.BigFileFinderApp;
import net.spacive.bigfilefinder.viewmodel.MainActivityViewModel;
import net.spacive.bigfilefinder.R;
import net.spacive.bigfilefinder.databinding.ActivityMainBinding;
import net.spacive.bigfilefinder.persistence.DirPathDao;
import net.spacive.bigfilefinder.persistence.DirPathModel;
import net.spacive.dirpickerdialog.DirPickerDialog;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private DirPathAdapter dirPathAdapter;

    private MainActivityViewModel viewModel;

    private DirPathDao dirPathDao;

    private List<DirPathModel> dataSet;

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

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeLeftRightCallback(itemPosition -> {
                    if (this.dataSet != null && itemPosition < dataSet.size()) {
                        new Thread(() -> {
                            dirPathDao.deleteDirPathModel(dataSet.get(itemPosition));
                        }).start();
                    }
        }));
        itemTouchHelper.attachToRecyclerView(binding.recyclerDirPaths);

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        viewModel.getDirPaths().observe(this, dirPathModels -> {
            dirPathAdapter.setDataset(dirPathModels);
            this.dataSet = dirPathModels;
        });

        binding.fab.setOnClickListener(view -> {
            new DirPickerDialog(this, Environment.getExternalStorageDirectory(), file -> {
                new Thread(() -> {
                    dirPathDao.addDirPathModel(new DirPathModel(file.getAbsolutePath()));
                }).start();
            }).show();
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
