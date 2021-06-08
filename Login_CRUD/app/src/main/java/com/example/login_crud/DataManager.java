package com.example.login_crud;

import com.google.firebase.firestore.FirebaseFirestore;

public abstract class DataManager {
    private static FirebaseFirestore db;

    public DataManager() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static FirebaseFirestore getDb() {
        return db;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }
}
