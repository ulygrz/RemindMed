package com.example.remindmed.ui.TaskBroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.remindmed.Services.RescheduleTaskService;
import com.example.remindmed.Services.SearchingUserService;

import java.util.Calendar;

public class TaskBroadcastReceiver extends BroadcastReceiver {

    public static final String MONDAY = "MONDAY";
    public static final String TUESDAY = "TUESDAY";
    public static final String WEDNESDAY = "WEDNESDAY";
    public static final String THURSDAY = "THURSDAY";
    public static final String FRIDAY = "FRIDAY";
    public static final String SATURDAY = "SATURDAY";
    public static final String SUNDAY = "SUNDAY";
    public static final String RECURRING = "RECURRING";
    public static final String TASK_NAME ="TASKNAME";
    public static final String TASK_INFO ="TASKINFO";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BroadcastReceiver", "Received");
        Log.d("BroadcastReceiver", "Received"+intent.getAction());


        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            String toastText = String.format("Task Reboot");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

            startRescheduleTaskService(context);
        } else {
            String toastText = String.format("Task Received");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

            if (!intent.getBooleanExtra(RECURRING, false) || taskIsToday(intent)) {
                startTaskService(context, intent);
            }
        }
    }

    private boolean taskIsToday(Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        switch (today) {
            case Calendar.MONDAY:
                return intent.getBooleanExtra(MONDAY, false);
            case Calendar.TUESDAY:
                return intent.getBooleanExtra(TUESDAY, false);
            case Calendar.WEDNESDAY:
                return intent.getBooleanExtra(WEDNESDAY, false);
            case Calendar.THURSDAY:
                return intent.getBooleanExtra(THURSDAY, false);
            case Calendar.FRIDAY:
                return intent.getBooleanExtra(FRIDAY, false);
            case Calendar.SATURDAY:
                return intent.getBooleanExtra(SATURDAY, false);
            case Calendar.SUNDAY:
                return intent.getBooleanExtra(SUNDAY, false);
        }
        return false;
    }

    private void startRescheduleTaskService(Context context) {
        Intent intentService = new Intent(context, RescheduleTaskService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
        String toast = String.format("Task wurde verschoben ");
        Toast.makeText(context,toast,Toast.LENGTH_LONG).show();
    }

    private void startTaskService(Context context, Intent intent) {
        String taskName = intent.getStringExtra(TASK_NAME);
        String taskInfo = intent.getStringExtra(TASK_INFO);
        Log.d("TaskBroadcastReceiver", "Task name: "+taskName);
        Log.d("TaskBroadcastReceiver", "Task info: "+taskInfo);

        Intent intentService = new Intent(context, SearchingUserService.class);
        intentService.putExtra(TASK_NAME, taskName);
        intentService.putExtra(TASK_INFO, taskInfo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
        String toast = String.format("Starting SearchingTask!");
        Toast.makeText(context,toast,Toast.LENGTH_LONG).show();
    }
}
