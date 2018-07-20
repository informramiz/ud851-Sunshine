package com.example.android.sunshine;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.general_settings);

        int preferenceCount = getPreferenceScreen().getPreferenceCount();
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            String value = sharedPreferences.getString(preference.getKey(), "");
            setSummary(preference, value);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = getPreferenceScreen().findPreference(key);
        if (preference != null) {
            String value = sharedPreferences.getString(preference.getKey(), "");
            setSummary(preference, value);
        }
    }

    private void setSummary(@Nullable Preference preference, String value) {
        if (preference == null) {
            return;
        }

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference)preference;
            int valueIndex = listPreference.findIndexOfValue(value);
            if (valueIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[valueIndex]);
            }
        } else if (preference instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference)preference;
            editTextPreference.setSummary(value);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
}
