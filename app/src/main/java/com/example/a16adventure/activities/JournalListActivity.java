package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a16adventure.R;
import com.example.a16adventure.models.JournalEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JournalListActivity extends BaseActivity {

    private RecyclerView rvJournals;
    private FloatingActionButton fabAddJournal;
    private TextView tvEmpty;
    private JournalAdapter adapter;
    private List<JournalEntry> journalEntries;
    private DatabaseReference userJournalRef;
    private TextView tvJournalCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        initViews();
        setupFirebase();
        loadJournals();
    }

    private void initViews() {
        rvJournals = findViewById(R.id.rvJournals);
        fabAddJournal = findViewById(R.id.fabAddJournal);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvJournalCount = findViewById(R.id.tvJournalCount);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        journalEntries = new ArrayList<>();
        adapter = new JournalAdapter(journalEntries);
        rvJournals.setLayoutManager(new LinearLayoutManager(this));
        rvJournals.setAdapter(adapter);

        fabAddJournal.setOnClickListener(v -> {
            startActivity(new Intent(this, AddJournalActivity.class));
        });
    }

    private void setupFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userJournalRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid()).child("journals");
        } else {
            finish();
        }
    }

    private void loadJournals() {
        if (userJournalRef == null) return;

        userJournalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                journalEntries.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    JournalEntry entry = data.getValue(JournalEntry.class);
                    if (entry != null) {
                        journalEntries.add(entry);
                    }
                }
                Collections.reverse(journalEntries); // Hiển thị mới nhất lên đầu
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(journalEntries.isEmpty() ? View.VISIBLE : View.GONE);
                if (tvJournalCount != null) {
                    tvJournalCount.setText(journalEntries.size() + " kỷ niệm");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(JournalListActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ADAPTER NỘI BỘ
    private class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder> {
        private List<JournalEntry> list;

        public JournalAdapter(List<JournalEntry> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_journal, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            JournalEntry entry = list.get(position);
            holder.tvMonument.setText(entry.getMonumentName());
            holder.tvNote.setText(entry.getNote());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(JournalListActivity.this, AddJournalActivity.class);
                intent.putExtra("JOURNAL_ID", entry.getId());
                intent.putExtra("MONUMENT_NAME", entry.getMonumentName());
                intent.putExtra("NOTE", entry.getNote());
                intent.putExtra("IMAGE_URL", entry.getImageUrl());
                intent.putExtra("TIMESTAMP", entry.getTimestamp());
                startActivity(intent);
            });

            ImageView btnDelete = holder.itemView.findViewById(R.id.btnDeleteJournal);
            btnDelete.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(JournalListActivity.this)
                        .setTitle("Xóa kỷ niệm")
                        .setMessage("Bạn có chắc chắn muốn xóa kỷ niệm này không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            if (userJournalRef != null && entry.getId() != null) {
                                userJournalRef.child(entry.getId()).removeValue()
                                        .addOnSuccessListener(aVoid -> Toast.makeText(JournalListActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show());
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });

            SimpleDateFormat sdf = new SimpleDateFormat("dd 'tháng' MM, yyyy", new Locale("vi", "VN"));
            holder.tvDate.setText(sdf.format(new Date(entry.getTimestamp())));

            if (entry.getImageUrl() != null && !entry.getImageUrl().isEmpty()) {
                holder.ivImage.setVisibility(View.VISIBLE);
                Glide.with(holder.itemView.getContext()).load(entry.getImageUrl()).into(holder.ivImage);
            } else {
                holder.ivImage.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvMonument, tvDate, tvNote;
            ImageView ivImage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvMonument = itemView.findViewById(R.id.tvJournalMonument);
                tvDate = itemView.findViewById(R.id.tvJournalDate);
                tvNote = itemView.findViewById(R.id.tvJournalNote);
                ivImage = itemView.findViewById(R.id.ivJournalImage);
            }
        }
    }
}
