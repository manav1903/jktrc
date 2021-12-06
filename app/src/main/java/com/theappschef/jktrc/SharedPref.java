package com.theappschef.jktrc;


import android.content.Context;
import android.content.SharedPreferences;


public class SharedPref {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setting_note", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getChoice() {
        return sharedPreferences.getString("choice", "");
    }

    public void setChoice(String choice) {
        editor.putString("choice", choice);
        editor.apply();
    }

    public String getWeek() {
        return sharedPreferences.getString("week", "6 8");
    }

    public void setWeek(String choice) {

        editor.putString("week", choice);
        editor.apply();
    }

    public void setDay(String choice) {
        editor.putString("day", choice);
        editor.apply();
    }

    public String getDay() {
        return sharedPreferences.getString("day", "6");
    }

    public void setBackup(String yes) {
        editor.putString("back", yes);
        editor.apply();
    }

    public String getBackup() {
        return sharedPreferences.getString("back", "yes");
    }
    public void setTheme(String yes) {
        editor.putString("theme", yes);
        editor.apply();
    }

    public String getTheme() {
        return sharedPreferences.getString("theme", "light");
    }
}
