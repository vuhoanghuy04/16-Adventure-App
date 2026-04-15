package com.example.a16adventure.domain.usecase;

import com.example.a16adventure.domain.repository.AuthRepository;

public class RegisterUseCase {
    private final AuthRepository authRepository;

    public RegisterUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String name, String email, String password, AuthRepository.AuthCallback callback) {
        authRepository.register(name, email, password, callback);
    }
}
