package com.example.bookandpostroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class dialogforfilter extends BottomSheetDialogFragment {
    private EditText locationEditText;
    private RadioGroup locationRadioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filteroutrooms, container, false);

        // Initialize views
        locationEditText = view.findViewById(R.id.locationet);
        locationRadioGroup = view.findViewById(R.id.location_radio_group);
        Button filterButton = view.findViewById(R.id.filterbtn);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user's input from EditText
                String place = locationEditText.getText().toString().trim();

                // Get the selected RadioButton
                int selectedId = locationRadioGroup.getCheckedRadioButtonId();

                // Note: We use the root view (view) here, not the clicked view (v)
                RadioButton selectedRadioButton = view.findViewById(selectedId);

                // Check for input validity
                if (place.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a location", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if a RadioButton is selected
                if (selectedRadioButton != null) {
                    // Get the selected radio button text
                    String district = selectedRadioButton.getText().toString();

                    // Display the results
                    BottomSheetDialogFragment sheet=fragmentaftersearchbuttonhasbeenclicked.newInstance(place,district);
                    sheet.show(getActivity().getSupportFragmentManager(), "BottomSheetFragment");

                } else {
                    // No RadioButton selected
                    Toast.makeText(getContext(), "Please select a district", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

}