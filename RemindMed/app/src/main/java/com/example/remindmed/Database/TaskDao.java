package com.example.remindmed.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.remindmed.ui.Tasks.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Query("SELECT * FROM task_table ORDER BY created ASC")
    LiveData<List<Task>> getAllTask();

    @Query("DELETE FROM task_table")
    void deleteAllTask();

    @Delete
    void delete(Task task);

    @Update
    void update(Task task);
}
