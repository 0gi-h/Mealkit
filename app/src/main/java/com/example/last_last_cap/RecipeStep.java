package com.example.last_last_cap;

import java.io.Serializable;

public class RecipeStep implements Serializable {
    private String description;
    private String imageUrl;
    public RecipeStep(String description, String imageUrl) {
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // getter 메서드
    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

