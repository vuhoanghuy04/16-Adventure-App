package com.example.a16adventure.domain.usecase;

import com.example.a16adventure.domain.repository.AuthRepository;

public class SignOutUseCase {
    private final AuthRepository authRepository;

    public SignOutUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute() {
        authRepository.signOut();
    }
}
