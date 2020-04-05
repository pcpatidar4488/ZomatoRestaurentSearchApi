package com.example.restorent.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    private static Preferences instance;

    private static final String BASE_URL = "base_url";
    private static final String login = "login";
    private static final String token = "login";
    private static final String lat = "lat";
    private static final String lon = "lon";
    private static final String address = "address";



    private Preferences(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences("My_pref", 0);
        editor = pref.edit();
    }


    public static Preferences getInstance(Context context) {
        if (instance == null) {
            instance = new Preferences(context);
        }
        return instance;
    }

    public void setLogin(Boolean cname) {
        editor.putBoolean(login, cname);
        editor.commit();
    }

    public boolean isLogin() {
        return pref.getBoolean(login, false);
    }

    public void setToken(String token1) {
        editor.putString(token, token1);
        editor.commit();
    }

    public String getToken() {
        return pref.getString(token, "");
    }

    public void setLat(String lat1) {
        editor.putString(lat, lat1);
        editor.commit();
    }

    public String getLat() {
        return pref.getString(lat, "");
    }

    public void setLon(String lon1) {
        editor.putString(lon, lon1);
        editor.commit();
    }

    public String getLon() {
        return pref.getString(lon, "");
    }

    public void setAddress(String address1) {
        editor.putString(address, address1);
        editor.commit();
    }

    public String getAddress() {
        return pref.getString(address, "");
    }
}