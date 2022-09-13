package com.example.remindmed.ui.Locations;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.remindmed.Database.TaskRepository;
import com.example.remindmed.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;

import java.util.ArrayList;
import java.util.List;


public class LocationsFragment extends Fragment {

    private List<String> locations;
    private Robot robot;
    private ListView locationsListView;
    private EditText newLocationName;
    private Button okButton;
    private LinearLayout newLocationNameContainer;
    public ArrayList<String> locationsarray = new ArrayList<>();



    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        robot = Robot.getInstance(); // get an instance of the robot in order to begin using its features.
        locations = robot.getLocations(); //locations saved in the robot
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locations,container,false);


        locationsListView = view.findViewById(R.id.locationsList);
        newLocationNameContainer = view.findViewById(R.id.newlocationName_container);
        newLocationName = view.findViewById(R.id.newLocationName);
        okButton = view.findViewById(R.id.okButton);

        FloatingActionButton addNewLocationButton = view.findViewById(R.id.addNewLocationButton);
        addNewLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newLocationNameContainer.setVisibility(View.VISIBLE);
            }
        });

        okButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!newLocationName.getText().toString().trim().isEmpty()){
                    hideKeyboard();
                    newLocationNameContainer.setVisibility(View.INVISIBLE);
                    saveLocation(newLocationName);
                    displayLocationsSaved();
                    newLocationName.setText("");
                }
            }
        });
        displayLocationsSaved();

        view.findViewById(R.id.constrainLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try{
                    hideKeyboard();
                } catch (Exception e){
                    Log.d("LocationsFragment", "HideKeyBoard Exception: "+e);
                }
                return true;
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    public List<String> getLocations(){
        return robot.getLocations();
    }

    private void locationsListAdapter() {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, locations);
            locationsListView.setAdapter(adapter);
        } catch (Exception e){
            Log.d("LocationsFragment", "onCreate Exception: "+e);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
    public void saveLocation(View view){
        String location = newLocationName.getText().toString().toLowerCase().trim();
        boolean result = robot.saveLocation(location);
        Log.d("Robot", "Location saved succesfully");
        if(result){
            robot.speak(TtsRequest.create("I've successfully saved the " + location + " location.", true));
        } else {
            robot.speak(TtsRequest.create("Saved the " + location + " location failed.", true));
        }


        //TODO: errase the comment slash, when the robot is active

    }

    public List<String> createArray() {
        if(locationsarray.isEmpty()){           //Erase when Robot Active
            locationsarray.add("Küche");
            locationsarray.add("Sessel");
            locationsarray.add("Esstisch");
        }
        if (!newLocationName.getText().toString().equals("")){
            locationsarray.add(newLocationName.getText().toString().trim());
        }
        return locationsarray;
    }

    private void displayLocationsSaved() {
        //List<String> locationsarray = createArray();       //Activ when Temi SDK not Implemented
        locations = robot.getLocations();                    //Activ when Temi SDK implemented
        //locations = locationsarray;                        //Activ when Temi SDK not Implemented
        Log.d("Robot", "LocationsArray"+locationsarray);
        locationsListAdapter();
    }
}