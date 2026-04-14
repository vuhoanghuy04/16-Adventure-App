package com.example.a16adventure.domain.usecase;

import com.example.a16adventure.domain.repository.AuthRepository;

public class SignInUseCase {
    private final AuthRepository authRepository;

    public SignInUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String email, String password, AuthRepository.AuthCallback callback) {
        authRepository.signIn(email, password, callback);
    }
}
