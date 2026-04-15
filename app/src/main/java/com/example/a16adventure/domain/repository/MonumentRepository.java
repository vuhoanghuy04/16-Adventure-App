package com.example.a16adventure.domain.repository;

import com.example.a16adventure.models.Monument;

import java.util.List;

public interface MonumentRepository {
    List<Monument> getAllMonuments() throws Exception;
}
