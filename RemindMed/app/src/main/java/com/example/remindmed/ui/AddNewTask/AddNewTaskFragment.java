package com.example.remindmed.ui.AddNewTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;

import com.example.remindmed.Database.TaskRepository;
import com.example.remindmed.R;
import com.example.remindmed.ui.Tasks.Task;
import com.example.remindmed.ui.Tasks.TasksFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AddNewTaskFragment extends Fragment {

    @BindView(R.id.fragment_creatTask_timePicker) TimePicker timePicker;
    @BindView(R.id.fragment_createTask_title) EditText taskTitle;
    @BindView(R.id.fragment_createTask_scheduleTask) Button saveTask;
    @BindView(R.id.fragment_createTask_recurring) CheckBox recurring;
    @BindView(R.id.fragment_createTask_checkMon) CheckBox mon;
    @BindView(R.id.fragment_createTask_checkTue) CheckBox tue;
    @BindView(R.id.fragment_ccreateTask_checkWed) CheckBox wed;
    @BindView(R.id.fragment_createTask_checkThu) CheckBox thu;
    @BindView(R.id.fragment_createTask_checkFri) CheckBox fri;
    @BindView(R.id.fragment_createTask_checkSat) CheckBox sat;
    @BindView(R.id.fragment_createTask_checkSun) CheckBox sun;
    @BindView(R.id.fragment_createTask_recurring_options) LinearLayout recurringDays;
    @BindView(R.id.fragment_taskExtraInfo) EditText taskInfo;

    private CreateTaskViewModel createTaskViewModel;
    SharedPreferences savedPath;
    private List<String> path = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            createTaskViewModel = new CreateTaskViewModel(this.getActivity().getApplication());
        } catch (Exception e){
            Log.d("onCreate", "Exception: "+e);
        }
        savedPath = requireContext().getSharedPreferences("savedPath_file", Context.MODE_PRIVATE);
        path = new Gson().fromJson(savedPath.getString("savedPath_file", null),new TypeToken<List<String>>(){}.getType());
        //NavController navigationController = Navigation.findNavController(getActivity(), R.id.fragment_container);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addnewtask, container, false);


        ButterKnife.bind(this, view);
        recurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    recurringDays.setVisibility(View.VISIBLE);
                } else {
                    recurringDays.setVisibility(View.GONE);
                }
            }
        });

        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!path.isEmpty()){
                    saveTask();
                    Log.d("AddNT","Task saved"+v);

                    TasksFragment newFragment = new TasksFragment();
                    try {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container,newFragment,"seeTaskList").commit();
                    } catch (Exception e){
                        Log.d("saveTask", "Exception: "+e);
                    }
                } else {
                    new AlertDialog.Builder(getContext()).setTitle("Kein Pfad gespeichert").setMessage("Vor der Erstellung eines Ereignisses, speichern Sie einen Pfad").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
                }
            }
        });

        view.findViewById(R.id.linearLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    hideKeyboard();
                } catch (Exception e) {
                    Log.d("LocationsFragment", "HideKeyBoard Exception: " + e);
                }
                return true;
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        timePicker = view.findViewById(R.id.fragment_creatTask_timePicker);
        timePicker.setIs24HourView(true);
        if (path.isEmpty()){
            new AlertDialog.Builder(getContext()).setTitle("Kein Pfad gespeichert").setMessage("Vor der Erstellung eines Ereignisses, speichern Sie einen Pfad").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();
        }
    }

    private void saveTask() {

        int taskId = new Random().nextInt(Integer.MAX_VALUE);

        Task task =new Task(
                taskId,
                getTimePickerHour(timePicker),
                getTimePickerMinute(timePicker),
                taskTitle.getText().toString(),
                System.currentTimeMillis(),
                true,
                recurring.isChecked(),
                mon.isChecked(),
                tue.isChecked(),
                wed.isChecked(),
                thu.isChecked(),
                fri.isChecked(),
                sat.isChecked(),
                sun.isChecked(),
                taskInfo.getText().toString()
        );

        Log.d("saveTask","Task: "+task);

        createTaskViewModel.insert(task);
        try {
            task.schedule(getContext());
        } catch (Exception e){
            Log.d("createTaskViewModel", "Exception: "+e);
        }

    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public int getTimePickerHour(TimePicker tp){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return tp.getHour();
        } else {
            return tp.getCurrentHour();
        }
    }

    public  int getTimePickerMinute(TimePicker tp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return tp.getMinute();
        } else {
            return tp.getCurrentMinute();
        }
    }

}