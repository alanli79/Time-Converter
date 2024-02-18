package com.example.timeconvert;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.timeconvert.databinding.FragmentFirstBinding;

import java.util.ArrayList;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String USER_HOME_TIME = "HomeTimeZone";
    private Spinner spinner;


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

        spinner = getView().findViewById(R.id.spinner_location);
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
        locations.add("America/New_York     GMT -05:00");
        locations.add("America/Los_Angeles  GMT -08:00");
        locations.add("Europe/Berlin        GMT +01:00");
        locations.add("Europe/Istanbul      GMT +02:00");
        locations.add("Asia/Singapore       GMT +08:00");
        locations.add("Asia/Tokyo           GMT +09:00");
        locations.add("Australia/Canberra   GMT +10:00");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, locations);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        spinner.setAdapter(adapter);

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

        binding.convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertTime();
            }
        });
    }

    private void convertTime() {
        //get current time
        String current = spinner.getSelectedItem().toString();
        //get home time
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        String home = preferences.getString(USER_HOME_TIME, "");
        //get time difference
        int current_offset = Integer.parseInt(current.substring(current.length()-6, current.length()-3));
        int home_offset = Integer.parseInt(home.substring(current.length()-6, current.length()-3));
        int offset = home_offset - current_offset;

        //TODO: get original time from time selector
        //get original time
        int hour = 12;
        int minute = 45;
        boolean pastMidnight = false; //false is AM, true is PM

        //calculate new time
        hour += offset;
        if (hour < 0) {
            pastMidnight = !pastMidnight;
            hour += 12;
        } else if (hour + offset > 12) {
            pastMidnight = !pastMidnight;
            hour -= 12;
        }

        //display converted time
        String isDay = (pastMidnight) ? "PM" : "AM";
        String new_time = hour + " : " + minute + " " + isDay;
        TextView textView = getView().findViewById(R.id.convertedTime);
        textView.setText(new_time);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}