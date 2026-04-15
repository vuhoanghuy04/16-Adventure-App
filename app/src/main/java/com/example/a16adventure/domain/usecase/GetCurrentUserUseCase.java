package com.example.a16adventure.domain.usecase;

import com.example.a16adventure.domain.model.UserSession;
import com.example.a16adventure.domain.repository.AuthRepository;

public class GetCurrentUserUseCase {
    private final AuthRepository authRepository;

    public GetCurrentUserUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public UserSession execute() {
        return authRepository.getCurrentUser();
    }

    public String executeUid() {
        return authRepository.getCurrentUserId();
    }
}
