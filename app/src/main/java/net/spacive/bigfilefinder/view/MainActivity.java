package net.spacive.bigfilefinder.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;

import net.spacive.bigfilefinder.BigFileFinderApp;
import net.spacive.bigfilefinder.databinding.DialogPickNumberBinding;
import net.spacive.bigfilefinder.viewmodel.MainActivityViewModel;
import net.spacive.bigfilefinder.R;
import net.spacive.bigfilefinder.databinding.ActivityMainBinding;
import net.spacive.bigfilefinder.persistence.DirPathDao;
import net.spacive.bigfilefinder.persistence.DirPathModel;
import net.spacive.dirpickerdialog.DirPickerDialog;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

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

        viewModel.isFinderServiceBound().observe(this, isBound -> {
            if (isBound) {
                binding.includedBottom.bottomSheet.setVisibility(View.VISIBLE);
                binding.fab.hide();
            } else {
                binding.includedBottom.bottomSheet.setVisibility(View.GONE);
                binding.fab.show();
            }

            binding.toolbarSecondary.getMenu().findItem(R.id.menu_search)
                    .setEnabled(!isBound)
                    .getIcon()
                    .setAlpha(isBound? 100 : 255);
        });

        viewModel.getFinderServiceProgress().observe(this, s -> {
            binding.includedBottom.textStatus.setText(s);
        });
    }

    @Override
    protected void onResume() {
        viewModel.bindFinderService();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        viewModel.unbindFinderService();
        super.onDestroy();
    }

    private boolean onMenuItemClicked(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.menu_clear: {
                new Thread(() -> dirPathDao.deleteAllDirPaths()).start();
                return true;
            }
            case R.id.menu_search: {
                showNumberPickerDialog();
                return true;
            }
        }

        return false;
    }

    private void showNumberPickerDialog() {
        DialogPickNumberBinding binding = DialogPickNumberBinding.inflate(getLayoutInflater());

        new AlertDialog.Builder(this)
                .setView(binding.getRoot())
                .setMessage(R.string.message_select_number)
                .setPositiveButton(R.string.button_start, (dialogInterface, i) -> {
                    int maxFiles = Integer.parseInt(binding.textNumber.getText().toString());
                    startFindingService(maxFiles);
                })
                .setNegativeButton(R.string.button_cancel,null)
                .show();
    }

    private void startFindingService(int maxFiles) {
        File rootDir = Environment.getExternalStorageDirectory();

        viewModel.startFinderService(maxFiles, new String[]{rootDir.getAbsolutePath()});
        viewModel.bindFinderService();
    }
}
