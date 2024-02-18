package com.example.timeconvert;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
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

public class SecondFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentSecondBinding binding;
    private Spinner spinner_locations;

    private EditText userInputEditText;
    private Button saveButton;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_USER_TEXT = "HomeCity";


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinner_locations = getView().findViewById(R.id.spinner_location);
        spinner_locations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
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
        spinner_locations.setAdapter(adapter);

        saveButton = getView().findViewById(R.id.save_button);
        userInputEditText = getView().findViewById(R.id.home_city_input);
        loadUserText();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });


    }

    private void saveUserData() {
        // Get user input from the EditText
        String userInput = userInputEditText.getText().toString();

        // Save edit text input permanently
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_TEXT, userInput);
        editor.apply();

        Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner_location) {
            String value = parent.getItemAtPosition(position).toString();
            SharedPreferences.Editor editor = this.getContext().getSharedPreferences("MyPref", 0).edit();
            editor.putString("userInputKey", value);
        }
    }

    private void loadUserText() {
        // Load previously saved text from SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        String savedUserText = preferences.getString(KEY_USER_TEXT, "");

        if (savedUserText.isEmpty()) {
            userInputEditText.setText("Baltimore");
        } else {// Display the loaded text in the EditText
            userInputEditText.setText(savedUserText);
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}