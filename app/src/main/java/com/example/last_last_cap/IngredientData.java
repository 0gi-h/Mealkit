package com.example.last_last_cap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class IngredientData extends Activity {

private String name;
private String date;

private String id;

    public IngredientData(String documentId,String name, String date) {

        this.id = documentId;

        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
    public String getid(){
        return id;
    }
}
