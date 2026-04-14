package com.example.a16adventure.domain.usecase;

import com.example.a16adventure.domain.repository.SavedRepository;

public class ToggleSavedMonumentUseCase {
    private final SavedRepository savedRepository;

    public ToggleSavedMonumentUseCase(SavedRepository savedRepository) {
        this.savedRepository = savedRepository;
    }

    public void execute(String uid, String monumentId, boolean newSavedState) {
        savedRepository.setSaved(uid, monumentId, newSavedState);
    }

    public void remove(String uid, String monumentId) {
        savedRepository.removeSaved(uid, monumentId);
    }
}
