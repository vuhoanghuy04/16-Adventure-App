package com.example.a16adventure.presentation.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.a16adventure.data.repository.FirebaseAuthRepository;
import com.example.a16adventure.domain.model.UserSession;
import com.example.a16adventure.domain.repository.AuthRepository;
import com.example.a16adventure.domain.usecase.GetCurrentUserUseCase;
import com.example.a16adventure.domain.usecase.RegisterUseCase;
import com.example.a16adventure.domain.usecase.SignInUseCase;
import com.example.a16adventure.domain.usecase.SignOutUseCase;

public class AuthViewModel extends ViewModel {
    private final SignInUseCase signInUseCase;
    private final RegisterUseCase registerUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final SignOutUseCase signOutUseCase;

    private final MutableLiveData<UserSession> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public AuthViewModel() {
        this(new FirebaseAuthRepository());
    }

    public AuthViewModel(AuthRepository authRepository) {
        signInUseCase = new SignInUseCase(authRepository);
        registerUseCase = new RegisterUseCase(authRepository);
        getCurrentUserUseCase = new GetCurrentUserUseCase(authRepository);
        signOutUseCase = new SignOutUseCase(authRepository);
    }

    public LiveData<UserSession> getCurrentUserLiveData() {
        return currentUser;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void loadCurrentUser() {
        currentUser.setValue(getCurrentUserUseCase.execute());
    }

    public String getCurrentUserId() {
        return getCurrentUserUseCase.executeUid();
    }

    public void signIn(String email, String password) {
        signInUseCase.execute(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(UserSession session) {
                currentUser.postValue(session);
                successMessage.postValue("Chào mừng bạn quay trở lại!");
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void register(String name, String email, String password) {
        registerUseCase.execute(name, email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(UserSession session) {
                currentUser.postValue(session);
                successMessage.postValue("Đăng ký thành công!");
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void signOut() {
        signOutUseCase.execute();
        currentUser.setValue(null);
        successMessage.setValue("Đã đăng xuất thành công!");
    }
}
