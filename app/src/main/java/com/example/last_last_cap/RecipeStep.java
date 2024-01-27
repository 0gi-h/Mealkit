package com.example.last_last_cap;

import java.io.Serializable;

public class RecipeStep implements Serializable {
    private String description;
    private String imageUrl;
    private int cookTimeInSeconds; // 조리 시간을 초로 저장

    public RecipeStep(String description, String imageUrl, int cookTimeInSeconds) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.cookTimeInSeconds = cookTimeInSeconds;
    }

    // getter 메서드
    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getCookTimeInSeconds() {
        return cookTimeInSeconds;
    }
}