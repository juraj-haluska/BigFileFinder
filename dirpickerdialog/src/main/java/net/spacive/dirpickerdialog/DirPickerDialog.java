package net.spacive.dirpickerdialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.spacive.dirpickerdialog.databinding.DialogDirPickerBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirPickerDialog extends Dialog {

    private File rootDir;
    private DirItemModelAdapter dirItemModelAdapter;
    private List<DirItemModel> dataSet;


    private DirPickerDialog(Context context) {
        super(context);

        DialogDirPickerBinding binding = DialogDirPickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.dirItemModelAdapter = new DirItemModelAdapter();

        binding.recyclerDirItems.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerDirItems.setAdapter(dirItemModelAdapter);
        binding.recyclerDirItems.addItemDecoration(
                new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        this.dataSet = new ArrayList<>();

        for (File f: Environment.getExternalStorageDirectory().listFiles()) {
            Log.d("KKT", "listing " + f.getName());
            if (f.isDirectory()) {
                dataSet.add(new DirItemModel(f));
            }
        }

        dirItemModelAdapter.setDataset(dataSet);

        setOnShowListener(dialogInterface -> {
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        });
    }

    public static class Builder {

        private DirPickerDialog dialog;

        public Builder(Context context) {
            dialog = new DirPickerDialog(context);
            Log.d("KKT", Boolean.toString(context.getFilesDir() == null));
            dialog.rootDir = context.getFilesDir();
        }

        public Builder addRootDir(File rootDir) {
            dialog.rootDir = rootDir;
            return this;
        }

        public DirPickerDialog build() {
            return dialog;
        }
    }


}
