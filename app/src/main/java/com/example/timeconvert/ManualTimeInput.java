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
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.timeconvert.databinding.FragmentSecondBinding;

public class ManualTimeInput extends Fragment{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manual_time_input, container, false);

        NumberPicker hourPicker = view.findViewById(R.id.hourPicker);
        NumberPicker minutePicker = view.findViewById(R.id.minutePicker);
        NumberPicker amPmPicker = view.findViewById(R.id.amPmPicker);

        // Set the range for hour picker (0-11)
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(11);

        // Set the range for minute picker (0-59)
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);

        // Set the displayed values for AM/PM picker
        amPmPicker.setMinValue(0);
        amPmPicker.setMaxValue(1);
        amPmPicker.setDisplayedValues(new String[]{"AM", "PM"});

        // Example of retrieving the selected time
        int selectedHour = hourPicker.getValue();
        int selectedMinute = minutePicker.getValue();
        String selectedAmPm = amPmPicker.getValue() == 0 ? "AM" : "PM";

        return view;
    }
}
