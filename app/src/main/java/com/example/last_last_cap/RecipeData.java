package com.example.last_last_cap;

import java.io.Serializable;

public class RecipeData implements Serializable {
    private String title;
    private String imageUrl;
    private String detailUrl; // 레시피 상세 페이지 URL

    public RecipeData(String title, String imageUrl, String detailUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.detailUrl = detailUrl;
    }

    // getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

}

