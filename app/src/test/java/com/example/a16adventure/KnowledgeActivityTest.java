package com.example.a16adventure;

import com.example.a16adventure.activities.KnowledgeActivity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KnowledgeActivityTest {
    @Test
    public void removeAccents_shouldNormalizeVietnameseText() {
        String input = "Đồ Sơn Hải Phòng";
        String normalized = KnowledgeActivity.removeAccents(input);
        assertEquals("Do Son Hai Phong", normalized);
    }
}
