package com.duzceuni.denemeapplication.manager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FavoriteManager {
    private final DatabaseReference userFavoritesRef;
    public FavoriteManager(String userId) {
        userFavoritesRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("favoriteBooks");
    }

   /**
    * Add book to favorite via db
    * */
    public void addFavorite(long isbn) {
        userFavoritesRef.child(String.valueOf(isbn)).setValue(true);
    }

    /**
     * Remove book from favorites via db
     * */
    public void removeFavorite(long isbn) {
        userFavoritesRef.child(String.valueOf(isbn)).removeValue();
    }

    /**
     * Check if book is listed as favorite or not
     * */
    public void isFavorite(long isbn, ValueEventListener listener) {
        userFavoritesRef.child(String.valueOf(isbn)).addListenerForSingleValueEvent(listener);
    }

    /**
     * Get all the favorite books in list
     * */
    public void getAllFavorites(ValueEventListener listener) {
        userFavoritesRef.addListenerForSingleValueEvent(listener);
    }
}
