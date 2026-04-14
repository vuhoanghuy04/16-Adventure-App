package com.example.a16adventure.models;

import java.util.ArrayList;
import java.util.List;

public class MonumentDataManager {
    private static MonumentDataManager instance;
    private List<Monument> monumentList;

    private MonumentDataManager() {
        monumentList = new ArrayList<>();
    }

    public static synchronized MonumentDataManager getInstance() {
        if (instance == null) {
            instance = new MonumentDataManager();
        }
        return instance;
    }

    public List<Monument> getMonumentList() {
        return monumentList;
    }

    public void setMonumentList(List<Monument> list) {
        this.monumentList = list;
    }

    public List<Monument> getSavedMonuments() {
        List<Monument> saved = new ArrayList<>();
        if (monumentList != null) {
            for (Monument m : monumentList) {
                if (m.isSaved()) saved.add(m);
            }
        }
        return saved;
    }

    // ĐÂY LÀ HÀM SẼ FIX LỖI getMonumentById
    public Monument getMonumentById(String id) {
        if (monumentList == null || id == null) return null;
        for (Monument m : monumentList) {
            if (m.getId().equals(id)) {
                return m;
            }
        }
        return null;
    }
}