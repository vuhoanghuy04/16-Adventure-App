package com.example.a16adventure.presentation.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.a16adventure.data.repository.AssetMonumentRepository;
import com.example.a16adventure.data.repository.FirebaseAuthRepository;
import com.example.a16adventure.data.repository.FirebaseSavedRepository;
import com.example.a16adventure.data.repository.OpenMeteoWeatherRepository;
import com.example.a16adventure.domain.model.WeatherInfo;
import com.example.a16adventure.domain.repository.WeatherRepository;
import com.example.a16adventure.domain.usecase.FetchCurrentWeatherUseCase;
import com.example.a16adventure.domain.usecase.FilterMonumentsByDistrictUseCase;
import com.example.a16adventure.domain.usecase.GetCurrentUserUseCase;
import com.example.a16adventure.domain.usecase.LoadMonumentsUseCase;
import com.example.a16adventure.domain.usecase.ToggleSavedMonumentUseCase;
import com.example.a16adventure.models.Monument;
import com.example.a16adventure.models.MonumentDataManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    public interface SaveActionCallback {
        void onSuccess();
        void onError(String message);
    }

    private final LoadMonumentsUseCase loadMonumentsUseCase;
    private final FilterMonumentsByDistrictUseCase filterMonumentsByDistrictUseCase;
    private final ToggleSavedMonumentUseCase toggleSavedMonumentUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final FetchCurrentWeatherUseCase fetchCurrentWeatherUseCase;

    private final MutableLiveData<List<Monument>> fullMonuments = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Monument>> displayMonuments = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> districts = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<WeatherInfo> weatherInfo = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        loadMonumentsUseCase = new LoadMonumentsUseCase(new AssetMonumentRepository(application));
        filterMonumentsByDistrictUseCase = new FilterMonumentsByDistrictUseCase();
        toggleSavedMonumentUseCase = new ToggleSavedMonumentUseCase(new FirebaseSavedRepository());
        getCurrentUserUseCase = new GetCurrentUserUseCase(new FirebaseAuthRepository());
        fetchCurrentWeatherUseCase = new FetchCurrentWeatherUseCase(new OpenMeteoWeatherRepository());
    }

    public LiveData<List<Monument>> getFullMonuments() {
        return fullMonuments;
    }

    public LiveData<List<Monument>> getDisplayMonuments() {
        return displayMonuments;
    }

    public LiveData<List<String>> getDistricts() {
        return districts;
    }

    public LiveData<WeatherInfo> getWeatherInfo() {
        return weatherInfo;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadMonuments() {
        try {
            List<Monument> monuments = loadMonumentsUseCase.execute();
            Collections.shuffle(monuments);
            fullMonuments.setValue(monuments);
            displayMonuments.setValue(new ArrayList<>(monuments));

            MonumentDataManager.getInstance().setMonumentList(monuments);

            LinkedHashSet<String> uniqueDistricts = new LinkedHashSet<>();
            for (Monument monument : monuments) {
                uniqueDistricts.add(monument.getDistrict());
            }
            districts.setValue(new ArrayList<>(uniqueDistricts));
        } catch (Exception e) {
            errorMessage.setValue(e.getMessage() != null ? e.getMessage() : "Không thể tải dữ liệu địa danh");
        }
    }

    public void filterMonuments(String districtName) {
        List<Monument> full = fullMonuments.getValue();
        displayMonuments.setValue(filterMonumentsByDistrictUseCase.execute(full, districtName));
    }

    public String getCurrentUserId() {
        return getCurrentUserUseCase.executeUid();
    }

    public void toggleSavedMonument(String uid, Monument monument, boolean newSavedState, SaveActionCallback callback) {
        if (uid == null || uid.trim().isEmpty()) {
            callback.onError("Bạn cần đăng nhập để lưu địa danh!");
            return;
        }

        monument.setSaved(newSavedState);
        toggleSavedMonumentUseCase.execute(uid, monument.getId(), newSavedState);
        callback.onSuccess();
    }

    public void fetchCurrentWeather() {
        fetchCurrentWeatherUseCase.execute(new WeatherRepository.WeatherCallback() {
            @Override
            public void onSuccess(WeatherInfo info) {
                weatherInfo.postValue(info);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }
}
