package com.example.timeconvert;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import androidx.navigation.fragment.NavHostFragment;
import com.example.timeconvert.databinding.FragmentFirstBinding;
import java.util.ArrayList;
import android.os.Handler;

public class FirstFragment extends Fragment {

    private Handler handler;
    private Map<String, Integer> timeZoneOffsets;
    private final int UPDATE_INTERVAL = 1;
    private Button currentTimeButton;
    private FragmentFirstBinding binding;
    private TextView convertedTime;
    private TextView currGMTDiff;
    private TextView homGMTDiff;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String USER_HOME_TIME = "HomeTimeZone";
    private Spinner spinner;
    private int hour, minute;
    private boolean custom_mode = false; //false means computer time, true means inputted time


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initializing
        initializeTimeZoneOffsets();
        currentTimeButton = view.findViewById(R.id.currentTimeButton);
        convertedTime = view.findViewById(R.id.convertedTime);
        currGMTDiff = view.findViewById(R.id.currentDiff);
        homGMTDiff = view.findViewById(R.id.homeDiff);
        spinner = view.findViewById(R.id.spinner_location);

        // Set interval for clock update
        startUpdatingButton();

        // Set view for home timezone
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        String homeTime = preferences.getString(USER_HOME_TIME, "");
        // Display the generated text in a TextView or any other UI element
        TextView homeView = view.findViewById(R.id.homeTimeZone);
        if (homeTime.isEmpty()) {
            spinner.setSelection(1);
            homeTime = "America/Los_Angeles";
        }
        homeView.setText(homeTime);

        String finalHomeTime = homeTime;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (item.equals(finalHomeTime)) {
                    Toast.makeText(getContext(), "Identical timezones!", Toast.LENGTH_SHORT).show();
                }
                convertTime();
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
        //initialize spinner to preselect America/New_York
        spinner.setSelection(0);

        // Implement the button
        currentTimeButton.setOnClickListener(this::popTimePicker);
        binding.settingsButton.setOnClickListener(view1 -> NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));

        binding.convertButton.setOnClickListener(v -> convertTime());
        convertTime();
    }

    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (tp, sel_hour, sel_minute) -> {
            hour = sel_hour;
            minute = sel_minute;
            int display_hour = (hour >= 12) ? hour - 12 : hour;
            String AM_PM = (hour >= 12) ? "PM" : "AM";
            currentTimeButton.setText(String.format(Locale.getDefault(), String.format(Locale.getDefault(), "%02d : %02d %s", display_hour, minute, AM_PM)));
            custom_mode = true;
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), onTimeSetListener, hour, minute, false);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }


    private void startUpdatingButton() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!custom_mode) {
                    convertTime();
                    // Schedule the next update after the defined interval
                    handler.postDelayed(this, UPDATE_INTERVAL);
                }
            }
        }, UPDATE_INTERVAL);
    }

    private void stopUpdatingButton() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    private void convertTime() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        String homeTime = preferences.getString(USER_HOME_TIME, "");
        //default is LA
        if (homeTime.isEmpty()) {
            homeTime = "America/Los_Angeles";
        }

        // Get timezone data
        String selectedTimeZone = spinner.getSelectedItem().toString();
        Integer sel_offset = timeZoneOffsets.get(selectedTimeZone);
        Integer home_offset = timeZoneOffsets.get(homeTime);

        int conversionDifference = home_offset - sel_offset;

        // Create a calendar instance for the selected time zone
        Calendar currCalendar = Calendar.getInstance(TimeZone.getTimeZone(selectedTimeZone));
        Calendar homCalendar = Calendar.getInstance(TimeZone.getTimeZone(homeTime));
        // Get the current time in the selected time zone
        int currHour = (custom_mode) ? hour : currCalendar.get(Calendar.HOUR_OF_DAY);
        int currMinute = (custom_mode) ? minute : currCalendar.get(Calendar.MINUTE);
        int homHour = currHour + conversionDifference;
        if (homHour < 0) {
            homHour += 24;
        } else if (homHour >= 24) {
            homHour -= 24;
        }

        //LA 23

        ImageView sleepAlert = getView().findViewById(R.id.sleepAlert);
        // Check if the current hour falls between 7 and 23
        if (homHour >= 7 && homHour <= 23) {
           sleepAlert.setVisibility(View.GONE);
        } else {
           sleepAlert.setVisibility(View.VISIBLE);
        }

        String currAmOrPm = (custom_mode) ? ((hour >= 12) ? "PM" : "AM") : (currCalendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";
        String homAmOrPm = (custom_mode) ? ((homHour >= 12 && homHour <= 23) ? "PM" : "AM") : (homCalendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";

        //convert to 12 hour time
        homHour = (homHour % 12 == 0) ? 12 : homHour % 12;
        currHour = (currHour % 12 == 0) ? 12 : currHour % 12;

        // Format the time string
        String currTime = String.format(Locale.getDefault(), "%02d : %02d %s", currHour, currMinute, currAmOrPm);
        String homTime = String.format(Locale.getDefault(), "%02d : %02d %s", homHour, currMinute, homAmOrPm);

        // Update the currentTimeTextView
        currentTimeButton.setText(currTime);
        convertedTime.setText(homTime);

        //current GMT
        int currGMT = sel_offset;
        String currSign = (currGMT >= 0) ? "+" : "-"; // Determine sign
        String GMTDiffSet = String.format(Locale.getDefault(), "GMT %s%02d:00", currSign, Math.abs(currGMT)); // Use sign
        currGMTDiff.setText(GMTDiffSet);

        // home GMT
        int homGMT = home_offset;
        String homSign = (homGMT >= 0) ? "+" : "-";
        String homGMTDiffSet = String.format(Locale.getDefault(),"GMT %s%02d:00", homSign, Math.abs(homGMT));
        homGMTDiff.setText(homGMTDiffSet);
    }

    private void initializeTimeZoneOffsets() {
        timeZoneOffsets = new HashMap<>();
        // Add time zone offsets to the dictionary
        timeZoneOffsets.put("America/New_York", -5);
        timeZoneOffsets.put("America/Los_Angeles", -8);
        timeZoneOffsets.put("Europe/Berlin", 1);
        timeZoneOffsets.put("Europe/Istanbul", 3);
        timeZoneOffsets.put("Asia/Singapore", 8);
        timeZoneOffsets.put("Asia/Tokyo", 9);
        timeZoneOffsets.put("Australia/Canberra", 11);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopUpdatingButton();
        binding = null;
    }

}