package com.example.a16adventure.domain.usecase;

import com.example.a16adventure.models.Monument;

import java.util.ArrayList;
import java.util.List;

public class FilterMonumentsByDistrictUseCase {
    public List<Monument> execute(List<Monument> fullMonuments, String districtName) {
        if (fullMonuments == null) return new ArrayList<>();
        if (districtName == null || districtName.trim().isEmpty() || "Tất cả".equals(districtName)) {
            return new ArrayList<>(fullMonuments);
        }

        List<Monument> filtered = new ArrayList<>();
        for (Monument monument : fullMonuments) {
            if (districtName.equals(monument.getDistrict())) {
                filtered.add(monument);
            }
        }
        return filtered;
    }
}
