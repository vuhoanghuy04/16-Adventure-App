package com.example.a16adventure.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull; // Fix lỗi NonNull
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a16adventure.R;
import com.example.a16adventure.adapters.SavedMonumentAdapter;
import com.example.a16adventure.models.Monument;
import com.example.a16adventure.models.MonumentDataManager; // Fix lỗi MonumentDataManager

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SavedMonumentsActivity extends AppCompatActivity {
    private List<Monument> listDisplay = new ArrayList<>();
    private SavedMonumentAdapter adapter;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_monuments);

        // Nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Kiểm tra đăng nhập
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("saved_ids");

        RecyclerView rv = findViewById(R.id.rvSavedMonuments);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SavedMonumentAdapter(this, listDisplay, position -> {
            Monument m = listDisplay.get(position);
            userRef.child(m.getId()).removeValue();
            listDisplay.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, listDisplay.size() - position);
        });

        rv.setAdapter(adapter);

        loadSavedDataFromFirebase();
    }

    // SavedMonumentsActivity.java
    private void loadSavedDataFromFirebase() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listDisplay.clear();
                MonumentDataManager manager = MonumentDataManager.getInstance();

                Log.d("DEBUG_SAVE", "Tim thay " + snapshot.getChildrenCount() + " ID tren Firebase");

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String savedId = ds.getKey();
                    Monument m = manager.getMonumentById(savedId);

                    if (m != null) {
                        listDisplay.add(m);
                        Log.d("DEBUG_SAVE", "Khớp thành công: " + m.getName());
                    } else {
                        Log.e("DEBUG_SAVE", "KHÔNG tìm thấy địa danh cho ID: " + savedId);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}