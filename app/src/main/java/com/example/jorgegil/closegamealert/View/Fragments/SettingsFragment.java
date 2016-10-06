package com.example.jorgegil.closegamealert.View.Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.jorgegil.closegamealert.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
    }
}
