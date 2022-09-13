package com.example.remindmed.ui.Tasks;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.remindmed.R;
import com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver;

import java.util.Calendar;

import static com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver.MONDAY;
import static com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver.TASK_INFO;
import static com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver.TUESDAY;
import static com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver.WEDNESDAY;
import static com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver.THURSDAY;
import static com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver.FRIDAY;
import static com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver.SATURDAY;
import static com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver.SUNDAY;
import static com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver.TASK_NAME;

import static com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver.RECURRING;

@Entity (tableName = "task_table")
public class Task {
    @PrimaryKey
    @NonNull
    private int taskId;
    private final int hour;
    private final int minute;
    private boolean started;
    private final boolean recurring;
    private final boolean monday;
    private final boolean tuesday;
    private final boolean wednesday;
    private final boolean thursday;
    private final boolean friday;
    private final boolean saturday;
    private final boolean sunday;
    private final String taskName;
    private final String taskInfo;
    private final long created;

    public Task(int taskId, int hour, int minute, String taskName, long created, boolean started, boolean recurring, boolean monday, boolean tuesday,boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday, String taskInfo) {
        this.taskId = taskId;
        this.hour = hour;
        this.minute = minute;
        this.taskName = taskName;
        this.created = created;
        this.started = started;
        this.recurring = recurring;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.taskInfo =taskInfo;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getTaskName() {
        return taskName;
    }

    public long getCreated() {
        return created;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public boolean isMonday() {
        return monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public String getTaskInfo(){ return taskInfo; }

    public void schedule(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, TaskBroadcastReceiver.class);
        intent.putExtra(RECURRING, recurring);
        intent.putExtra(MONDAY, monday);
        intent.putExtra(TUESDAY, tuesday);
        intent.putExtra(WEDNESDAY, wednesday);
        intent.putExtra(THURSDAY, thursday);
        intent.putExtra(FRIDAY, friday);
        intent.putExtra(SATURDAY, saturday);
        intent.putExtra(SUNDAY, sunday);
        intent.putExtra(TASK_NAME, taskName);
        intent.putExtra(TASK_INFO, taskInfo);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND,0);
        
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()){
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+1);
        }
        try {
            if (!recurring){
                String toastText = String.format("Task wird nur ein Mal acktiviert");
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),alarmPendingIntent);

            } else {
                String toastText = String.format("Task wird wiederhold");
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

                final long RUN_DAILY = 24*60*60*1000;
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), RUN_DAILY,alarmPendingIntent);
            }
        } catch (Exception e){
            Log.d("Task", "schedule Exception: "+e);
        }

        this.started = true;
    }

    public void cancelTask(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TaskBroadcastReceiver.class);
        PendingIntent taskPendingIntent = PendingIntent.getBroadcast(context, taskId, intent, 0);
        try{
            alarmManager.cancel(taskPendingIntent);
            Log.d("Task", "canceling Task");
            String toastText = String.format("Task wurde gelöscht");
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Log.d("Task", "cancelTask Exception: "+e);
        }
        this.started = false;
    }

    public String getRecurringDaysText() {
        if (!recurring) {
            return null;
        }

        String days = "";

        if (monday) {
            days += "Mo ";
        }

        if (tuesday) {
            days += "Di ";
        }

        if (wednesday) {
            days += "Mi ";
        }

        if (thursday) {
            days += "Do ";
        }

        if (friday) {
            days += "Fr ";
        }

        if (saturday) {
            days += "Sa ";
        }

        if (sunday) {
            days += "So ";
        }
        return days;
    }
}
