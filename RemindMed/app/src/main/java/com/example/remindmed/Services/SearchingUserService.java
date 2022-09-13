package com.example.remindmed.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.CheckResult;
import androidx.annotation.Nullable;

import com.example.remindmed.ui.Tasks.Task;
import com.example.remindmed.ui.notifications.NotificationActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.listeners.OnDetectionDataChangedListener;
import com.robotemi.sdk.listeners.OnDetectionStateChangedListener;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnUserInteractionChangedListener;
import com.robotemi.sdk.model.DetectionData;
import com.robotemi.sdk.permission.Permission;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver.TASK_INFO;
import static com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver.TASK_NAME;

public class SearchingUserService extends Service  implements OnDetectionStateChangedListener,
        OnUserInteractionChangedListener,
        OnDetectionDataChangedListener,
        OnGoToLocationStatusChangedListener
        {

    private Robot robot;
    SharedPreferences savedPath;
    private int pathIndex= 0;
    private List<String> path = new ArrayList<>();
    private volatile int detectionState = 0; //Changed from stateDetection to detectionState
    private volatile  int detectionAngle;
    private List<String> locations;
    private int interactionCounter = 0;
    private double angle;
    private double distance;
    private boolean isDetected;
    private String taskName, taskInfo;

    private static final int REQUEST_CODE_START_DETECTION_WITH_DISTANCE = 6;


    @Override
    public void onCreate() {
        super.onCreate();

        //--------------Robot Instance--------------
        robot = Robot.getInstance();
        locations = robot.getLocations();
            //locationListSize = locations.lastIndexOf(locations);



        //--------------Path--------------

        savedPath = getSharedPreferences("savedPath_file", Context.MODE_PRIVATE);
        path = new Gson().fromJson(savedPath.getString("savedPath_file", null),new TypeToken<List<String>>(){}.getType());
        Log.d("SearchingTaskService", "Saved Path:"+path);


        //--------------Robot Listeners--------------
        robot.addOnDetectionDataChangedListener(this);
        robot.addOnUserInteractionChangedListener(this);
        robot.addOnDetectionStateChangedListener(this);
        robot.addOnGoToLocationStatusChangedListener(this);



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SearchingTaskService", "Service starting ");
        Log.d("SearchingTaskService", "TASK_NAME: "+intent.getStringExtra(TASK_NAME));
        Log.d("SearchingTaskService", "TASK_INFO: "+intent.getStringExtra(TASK_INFO));

        taskName = intent.getStringExtra(TASK_NAME);
        taskInfo = intent.getStringExtra(TASK_INFO);
        //robot.setDetectionModeOn(true);
        startDetection();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        robot.removeOnDetectionDataChangedListener(this);
        robot.removeOnUserInteractionChangedListener(this);
        robot.removeOnDetectionStateChangedListener(this);
        robot.removeOnGoToLocationStatusChangedListener(this);
        try{
            Intent intentService = new Intent(getApplicationContext(), Task.class);
            getApplicationContext().stopService(intentService);
        } catch (Exception e){
            Log.e("NotificationActivity", "Exception: "+e);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }




    /*-------------------------------------- User Quest functions --------------------------------------*/


    private void startDetection() {
        if (requestPermissionIfNeeded(Permission.SETTINGS, REQUEST_CODE_START_DETECTION_WITH_DISTANCE)) {
            return;
        }
        pathIndex = 0;
        goTo(pathIndex);
        Log.d("StartDetection", "detectionMode: "+robot.isDetectionModeOn());
        //detecting(); TODO: erase if the process works
    }

    private void detecting(){
        if (!robot.isDetectionModeOn()){
            robot.setDetectionModeOn(true);
            Log.d("Detecting", "Mode: "+robot.isDetectionModeOn());
        }

        detectionAngle = 0;
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try{
                    if (detectionState != OnDetectionStateChangedListener.DETECTED && detectionAngle<360) {
                        turnBy();
                        detectionAngle = detectionAngle + 45;
                    } else if (detectionState == OnDetectionStateChangedListener.DETECTED){
                        detectionAngle = 360;
                    }
                } catch (Exception e){
                    Log.d("Robot", "Exception: "+ e);
                }finally {

                }
                if (detectionAngle == 360) {
                    if(detectionState != OnDetectionStateChangedListener.DETECTED) {
                        Log.d("Detecting...", "No user detected, going to the next position in the path");
                        pathIndex = pathIndex + 1;
                        goTo(pathIndex);
                        //TODO: Check if it does work, if not move if-statement outside of scheduleTaskExecutor!
                    }
                    Log.d("detectingProces", "Stop turning around, ScheduleTaskExecutor stopped");
                    scheduleTaskExecutor.shutdown();

                }


            }
        },0,5000, TimeUnit.MILLISECONDS);
    }

    private void turnBy(){
        if (detectionState != OnDetectionStateChangedListener.DETECTED) {
            robot.turnBy(45);
            Log.d("TurnBy", "Robot turning around");
        }
    }

    private void goTo(int index){
        if (index < path.size()){
            Log.d("goTo", "going to:"+path.get(index));
            robot.goTo(path.get(index));
        } else { robot.goTo("home base");}

    }

    @CheckResult
    private boolean requestPermissionIfNeeded(Permission permission, int requestCode) {
        if (robot.checkSelfPermission(permission) == Permission.GRANTED) {
            return false;
        }
        robot.requestPermissions(Collections.singletonList(permission), requestCode);
        return true;
    }

    private void interacting(){
        Log.d("Interaction", "Temi is interacting with a user");
        if ( detectionState == OnDetectionStateChangedListener.DETECTED){
            robot.stopMovement();
            robot.setDetectionModeOn(false);


        }


    }

    //--------------------------------------Temi Listeners--------------------------------------

    @Override
    public void onUserInteraction(boolean isInteracting) {
    if (isInteracting && interactionCounter<1){
        interactionCounter++;
        interacting();
    }

    }


    @Override
    public void onDetectionDataChanged(@NotNull DetectionData detectionData) {

    }

    @Override
    public void onDetectionStateChanged(int state) {
        Log.d("DetectionActivity", "onDetectionStateChanged - State:"+state);
        if(state == OnDetectionStateChangedListener.DETECTED){
            //robot.stopMovement();
            //TODO: Restrict to once detectionStateChange to trigger NotificationActivity and the Speak request
            Log.d("onDetectionStateChanged", "User detected");
            detectionState = state;
                robot.speak(TtsRequest.create("Hallo, du hast ein gespeichertes Ereignis", true));
                robot.beWithMe();

            Intent notificationIntent = new Intent(this, NotificationActivity.class);
            notificationIntent.putExtra(TASK_NAME, taskName);
            notificationIntent.putExtra(TASK_INFO, taskInfo);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            String toastText = String.format("Starting NotificationsActivity");
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
            startActivity(notificationIntent);
            //Stop Service
            Intent intentService = new Intent(getApplicationContext(),Task.class);
            getApplicationContext().stopService(intentService);

        } else if (state == IDLE){
            detectionState = state;
            Log.d("onDetectionStateChanged", "Nobody detected");
            //robot.turnBy(45);
        } else if(state == OnDetectionStateChangedListener.LOST){
            detectionState = state;
            robot.turnBy(45);
            robot.turnBy(-90);
        }

    }

    @Override
    public void onGoToLocationStatusChanged(@NotNull String location, String status, int descriptionId, @NotNull String description) {
        try{
            if (pathIndex < path.size()) {
                Log.d("onGoToLocationStatus", "Location: " + location);
                Log.d("onGoToLocationStatus", "PathLocation: " + path.get(pathIndex));
            }
        } catch (Exception e){
            Log.e("onGoToLocationStatus", "Exception: "+e);

        }

        switch (status) {
            case OnGoToLocationStatusChangedListener.START:
                break;

            case OnGoToLocationStatusChangedListener.CALCULATING:
                break;

            case OnGoToLocationStatusChangedListener.GOING:
                break;

            case OnGoToLocationStatusChangedListener.COMPLETE:

                try{//if the Location is the same as the location in the path(index) then detecting() will star
                    if (pathIndex < path.size()) {
                        Log.d("onGoToLocationStatus", "COMPLETE");
                        Log.d("onGoToLocationStatus", "Location: "+location+" PathLocation: "+path.get(pathIndex));

                        if(location.equals(path.get(pathIndex))){
                            Log.d("onGoToLocationStatus", "Detecting...");
                            detecting();
                        } else {
                            Log.d("onGoToLocationStatus", "Location doesn't match...");
                            //TODO: See if condition is met, if not then place detecting() outside, and see why the condition won't be fulfilled
                        }
                        if (location.equals("home base") && detectionState==0){
                            rescheduleTask();
                        }
                    }
                } catch (Exception e){
                    Log.e("onGoToLocationStatus", "Exception: "+e);
                }

                if (location.equals("home base") && detectionState==0){
                    rescheduleTask();
                }
                break;

            case OnGoToLocationStatusChangedListener.ABORT:
                break;
        }
    }

    private void rescheduleTask() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR, 1);
        //calendar.add(Calendar.MINUTE, 10);


        Task task = new Task (
                new Random().nextInt(Integer.MAX_VALUE),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                taskName,
                System.currentTimeMillis(),
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                taskInfo
        );

        task.schedule(getApplicationContext());
        Intent intentService = new Intent(getApplicationContext(),Task.class);
        getApplicationContext().stopService(intentService);
    }
}
