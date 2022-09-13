package com.example.remindmed.Services;

import android.content.Intent;
import android.os.IBinder;
import android.app.Application;
import android.app.Service;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.example.remindmed.Database.TaskRepository;
import com.example.remindmed.ui.Tasks.Task;

import java.util.List;

public class RescheduleTaskService extends LifecycleService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        TaskRepository taskRepository = new TaskRepository(getApplication());

        taskRepository.getAllTask().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                for (Task newTask : tasks){
                    if(newTask.isStarted()){
                        newTask.schedule(getApplicationContext());
                    }
                }
            }
        });

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }
}
