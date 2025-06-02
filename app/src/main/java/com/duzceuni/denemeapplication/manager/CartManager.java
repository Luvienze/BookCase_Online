package com.duzceuni.denemeapplication.manager;

import android.util.Log;

import com.duzceuni.denemeapplication.adapter.CartAdapter;
import com.duzceuni.denemeapplication.entity.Books;

import java.util.ArrayList;
import java.util.List;
public class   CartManager {
    private static CartManager instance;
    private List<Books> cartItems = new ArrayList<>();
    private CartAdapter.OnCartUpdatedListener listener;
    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    /**
     * Add book to cart in a list
     * */
    public boolean addToCart(Books book) {
        if (isInCart(book)) {
            Log.d("CartManager", "Book is already in the cart");
            return false;
        }
        if (isFull()) {
            Log.d("CartManager", "Cart is full");
            return false;
        }
        // Add to cart logic
        cartItems.add(book);
        return true;
    }

   /**
    *  Set listener to cart list
    * */
    public void setOnCartUpdatedListener(CartAdapter.OnCartUpdatedListener listener) {
        this.listener = listener;
    }

   /**
    *  Remove a book from cart
    * */
    public void removeFromCart(Books book) {
        cartItems.remove(book);
        if (listener != null) {
            listener.onCartUpdated();
        }
    }

   /**
    * Get books in cart as list
    * */
    public List<Books> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    /**
     * Check if cart size greater than 3
     * */
    public boolean isFull() {
        return cartItems.size() >= 3;
    }

    /**
     * Clear cart list
     * */
    public void clearCart() {
        cartItems.clear();
    }

    /**
     * Check if the same book is in the cart
     * */
    public boolean isInCart(Books book) {
        return cartItems.contains(book);
    }
}
