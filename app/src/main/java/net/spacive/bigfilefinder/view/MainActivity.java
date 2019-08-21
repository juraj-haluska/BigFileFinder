package net.spacive.bigfilefinder.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import net.spacive.bigfilefinder.BigFileFinderApp;
import net.spacive.bigfilefinder.databinding.DialogPickNumberBinding;
import net.spacive.bigfilefinder.util.StorageManager;
import net.spacive.bigfilefinder.viewmodel.MainActivityViewModel;
import net.spacive.bigfilefinder.R;
import net.spacive.bigfilefinder.databinding.ActivityMainBinding;
import net.spacive.bigfilefinder.persistence.DirPathDao;
import net.spacive.bigfilefinder.persistence.DirPathModel;
import net.spacive.dirpickerdialog.DirPickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL = 50;

    private ActivityMainBinding binding;

    private DirPathAdapter dirPathAdapter;

    private MainActivityViewModel viewModel;

    private DirPathDao dirPathDao;

    private List<DirPathModel> dataSet;

    private Runnable requestedAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        BigFileFinderApp app = (BigFileFinderApp) getApplication();
        dirPathDao = app.getAppDatabase().dirPathDao();

        initDirRecycler();

        binding.toolbarSecondary.inflateMenu(R.menu.secondary_toolbar_menu);
        binding.toolbarSecondary.setOnMenuItemClickListener(this::onMenuItemClicked);

        initFinderServiceObservers();
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

    void initDirRecycler() {
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

        viewModel.getDirPaths().observe(this, dirPathModels -> {

            if (dirPathModels.isEmpty()) {
                binding.emptyState.setVisibility(View.VISIBLE);
            } else {
                binding.emptyState.setVisibility(View.GONE);
            }

            dirPathAdapter.setDataset(dirPathModels);
            this.dataSet = dirPathModels;
        });

        binding.fab.setOnClickListener(view -> showSelectStorageDialog());
    }

    void initFinderServiceObservers() {
        viewModel.isServiceBound().observe(this, isBound -> {
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
                    .setAlpha(isBound ? 100 : 255);
        });

        viewModel.getFinderServiceProgress().observe(this, s -> {
            binding.includedBottom.textStatus.setText(s);
        });

        viewModel.getResultsReady().observe(this, isReady -> {
            if (isReady) {
                Intent intent = new Intent(this, ViewResultsActivity.class);
                startActivity(intent);
            }
        });
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
                    if (!binding.textNumber.getText().toString().isEmpty()) {
                        int maxFiles = Integer.parseInt(binding.textNumber.getText().toString());
                        checkFilePermissions(() -> startFindingService(maxFiles));
                    }
                })
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    private void startFindingService(int maxFiles) {
        String [] dirs = new String[this.dataSet.size()];

        for (int i = 0; i < this.dataSet.size(); i++) {
            dirs[i] = this.dataSet.get(i).getPath();
        }

        viewModel.startFinderService(maxFiles, dirs);
        viewModel.bindFinderService();
    }

    private void checkFilePermissions(Runnable requestedAction) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showRationale();
            } else {
                requestFilePermissions();
            }

            this.requestedAction = requestedAction;
        } else {
            requestedAction.run();
        }
    }

    private void requestFilePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_WRITE_EXTERNAL);
    }

    private void showRationale() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_warning)
                .setMessage(R.string.message_rationale)
                .setPositiveButton(R.string.button_grant, (dialogInterface, i) -> {
                    requestFilePermissions();
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestedAction != null) {
                    requestedAction.run();
                }
            }
        }
    }

    private void showSelectStorageDialog() {
        checkFilePermissions(() -> {
            StorageManager manager = new StorageManager(this);

            List<String> external = manager.getExternalStorage();
            List<String> emulated = manager.getEmulatedStorage();

            List<String> merged = new ArrayList<>();
            merged.addAll(emulated);
            merged.addAll(external);

            String[] items = merged.toArray(new String[0]);

            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_storage_area)
                    .setItems(items, (dialogInterface, i) -> {
                        showBrowseFolderDialog(new File(merged.get(i)));
                    })
                    .show();
        });
    }

    private void showBrowseFolderDialog(File rootDir) {
        checkFilePermissions(() -> {
            new DirPickerDialog(this, rootDir, file -> {
                new Thread(() -> {
                    dirPathDao.addDirPathModel(new DirPathModel(file.getAbsolutePath()));
                }).start();
            }).show();
        });
    }
}
