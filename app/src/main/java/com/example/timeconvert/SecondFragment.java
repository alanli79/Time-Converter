package com.example.timeconvert;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.timeconvert.databinding.FragmentSecondBinding;

import java.util.ArrayList;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private EditText userInputEditText;
    private Spinner spinner;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String USER_HOME_CITY = "HomeCity";
    private static final String USER_HOME_TIME = "HomeTimeZone";


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinner = view.findViewById(R.id.spinner_location);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), "Selected Item: "+item, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayList<String> locations = new ArrayList<>();
        locations.add("America/New_York");
        locations.add("America/Los_Angeles");
        locations.add("Europe/Berlin");
        locations.add("Europe/Istanbul");
        locations.add("Asia/Singapore");
        locations.add("Asia/Tokyo");
        locations.add("Australia/Canberra");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, locations);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        spinner.setAdapter(adapter);

        Button saveButton = view.findViewById(R.id.save_button);
        // Edit text for home city
        userInputEditText = view.findViewById(R.id.home_city_input);

        loadUserText();

        saveButton.setOnClickListener(v -> {
            saveUserData();
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        });


    }

    private void loadUserText() {
        // Load previously saved text from SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        String home_city = preferences.getString(USER_HOME_CITY, "");
        if (home_city.isEmpty()) {
            home_city = "Baltimore";
        }
        userInputEditText.setText(home_city);
        String home_time = preferences.getString(USER_HOME_TIME, "");
        if (home_time.isEmpty()) {
            home_time = "America/Los_Angeles";
        }
        switch (home_time) {
            case "America/New_York":
                spinner.setSelection(0);
                break;
            case "America/Los_Angeles":
                spinner.setSelection(1);
                break;
            case "Europe/Berlin":
                spinner.setSelection(2);
                break;
            case "Europe/Istanbul":
                spinner.setSelection(3);
                break;
            case "Asia/Singapore":
                spinner.setSelection(4);
                break;
            case "Asia/Tokyo":
                spinner.setSelection(5);
                break;
            case "Australia/Canberra":
                spinner.setSelection(6);
                break;
        }
    }

    private void saveUserData() {
        // Get user input from the EditText
        String userInput = userInputEditText.getText().toString();
        String userTimeZone = spinner.getSelectedItem().toString();

        // Save edit text input
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_HOME_CITY, userInput);
        editor.apply();
        // Save spinner input
        editor.putString(USER_HOME_TIME, userTimeZone);
        editor.apply();

        Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}