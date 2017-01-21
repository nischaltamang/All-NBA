package com.gmail.jorgegilcavazos.ballislife.features.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.util.ArraySet;
import android.util.Log;

import com.gmail.jorgegilcavazos.ballislife.features.login.LoginActivity;
import com.gmail.jorgegilcavazos.ballislife.network.RedditAuthentication;
import com.gmail.jorgegilcavazos.ballislife.util.Constants;
import com.gmail.jorgegilcavazos.ballislife.util.TeamName;
import com.gmail.jorgegilcavazos.ballislife.R;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Set;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String TAG = "SettingsFragment";

    // Should match string value in strings.xml
    public static final String KEY_PREF_CGA_TOPICS = "pref_cga_topics";

    private final RedditAuthentication.DeAuthTask.OnDeAuthTaskCompleted deAuthListener =
            new RedditAuthentication.DeAuthTask.OnDeAuthTaskCompleted() {
        @Override
        public void onSuccess() {
            RedditAuthentication.getInstance().clearRefreshTokenInPrefs(getActivity());
            initLogInStatusText();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            pickPreferenceObject(getPreferenceScreen().getPreference(i));
        }

        initListeners();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> set = sharedPreferences.getStringSet(KEY_PREF_CGA_TOPICS, new ArraySet<String>());
        Log.d(TAG, "set: " + set.size());
    }

    private void pickPreferenceObject(Preference preference) {
        if (preference instanceof PreferenceCategory) {
            PreferenceCategory category = (PreferenceCategory) preference;
            for (int i = 0; i < category.getPreferenceCount(); i++) {
                pickPreferenceObject(category.getPreference(i));
            }
        } else {
            initSummary(preference);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        switch (key) {
            case "teams_list":
                String abbrev = sharedPreferences.getString(key, null);
                preference.setSummary(getTeamName(abbrev));
                break;
            case "log_out_pref":
                preference.setTitle("Log in");
                break;
            case KEY_PREF_CGA_TOPICS:
                Set<String> newTopics = sharedPreferences.getStringSet(key, null);
                String[] availableTopics = getResources().getStringArray(R.array.pref_cga_values);

                if (newTopics != null) {
                    for (String availableTopic : availableTopics) {
                        if (newTopics.contains(availableTopic)) {
                            FirebaseMessaging.getInstance().subscribeToTopic(availableTopic);
                        } else {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(availableTopic);
                        }
                    }
                }
        }
    }

    private String getTeamName(String abbreviation) {
        if (abbreviation != null) {
            if (abbreviation.equals("noteam")) {
                return "No team selected";
            }

            abbreviation = abbreviation.toUpperCase();

            if (Constants.NBA_MATERIAL_ENABLED) {
                for (TeamName teamName : TeamName.values()) {
                    if (teamName.toString().equals(abbreviation)) {
                        return teamName.getTeamName();
                    }
                }
            } else {
                return abbreviation;
            }
        }
        return "No team selected";
    }

    private void initSummary(Preference preference) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            preference.setSummary(getTeamName(listPreference.getValue()));
        }
    }

    private void initLogInStatusText() {
        Preference logInStatusPref = findPreference("log_in_status_pref");
        RedditAuthentication reddit = RedditAuthentication.getInstance();
        if (reddit.isUserLoggedIn()) {
            logInStatusPref.setTitle(R.string.log_out);
            logInStatusPref.setSummary(String.format(getString(R.string.logged_as_user),
                    reddit.getRedditClient().getAuthenticatedUser()));

        } else {
            logInStatusPref.setTitle(R.string.log_in);
            logInStatusPref.setSummary(R.string.click_login);
        }
    }

    private void initListeners() {
        Preference logInStatusPref = findPreference("log_in_status_pref");
        logInStatusPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                RedditAuthentication reddit = RedditAuthentication.getInstance();
                if (reddit.isUserLoggedIn()) {
                    reddit.deAuthenticateUser(getActivity(), deAuthListener);
                } else {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginIntent);
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        initLogInStatusText();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
