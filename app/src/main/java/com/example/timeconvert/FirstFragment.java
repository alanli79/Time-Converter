package com.example.timeconvert;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.Calendar;
import java.util.HashMap;
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
    private String selectedTimeZone;
    private TextView convertedTime;
    private String homeTime ;
    private boolean liveButton;
    private TextView currGMTDiff;
    private TextView homGMTDiff;
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
        initializeTimeZoneOffsets();
        currentTimeButton = view.findViewById(R.id.currentTimeButton);
        convertedTime = view.findViewById(R.id.convertedTime);
        currGMTDiff = getView().findViewById(R.id.currentDiff);
        homGMTDiff = getView().findViewById(R.id.homeDiff);
        spinner = getView().findViewById(R.id.spinner_location);
        View timeInputView = getLayoutInflater().inflate(R.layout.manual_time_input, null);
        PopupWindow timeInputWindow = new PopupWindow(
                timeInputView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        // Set background drawable to allow dismissal when clicking outside
        timeInputWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

// Set the animation style
        timeInputWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        startUpdatingButton();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), "Selected Item: " + item, Toast.LENGTH_SHORT).show();
                convertTime1();
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

        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        homeTime = preferences.getString(USER_HOME_TIME, "");

        // Display the generated text in a TextView or any other UI element
        TextView homeView = getView().findViewById(R.id.homeTimeZone);
        if (homeTime.isEmpty()) {
            homeTime = "America/New_York";
        }
        homeView.setText(homeTime);

        currentTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Action to perform when the button is clicked
                Toast.makeText(getContext(), "Pressed " , Toast.LENGTH_SHORT).show(); // Print something
                stopUpdatingButton();
                timeInputWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            }
        });

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
                convertTime1();
            }
        });
        convertTime1();
    }

    private void startUpdatingButton() {
        handler = new Handler();
        liveButton = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                convertTime1();
                // Schedule the next update after the defined interval
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    private void stopUpdatingButton() {
        liveButton = false;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    private void convertTime1() {
        int conversionDifference;
        if(liveButton) {
            // Get the selected time zone from the spinner
            selectedTimeZone = spinner.getSelectedItem().toString();
        } else {
            selectedTimeZone = spinner.getSelectedItem().toString();
        }
        if(timeZoneOffsets.get(selectedTimeZone) > timeZoneOffsets.get(homeTime)) {
            conversionDifference = (timeZoneOffsets.get(homeTime) - timeZoneOffsets.get(selectedTimeZone));
        } else {
            conversionDifference = Math.abs((timeZoneOffsets.get(selectedTimeZone) - timeZoneOffsets.get(homeTime)));
        }
        // Create a calendar instance for the selected time zone
        Calendar currCalendar = Calendar.getInstance(TimeZone.getTimeZone(selectedTimeZone));
        Calendar homCalendar = Calendar.getInstance(TimeZone.getTimeZone(homeTime));
        // Get the current time in the selected time zone
        int currHour = currCalendar.get(Calendar.HOUR_OF_DAY);
        int currMinute = currCalendar.get(Calendar.MINUTE);
        int homHour = (currHour + conversionDifference);
        ImageView sleepAlert = getView().findViewById(R.id.sleepAlert);
        // Check if the current hour falls between 7 and 23
        if (homHour >= 7 && homHour <= 23) {
           sleepAlert.setVisibility(View.GONE);
        } else {
           sleepAlert.setVisibility(View.VISIBLE);
        }
        String currAmOrPm = (currCalendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";
        String homAmOrPm = (homCalendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";

        //convert to 12 hour time
        homHour %= 12;
        currHour %= 12;
        if(homHour == 0){
            homHour = 12;
        }
        if(currHour == 0){
            currHour = 12;
        }

        // Format the time string
        String currTime = String.format("%02d : %02d %s", currHour, currMinute, currAmOrPm);
        String homTime = String.format("%02d : %02d %s", homHour, currMinute, homAmOrPm);

        // Update the currentTimeTextView
        currentTimeButton.setText(currTime);
        convertedTime.setText(homTime);

        //current GMT
        int currGMT = timeZoneOffsets.get(selectedTimeZone);
        String currSign = (currGMT >= 0) ? "+" : "-"; // Determine sign
        String GMTDiffSet = String.format("%s%02d:00", currSign, Math.abs(currGMT)); // Use sign
        currGMTDiff.setText("GMT " + GMTDiffSet);

        // home GMT
        int homGMT = timeZoneOffsets.get(homeTime);
        String homSign = (homGMT >= 0) ? "+" : "-";
        String homGMTDiffSet = String.format("%s%02d:00", homSign, Math.abs(homGMT));
        homGMTDiff.setText("GMT " + homGMTDiffSet);
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

    private String textViewToString(TextView textView) {
        // Get the text from the TextView
        CharSequence text = textView.getText();

        // Convert the text to a String
        String textString = text.toString();

        return textString;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopUpdatingButton();
        binding = null;
    }

}