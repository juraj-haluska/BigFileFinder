package net.spacive.dirpickerdialog;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.spacive.dirpickerdialog.databinding.DialogDirPickerBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirPickerDialog extends Dialog implements DirItemModelAdapter.DirExplorerLogicProvider {

    private final File rootDir;
    private File enteredDir;
    private DirItemModelAdapter dirItemModelAdapter;
    private DialogDirPickerBinding binding;
    private OnFolderSelectedListener folderSelectedListener;

    public interface OnFolderSelectedListener {
        void onFolderSelected(File file);
    }

    public DirPickerDialog(@NonNull Context context, @NonNull File rootDir,
                           OnFolderSelectedListener listener) {
        super(context);

        this.rootDir = rootDir;
        this.enteredDir = rootDir;

        this.folderSelectedListener = listener;

        binding = DialogDirPickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setOnShowListener(dialogInterface -> {
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        });

        this.dirItemModelAdapter = new DirItemModelAdapter();

        binding.recyclerDirItems.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerDirItems.setAdapter(dirItemModelAdapter);
        binding.recyclerDirItems.addItemDecoration(
                new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        binding.footer.textCancel.setOnClickListener(view -> dismiss());
        binding.footer.textSelect.setOnClickListener(view -> onFolderSelected());

        populateFolderChildren(rootDir);
        setCurrentPathTitle(rootDir);

        dirItemModelAdapter.setDirExplorerLogicProvider(this);
    }

    @Override
    public boolean canGoBack() {
        return isFolderChild(rootDir, enteredDir);
    }

    @Override
    public void goBack() {
        File parent = enteredDir.getParentFile();

        if (parent != null) {
            enteredDir = parent;
            populateFolderChildren(parent);
            setCurrentPathTitle(parent);
        }
    }

    @Override
    public void onFolderEntered(File file) {
        this.enteredDir = file;
        populateFolderChildren(file);
        setCurrentPathTitle(file);
    }

    private void onFolderSelected() {
        if (folderSelectedListener != null) {
            folderSelectedListener.onFolderSelected(enteredDir);
            dismiss();
        }
    }

    private void populateFolderChildren(@NonNull File folder) {
        List<DirItemModel> dataSet = new ArrayList<>();

        File[] files = folder.listFiles();

        if (files != null) {
            for (File f : files) {
                if (f.isDirectory() && f.canRead()) {
                    dataSet.add(new DirItemModel(f));
                }
            }
        }

        dirItemModelAdapter.setDataset(dataSet);
    }

    private void setCurrentPathTitle(File file) {
        binding.header.textPath.setText(file.getAbsolutePath());
    }

    private boolean isFolderChild(File possibleParent, File possibleChild) {
        if (possibleChild.getAbsolutePath().equals(possibleParent.getAbsolutePath())) {
            return false;
        }

        return possibleChild.getAbsolutePath().startsWith(possibleParent.getAbsolutePath());
    }
}
