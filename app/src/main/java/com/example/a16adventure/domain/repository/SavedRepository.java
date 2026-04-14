package com.example.a16adventure.domain.repository;

import java.util.List;

public interface SavedRepository {
    interface SavedIdsCallback {
        void onSuccess(List<String> savedIds);
        void onError(String message);
    }

    void observeSavedIds(String uid, SavedIdsCallback callback);

    void setSaved(String uid, String monumentId, boolean saved);

    void removeSaved(String uid, String monumentId);

    void clearObserver();
}
