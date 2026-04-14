package com.example.a16adventure.presentation.saved;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.a16adventure.data.repository.AssetMonumentRepository;
import com.example.a16adventure.data.repository.FirebaseSavedRepository;
import com.example.a16adventure.domain.usecase.GetSavedMonumentsUseCase;
import com.example.a16adventure.domain.usecase.ToggleSavedMonumentUseCase;
import com.example.a16adventure.models.Monument;

import java.util.ArrayList;
import java.util.List;

public class SavedMonumentsViewModel extends AndroidViewModel {
    private final GetSavedMonumentsUseCase getSavedMonumentsUseCase;
    private final ToggleSavedMonumentUseCase toggleSavedMonumentUseCase;
    private final FirebaseSavedRepository savedRepository;

    private final MutableLiveData<List<Monument>> savedMonuments = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public SavedMonumentsViewModel(@NonNull Application application) {
        super(application);
        savedRepository = FirebaseSavedRepository.getInstance();
        getSavedMonumentsUseCase = new GetSavedMonumentsUseCase(
                savedRepository,
                new AssetMonumentRepository(application)
        );
        toggleSavedMonumentUseCase = new ToggleSavedMonumentUseCase(savedRepository);
    }

    public LiveData<List<Monument>> getSavedMonuments() {
        return savedMonuments;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void observeSavedMonuments(String uid) {
        getSavedMonumentsUseCase.execute(uid, new GetSavedMonumentsUseCase.Callback() {
            @Override
            public void onSuccess(List<Monument> monuments) {
                savedMonuments.postValue(monuments);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void removeSavedMonument(String uid, String monumentId) {
        toggleSavedMonumentUseCase.remove(uid, monumentId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        savedRepository.clearObserver();
    }
}
