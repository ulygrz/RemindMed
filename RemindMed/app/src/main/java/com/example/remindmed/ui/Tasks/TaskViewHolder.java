package com.example.remindmed.ui.Tasks;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindmed.R;

import java.util.List;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    private final TextView taskTime;
    private final ImageView taskRecurring;
    private final TextView taskName;
    private final TextView taskRecurringDays;
    private final OnSwitchTaskListener listener;
    Switch taskStarted;


    public TaskViewHolder(@NonNull View itemView, OnSwitchTaskListener listener, List<Task> tasks) {
        super(itemView);

        taskTime = itemView.findViewById(R.id.item_task_time);
        taskRecurring = itemView.findViewById(R.id.item_task_recurring);
        taskName = itemView.findViewById(R.id.item_task_title);
        taskRecurringDays = itemView.findViewById(R.id.item_task_recurringDays);
        taskStarted = itemView.findViewById(R.id.item_task_started);


        this.listener = listener;
    }

    public void bind(Task task){
        String taskTimeText = String.format("%02d:%02d", task.getHour(), task.getMinute());
        taskTime.setText(taskTimeText);
        taskStarted.setChecked(task.isStarted());

        if (task.isRecurring()){
            taskRecurring.setImageResource(R.drawable.ic_repeat_black_24dp);
            taskRecurringDays.setText(task.getRecurringDaysText());
        } else {
            taskRecurring.setImageResource(R.drawable.ic_looks_one_black_24dp);
            taskRecurringDays.setText("");
        }

        if (task.getTaskName().length() != 0){
            taskName.setText(String.format("%s | %d", task.getTaskName(), task.getTaskId()));
        }

        taskStarted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.update(task);
                Log.d("update", "TaskViewHolder Listener");
            }
        });
    }


}
