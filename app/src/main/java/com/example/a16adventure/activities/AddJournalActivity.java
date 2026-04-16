package com.example.a16adventure.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a16adventure.R;
import com.example.a16adventure.models.JournalEntry;
import com.example.a16adventure.util.AppConstants;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AddJournalActivity extends AppCompatActivity {

    private ImageView ivSelectedImage, btnBack;
    private Button btnSave;
    private TextInputEditText etMonumentName, etNote;
    private MaterialCardView cardAddImage, btnPickDate;
    private View llPlaceholder;
    private ProgressBar pbSaving;
    private TextView tvSelectedDate;

    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    
    private DatabaseReference journalRef;
    private StorageReference storageRef;
    
    private String existingJournalId;
    private String existingImageUrl;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        initViews();
        setupFirebase();
        checkEditMode();
        setupImagePicker();
    }

    private void checkEditMode() {
        Intent intent = getIntent();
        if (intent.hasExtra("JOURNAL_ID")) {
            existingJournalId = intent.getStringExtra("JOURNAL_ID");
            etMonumentName.setText(intent.getStringExtra("MONUMENT_NAME"));
            etNote.setText(intent.getStringExtra("NOTE"));
            existingImageUrl = intent.getStringExtra(AppConstants.Extras.IMAGE_URL);
            long timestamp = intent.getLongExtra("TIMESTAMP", System.currentTimeMillis());
            calendar.setTimeInMillis(timestamp);
            updateDateLabel();

            if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                com.bumptech.glide.Glide.with(this).load(existingImageUrl).into(ivSelectedImage);
                ivSelectedImage.setVisibility(View.VISIBLE);
                llPlaceholder.setVisibility(View.GONE);
            }
            
            ((TextView)findViewById(R.id.tvHeader)).setText("Chỉnh sửa kỷ niệm");
            btnSave.setText("Cập nhật kỷ niệm");
        }
    }

    private void initViews() {
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSaveJournal);
        etMonumentName = findViewById(R.id.etMonumentName);
        etNote = findViewById(R.id.etNote);
        cardAddImage = findViewById(R.id.cardAddImage);
        llPlaceholder = findViewById(R.id.llAddImagePlaceholder);
        pbSaving = findViewById(R.id.pbSaving);
        btnPickDate = findViewById(R.id.btnPickDate);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        updateDateLabel();

        btnBack.setOnClickListener(v -> finish());
        
        cardAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        btnPickDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveJournal());
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateLabel() {
        tvSelectedDate.setText("Ngày đi: " + dateFormat.format(calendar.getTime()));
    }

    private void setupFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            journalRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid()).child("journals");
            storageRef = FirebaseStorage.getInstance().getReference("journals")
                    .child(user.getUid());
        } else {
            finish();
        }
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        ivSelectedImage.setImageURI(selectedImageUri);
                        ivSelectedImage.setVisibility(View.VISIBLE);
                        llPlaceholder.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void saveJournal() {
        String name = etMonumentName.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (name.isEmpty()) {
            etMonumentName.setError("Vui lòng nhập tên địa danh");
            return;
        }

        btnSave.setEnabled(false);
        pbSaving.setVisibility(View.VISIBLE);

        if (selectedImageUri != null) {
            uploadImageAndSave(name, note);
        } else {
            saveToDatabase(name, note, "");
        }
    }

    private void uploadImageAndSave(String name, String note) {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveToDatabase(name, note, uri.toString())))
                .addOnFailureListener(e -> {
                    pbSaving.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Tải ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToDatabase(String name, String note, String imageUrl) {
        String id = (existingJournalId != null) ? existingJournalId : journalRef.push().getKey();
        
        // Giữ lại ảnh cũ nếu không chọn ảnh mới khi sửa
        String finalImageUrl = (imageUrl.isEmpty() && existingImageUrl != null) ? existingImageUrl : imageUrl;
        
        JournalEntry entry = new JournalEntry(id, name, note, finalImageUrl, calendar.getTimeInMillis(), "");

        if (id != null) {
            journalRef.child(id).setValue(entry)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, existingJournalId != null ? "Đã cập nhật!" : "Đã lưu kỷ niệm!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        pbSaving.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        Toast.makeText(this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
