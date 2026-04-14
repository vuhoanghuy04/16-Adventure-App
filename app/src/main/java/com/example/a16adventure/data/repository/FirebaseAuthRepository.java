package com.example.a16adventure.data.repository;

import com.example.a16adventure.domain.model.UserSession;
import com.example.a16adventure.domain.repository.AuthRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FirebaseAuthRepository implements AuthRepository {
    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthRepository() {
        this(FirebaseAuth.getInstance());
    }

    public FirebaseAuthRepository(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public UserSession getCurrentUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) return null;
        return new UserSession(user.getUid(), user.getDisplayName(), user.getEmail());
    }

    @Override
    public String getCurrentUserId() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    @Override
    public void signIn(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserSession session = getCurrentUser();
                        if (session != null) {
                            callback.onSuccess(session);
                        } else {
                            callback.onError("Không tìm thấy thông tin người dùng");
                        }
                    } else {
                        String message = task.getException() != null ? task.getException().getMessage() : "Lỗi đăng nhập";
                        callback.onError(message);
                    }
                });
    }

    @Override
    public void register(String name, String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        String message = task.getException() != null ? task.getException().getMessage() : "Lỗi đăng ký";
                        callback.onError(message);
                        return;
                    }

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        callback.onError("Không thể tạo phiên người dùng");
                        return;
                    }

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    UserSession session = getCurrentUser();
                                    if (session != null) {
                                        callback.onSuccess(session);
                                    } else {
                                        callback.onError("Không tìm thấy thông tin người dùng");
                                    }
                                } else {
                                    String message = updateTask.getException() != null ? updateTask.getException().getMessage() : "Lỗi cập nhật thông tin";
                                    callback.onError(message);
                                }
                            });
                });
    }

    @Override
    public void signOut() {
        firebaseAuth.signOut();
    }
}
