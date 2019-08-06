package net.spacive.bigfilefinder.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import net.spacive.bigfilefinder.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private DirPathAdapter dirPathAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        dirPathAdapter = new DirPathAdapter();

        binding.recyclerDirPaths.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerDirPaths.setAdapter(dirPathAdapter);
        binding.recyclerDirPaths.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
}
