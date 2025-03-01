package com.kahdse.horizonnewsapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.kahdse.horizonnewsapp.R;
import com.kahdse.horizonnewsapp.activity.LoginActivity;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPref;
    private Switch switchNotifications;
    private Button btnLogout;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        switchNotifications = view.findViewById(R.id.switch_notifications);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Load saved notification preference
        boolean isNotificationsEnabled = sharedPref.getBoolean("NOTIFICATIONS", true);
        switchNotifications.setChecked(isNotificationsEnabled);

        // Toggle notifications
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.edit().putBoolean("NOTIFICATIONS", isChecked).apply();
        });

        // Logout functionality
        btnLogout.setOnClickListener(v -> {
            sharedPref.edit().remove("TOKEN").apply(); // Remove login token
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish(); // Close current activity
        });

        return view;
    }
}
