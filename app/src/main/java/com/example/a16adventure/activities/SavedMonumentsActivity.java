package com.example.a16adventure.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a16adventure.R;
import com.example.a16adventure.adapters.SavedMonumentAdapter;
import com.example.a16adventure.models.Monument;
import com.example.a16adventure.presentation.auth.AuthViewModel;
import com.example.a16adventure.presentation.saved.SavedMonumentsViewModel;

import java.util.ArrayList;
import java.util.List;

public class SavedMonumentsActivity extends AppCompatActivity {
    private List<Monument> listDisplay = new ArrayList<>();
    private SavedMonumentAdapter adapter;
    private AuthViewModel authViewModel;
    private SavedMonumentsViewModel savedMonumentsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_monuments);

        // Nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        savedMonumentsViewModel = new ViewModelProvider(this).get(SavedMonumentsViewModel.class);

        // Kiểm tra đăng nhập
        String uid = authViewModel.getCurrentUserId();
        if (uid == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RecyclerView rv = findViewById(R.id.rvSavedMonuments);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SavedMonumentAdapter(this, listDisplay, position -> {
            Monument m = listDisplay.get(position);
            savedMonumentsViewModel.removeSavedMonument(uid, m.getId());
            listDisplay.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, listDisplay.size() - position);
        });

        rv.setAdapter(adapter);

        savedMonumentsViewModel.getSavedMonuments().observe(this, monuments -> {
            listDisplay.clear();
            if (monuments != null) {
                listDisplay.addAll(monuments);
            }
            adapter.notifyDataSetChanged();
        });

        savedMonumentsViewModel.getErrorMessage().observe(this, error ->
                Toast.makeText(this, "Lỗi tải danh sách đã lưu: " + error, Toast.LENGTH_SHORT).show()
        );

        savedMonumentsViewModel.observeSavedMonuments(uid);
    }
}
