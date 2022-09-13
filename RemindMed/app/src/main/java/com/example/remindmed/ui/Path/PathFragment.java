package com.example.remindmed.ui.Path;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.example.remindmed.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.robotemi.sdk.Robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PathFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

    private Robot robot;
    private FloatingActionButton addNewLocation;
    private ListView pathList;
    private Spinner pathSpinner;
    private ImageButton savePathButton;
    private ImageButton restartPathButton;

    private final java.util.ArrayList<String> savedLocations = new java.util.ArrayList<>();
    private List<String> locations;
    private java.util.ArrayList<String> path = new java.util.ArrayList<>();
    public SharedPreferences savedPath = null;
    private ArrayAdapter<String> listAdapter;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robot = Robot.getInstance();
        locations = robot.getLocations();
        savedPath = requireContext().getSharedPreferences("savedPath_file", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_path, container, false);

        addNewLocation = view.findViewById(R.id.addNewLocationButton);
        pathSpinner = view.findViewById(R.id.spinner);
        pathList = view.findViewById(R.id.pathListView);
        savePathButton = view.findViewById(R.id.saveButton);
        restartPathButton = view.findViewById(R.id.resetButton);

        //--------------Path--------------
        savedPath = requireContext().getSharedPreferences("savedPath_file", Context.MODE_PRIVATE);
        //locations = createTryArray();  //TODO: Erase this line, when the app is installed in the robot
        Log.d("SavedLocations", "saved Locations: "+locations);
        getSavedPath();
        getSaveLocations();
        setPathSpinnerItems();

        addNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path != null) {
                    //AlertDialog.Builder builder = new AlertDialog.Builder();
                }
                pathSpinner.setVisibility(View.VISIBLE);
                savePathButton.setVisibility(View.VISIBLE);
                pathSpinner.setOnItemSelectedListener(PathFragment.this); //TODO: Check if the spinner works with the onItemSelectedListener here
            }
        });

        savePathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePath();
                spinnerAdapter.notifyDataSetChanged();
                pathSpinner.setVisibility(View.INVISIBLE);
            }
        });

        restartPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savedPath != null && listAdapter != null) {
                    SharedPreferences.Editor editor = savedPath.edit();
                    editor.clear().apply();
                    getSaveLocations();
                    listAdapter.notifyDataSetChanged();
                    spinnerAdapter.notifyDataSetChanged();
                    path.clear();
                }
            }
        });

        return view;
    }

    private void setPathSpinnerItems() {
        spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,savedLocations);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pathSpinner.setAdapter(spinnerAdapter);
    }


    //------------------------- Save Path -------------------------//
    private void savePath() {
        SharedPreferences.Editor pathEditor = savedPath.edit();
        pathEditor.putString("savedPath_file", new Gson().toJson(path)).apply();
        Log.d("savePath", "Path: "+new Gson().fromJson(savedPath.getString("savedPath_file", null),new TypeToken<List<String>>(){}.getType()));
    }

    //------------------------- Save Locations for Spinner-------------------------
    private void getSaveLocations() {
        if (!savedLocations.contains("Wähle einen Ort")){
            savedLocations.add("Wähle einen Ort");
        }
        for(String string : locations){
            if(!string.equals("home base") && !savedLocations.contains(string)){
                savedLocations.add(string);
            }
        }
        //TODO: Home base will be called, when the path is completed
    }

    //-------- Creates an Array for the tests------------
    public List<String> createTryArray() {
        ArrayList<String> locationsArray = new ArrayList<>();
        locationsArray.add("Küche");
        locationsArray.add("Sessel");
        locationsArray.add("Esstisch");
        locationsArray.add("Wohnzimmer");
        locationsArray.add("Terrase");
        return locationsArray;
    }

    //------------------------- get saved Path -------------------------
    private void getSavedPath() {
        //savedPath = getSharedPreferences("savedPath_file", Context.MODE_PRIVATE);
        if (savedPath != null) {
            try{
                path = new Gson().fromJson(savedPath.getString("savedPath_file", null),new TypeToken<List<String>>(){}.getType());
                if (path != null){
                    pathListAdapter();
                }
                Log.d("getSavedPath", "Path"+path);
            } catch (Exception e){

                Log.d("getSavedPath", "Exception: "+ e);
                Log.d("getSavedPath", "Path is null!");
                Log.d("getSavedPath", "Path is empty?: "+path.isEmpty());
            }
        }
        if(path == null){
            path = new ArrayList<>();
        }
    }

    //------------------------- Adapter to fill the PathListView -------------------------
    private void pathListAdapter() {
        pathSpinner.setVisibility(View.INVISIBLE);
        pathSpinner.setSelection(0);
        Log.d("pathListAdapter", "creating Adapter");
        listAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, path);
        pathList.setAdapter(listAdapter);
        Log.d("pathListAdapter", "Path: "+path);
    }

    //------------------------- Spinner OnItemSelectedListener -------------------------
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try{
            if (!pathSpinner.getSelectedItem().toString().equals("Wähle einen Ort")) {
                String itemSelected = pathSpinner.getSelectedItem().toString();
                //path.add(pathSpinner.getSelectedItem().toString());
                path.add(itemSelected);
                Log.d("SpinnerOnItemSelected", "ItemSelectedfromSpinner: "+pathSpinner.getSelectedItem());
                Log.d("SpinnerOnItemSelected", "ItemSelected: "+itemSelected);

                if (path.contains(itemSelected) && savedLocations.contains(itemSelected)){
                    savedLocations.remove(itemSelected);
                    spinnerAdapter.notifyDataSetChanged();
                }

            }
        } catch (Exception e){
            Log.d("SpinnerOnItemSelected", "Exception: "+e);
        }
        if(path != null){
            pathListAdapter();
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        pathSpinner.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}