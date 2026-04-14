package com.example.a16adventure.data.repository;

import androidx.annotation.NonNull;

import com.example.a16adventure.domain.repository.SavedRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseSavedRepository implements SavedRepository {
    private static volatile FirebaseSavedRepository instance;

    private ValueEventListener valueEventListener;
    private DatabaseReference observedReference;

    public static FirebaseSavedRepository getInstance() {
        if (instance == null) {
            synchronized (FirebaseSavedRepository.class) {
                if (instance == null) {
                    instance = new FirebaseSavedRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public void observeSavedIds(String uid, SavedIdsCallback callback) {
        clearObserver();

        observedReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("saved_ids");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> ids = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey() != null) {
                        ids.add(child.getKey());
                    }
                }
                callback.onSuccess(ids);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        };

        observedReference.addValueEventListener(valueEventListener);
    }

    @Override
    public void setSaved(String uid, String monumentId, boolean saved) {
        DatabaseReference savedRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("saved_ids")
                .child(monumentId);

        if (saved) {
            savedRef.setValue(true);
        } else {
            savedRef.removeValue();
        }
    }

    @Override
    public void removeSaved(String uid, String monumentId) {
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("saved_ids")
                .child(monumentId)
                .removeValue();
    }

    @Override
    public void clearObserver() {
        if (observedReference != null && valueEventListener != null) {
            observedReference.removeEventListener(valueEventListener);
        }
        observedReference = null;
        valueEventListener = null;
    }
}
