package com.absket.in;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Sreejith on 12-11-2015.
 */
public class UserSessionManager {

    private SharedPreferences prefs;




    public UserSessionManager(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setUserName(String userEmail) {
        prefs.edit().putString("name", userEmail).commit();

    }

    public String getUserName() {
        String userEmail = prefs.getString("name","");
        return userEmail;
    }

    public String getIsLoggedIn() {
        String UserFees = prefs.getString("log","");
        return UserFees;
    }

    public void setIsLoggedIn(String userFees) {
        prefs.edit().putString("log", userFees).commit();
        UserFees = userFees;
    }

    public void setUserId(String userEmail) {
        prefs.edit().putString("id", userEmail).commit();

    }

    public String getUserId() {
        String userEmail = prefs.getString("id","");
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        prefs.edit().putString("email", userEmail).commit();

    }

    public String getUserEmail() {
        String userEmail = prefs.getString("email","");
        return userEmail;
    }


    public void setUserMobile(String userEmail) {
        prefs.edit().putString("mobile", userEmail).commit();

    }

    public String getUserMobile() {
        String userEmail = prefs.getString("mobile","");
        return userEmail;
    }


    public void setUserFCM(String userEmail) {
        prefs.edit().putString("fcm", userEmail).commit();

    }

    public String getUserFCM() {
        String userEmail = prefs.getString("fcm","");
        return userEmail;
    }


    public void setUserAddress(String userEmail) {
        prefs.edit().putString("address", userEmail).commit();

    }

    public String getUserAddress() {
        String userEmail = prefs.getString("address","");
        return userEmail;
    }

    public String getOTP() {
        String UserFees = prefs.getString("otp","");
        return UserFees;
    }

    public void setOTP(String userFees) {
        prefs.edit().putString("otp", userFees).commit();
    }


    public String getUserLat() {
        String UserFees = prefs.getString("lat","");
        return UserFees;
    }

    public void setUserLat(String userFees) {
        prefs.edit().putString("lat", userFees).commit();
    }


    public String getUserLon() {
        String UserFees = prefs.getString("lon","");
        return UserFees;
    }

    public void setUserLon(String userFees) {
        prefs.edit().putString("lon", userFees).commit();
    }


    String UserFees;



    public void setCustomerId(String userFees) {
        prefs.edit().putString("cid", userFees).commit();
    }


    public String getCustomerId() {
        String UserFees = prefs.getString("cid","");
        return UserFees;
    }


    public void setRefCode(String userFees) {
        prefs.edit().putString("ref", userFees).commit();
    }


    public String getRefCode() {
        String UserFees = prefs.getString("ref","");
        return UserFees;
    }


    public void setRefCodeSignUp(String userFees) {
        prefs.edit().putString("refusing", userFees).commit();
    }


    public String getRefCodeSignUp() {
        String UserFees = prefs.getString("refusing","");
        return UserFees;
    }



    public void setLastPurchasePrice(String userFees) {
        prefs.edit().putString("last", userFees).commit();
    }


    public String getLastPurchasePrice() {
        String UserFees = prefs.getString("last","");
        return UserFees;
    }


}