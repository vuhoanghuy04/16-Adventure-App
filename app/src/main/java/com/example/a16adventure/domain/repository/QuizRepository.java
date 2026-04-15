package com.example.a16adventure.domain.repository;

import org.json.JSONObject;

import java.util.List;

public interface QuizRepository {
    List<JSONObject> getAllQuizzes() throws Exception;
}
