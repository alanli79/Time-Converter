package com.example.timeconvert;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.timeconvert.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String USER_HOME_CITY = "HomeCity";
    private static final String USER_HOME_TIME = "HomeTimeZone";


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        String home_time = preferences.getString(USER_HOME_TIME, "");

        // Display the generated text in a TextView or any other UI element
        TextView textView = getView().findViewById(R.id.homeTimeZone);
        if (home_time.isEmpty()) {
            home_time = "America/New_York     GMT -05:00";
        }
        textView.setText(home_time);

        binding.settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}