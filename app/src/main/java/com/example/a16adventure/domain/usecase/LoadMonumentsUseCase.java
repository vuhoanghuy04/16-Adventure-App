package com.example.a16adventure.domain.usecase;

import com.example.a16adventure.domain.repository.MonumentRepository;
import com.example.a16adventure.models.Monument;

import java.util.List;

public class LoadMonumentsUseCase {
    private final MonumentRepository monumentRepository;

    public LoadMonumentsUseCase(MonumentRepository monumentRepository) {
        this.monumentRepository = monumentRepository;
    }

    public List<Monument> execute() throws Exception {
        return monumentRepository.getAllMonuments();
    }
}
