package com.example.last_last_cap;

public class AllergyData {
    private String name;
    public AllergyData() {
        // 기본 생성자가 필요합니다. (Firestore에서 데이터를 읽을 때 필요)
    }
    public AllergyData(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
