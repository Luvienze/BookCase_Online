package com.duzceuni.denemeapplication.manager;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_EMAIL = "EmailPref";
    private static final String KEY_PASSWORD = "PasswordPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Save login user credentials
     * */
    public void saveLogin(String email, String password){
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

   /**
   * Check if user is logged in
   * */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Save user email
     * */
    public void saveUserEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    /**
     * Return user email from preferences
     * */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    /**
     * Clear all preferences
     * */
    public void clearSession() {
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_PASSWORD);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }

}
