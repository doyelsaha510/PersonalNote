package com.example.personalnote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;

import com.google.android.material.textview.MaterialTextView;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat nightmode;
    MaterialTextView textViewnight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Settings");

        nightmode = findViewById(R.id.nightmode);
        textViewnight = findViewById(R.id.textnight);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        // When user reopens the app
        // after applying dark/light mode
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            textViewnight.setText("Disable Dark Mode");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            textViewnight.setText("Enable Dark Mode");
        }

        nightmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // if dark mode is on it
                    // will turn it off
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    // it will set isDarkModeOn
                    // boolean to false
                    editor.putBoolean("isDarkModeOn", false);
                    editor.apply();

                    // change text of Button
                    textViewnight.setText("Enable Dark Mode");
                } else {

                    // if dark mode is off
                    // it will turn it on
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    // it will set isDarkModeOn
                    // boolean to true
                    editor.putBoolean("isDarkModeOn", true);
                    editor.apply();

                    // change text of Button
                    textViewnight.setText("Disable Dark Mode");
                }
            }
        });
    }
}
