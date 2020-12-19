package com.absket.in.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsPreferences {
    private static final int PREFERENCES_VERSION = 1;
    public static final String PREFERENCES_NAME = "settings_prefs+" + PREFERENCES_VERSION;
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String USER_ID = "user_id";
    public static final String LOGIN_TYPE = "login_type";
    private Context _context = null;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;




    public SettingsPreferences( Context context) {
        this._context = context;
        preferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
    }

    public String getFirstName(){
        return preferences.getString(FIRST_NAME, "");
    }
    public String getLastName(){
        return preferences.getString(LAST_NAME, "");
    }
    public String getEmail(){
        return preferences.getString(EMAIL, "");
    }

    public void createLoginSession(String firstName,
                                   String lastName,
                                   String email,
                                   String userid,
                                   int loginType){
        editor = preferences.edit();
        editor.putString(FIRST_NAME, firstName);
        editor.putString(LAST_NAME, lastName);
        editor.putString(EMAIL, email);
        editor.putString(USER_ID, userid);
        editor.putInt(LOGIN_TYPE, loginType);
        editor.apply();
    }
}
