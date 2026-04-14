package com.example.a16adventure.domain.usecase;

import com.example.a16adventure.domain.repository.MonumentRepository;
import com.example.a16adventure.domain.repository.SavedRepository;
import com.example.a16adventure.models.Monument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetSavedMonumentsUseCase {
    public interface Callback {
        void onSuccess(List<Monument> monuments);
        void onError(String message);
    }

    private final SavedRepository savedRepository;
    private final MonumentRepository monumentRepository;

    public GetSavedMonumentsUseCase(SavedRepository savedRepository, MonumentRepository monumentRepository) {
        this.savedRepository = savedRepository;
        this.monumentRepository = monumentRepository;
    }

    public void execute(String uid, Callback callback) {
        final Map<String, Monument> monumentById = new HashMap<>();

        try {
            List<Monument> monuments = monumentRepository.getAllMonuments();
            for (Monument monument : monuments) {
                monumentById.put(monument.getId(), monument);
            }
        } catch (Exception e) {
            callback.onError(e.getMessage() != null ? e.getMessage() : "Không thể tải danh sách địa danh");
            return;
        }

        savedRepository.observeSavedIds(uid, new SavedRepository.SavedIdsCallback() {
            @Override
            public void onSuccess(List<String> savedIds) {
                List<Monument> savedMonuments = new ArrayList<>();
                for (String id : savedIds) {
                    Monument monument = monumentById.get(id);
                    if (monument != null) {
                        monument.setSaved(true);
                        savedMonuments.add(monument);
                    }
                }
                callback.onSuccess(savedMonuments);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }
}
