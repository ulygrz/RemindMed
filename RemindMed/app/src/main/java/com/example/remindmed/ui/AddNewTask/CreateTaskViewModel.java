package com.example.remindmed.ui.AddNewTask;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.remindmed.Database.TaskRepository;
import com.example.remindmed.ui.Tasks.Task;

class CreateTaskViewModel extends AndroidViewModel {
    private final TaskRepository alarmRepository;

    public CreateTaskViewModel(@NonNull Application application) {
        super(application);

        alarmRepository = new TaskRepository(application);
    }

    public void insert(Task task) {
        alarmRepository.insert(task);
    }
}
