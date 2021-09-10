package com.example.dataManagers;

import com.google.firebase.firestore.FirebaseFirestore;

public abstract class DataManager {
    private static FirebaseFirestore db ;

    public DataManager() {

    }

    public static FirebaseFirestore getDb() {
        return FirebaseFirestore.getInstance();
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }
}
