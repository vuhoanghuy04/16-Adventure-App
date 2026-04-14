package com.example.a16adventure.domain.repository;

import com.example.a16adventure.domain.model.UserSession;

public interface AuthRepository {
    interface AuthCallback {
        void onSuccess(UserSession session);
        void onError(String message);
    }

    UserSession getCurrentUser();

    String getCurrentUserId();

    void signIn(String email, String password, AuthCallback callback);

    void register(String name, String email, String password, AuthCallback callback);

    void signOut();
}
