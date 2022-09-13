package com.example.remindmed.ui.home;

import android.app.AlarmManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindmed.Database.TaskRepository;
import com.example.remindmed.R;
import com.example.remindmed.ui.Tasks.Task;
import com.example.remindmed.ui.Tasks.TaskListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView nextTaskTitel;
    private TextView nextTaskInfo;
    private TextView nextTaskRecurringDays;

    SharedPreferences savedPath;
    private FloatingActionButton info;
    private TaskListViewModel tasksListViewModel;
    private Task nextTask;
    private TaskRepository repository;
    Calendar currentCalendar;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        savedPath = requireContext().getSharedPreferences("savedPath_file", Context.MODE_PRIVATE);
        ArrayList<String> path = new Gson().fromJson(savedPath.getString("savedPath_file", null),new TypeToken<List<String>>(){}.getType());
        Log.d("HomeFragment", "Saved Path:"+path);

        currentCalendar= Calendar.getInstance();
        currentCalendar.getTimeInMillis();
        Log.d("HomeFragment", "Current Time"+currentCalendar.getTimeInMillis());
        try {
            repository = new TaskRepository(getActivity().getApplication());
        } catch (Exception e){
            Log.d("HomeFragment", "onCreate Exception: "+e);
        }
        //allTask = repository.getAllTask();
       // nextTask = getNextTask(allTask.getValue(),currentCalendar);

        tasksListViewModel = new ViewModelProvider(this).get(TaskListViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);

        nextTaskTitel = view.findViewById(R.id.nextTaskName);
        nextTaskRecurringDays = view.findViewById(R.id.recurringDays);
        nextTaskInfo = view.findViewById(R.id.taskExtraInfo);

        repository.getAllTask().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {

            @Override
            public void onChanged(List<Task> tasks) {
                try {
                    if (tasks != null) {
                        int index = getNextTask(tasks,currentCalendar);
                        Log.d("HomeFragment", "Task index"+index);

                        nextTaskTitel.setText(tasks.get(index).getTaskName());
                        nextTaskRecurringDays.setText(tasks.get(index).getRecurringDaysText());
                        nextTaskInfo.setText(tasks.get(index).getTaskInfo());
                    }
                } catch (Exception e){
                    Log.e("HomeFragment", "Exception: "+e);

                }

            }
        });

        info = view.findViewById(R.id.infoButton);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toastText = String.format("Information wird gezeigt");
                Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private int getNextTask(List<Task> tasks, Calendar currentCalendar) {

        Log.d("HomeFragment", "Amount of Tasks:"+tasks.size());

        Calendar nextEvent = Calendar.getInstance();
        nextTask = null;
        long tempTaskInMs = 0;
        long helpMilis = 0;

        int index = 0;
        for (int i = 0; i<tasks.size(); i++){

            Calendar cal = getTaskInMilliseconds(tasks.get(i));
            int temp = cal.compareTo(currentCalendar);

            if (temp > 0){
                tempTaskInMs = cal.getTimeInMillis() - currentCalendar.getTimeInMillis();
                Log.d("HomeFragment", "temptaskInMs: "+tempTaskInMs);

                if (i == 0){
                    helpMilis = tempTaskInMs;
                    index = i;
                }
                if (tempTaskInMs < helpMilis){
                    helpMilis = tempTaskInMs;
                    index = i;
                }
            }
        }
        nextTask = tasks.get(index);
        Log.d("HomeFragment", "nextTask: "+nextTask.getTaskId() + "Index: "+index);
        return index;
    }

    private Calendar getTaskInMilliseconds(Task task){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,task.getHour());
        calendar.set(Calendar.MINUTE, task.getMinute());
        calendar.set(Calendar.SECOND,0);
        calendar.getTimeInMillis();
        return calendar;
    }
}