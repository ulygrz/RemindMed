package com.example.remindmed.ui.Tasks;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.remindmed.Database.TaskRepository;

import java.util.List;

public class TaskListViewModel extends AndroidViewModel {
    private final TaskRepository repository;
    private final LiveData<List<Task>> allTask;

    public TaskListViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTask = repository.getAllTask();
    }

    public void  insert(Task task){
        repository.insert(task);
    }

    public void  update(Task task){
        repository.update(task);
    }

    public void  delete(Task task){
        repository.delete(task);
    }

    public void  deleteAllTask(){
        repository.deleteAll();
    }

    public LiveData<List<Task>> getAllTask(){
        return allTask;
    }
}
