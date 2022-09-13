package com.example.remindmed.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.remindmed.R;
import com.example.remindmed.Services.SearchingUserService;
import com.example.remindmed.ui.Tasks.Task;
import com.robotemi.sdk.Robot;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver.TASK_INFO;
import static com.example.remindmed.ui.TaskBroadcastReceiver.TaskBroadcastReceiver.TASK_NAME;

public class NotificationActivity extends AppCompatActivity {

    @BindView(R.id.notification_titel) TextView taskTitel;
    @BindView(R.id.notification_text) TextView taskInfo;
    @BindView(R.id.okAndStayButton) Button confirmAndStayButton;
    @BindView(R.id.goToHomeBaseButton) Button confirmAndGoToBase;
    private Robot robot;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        robot = Robot.getInstance();
        ButterKnife.bind(this);

        Log.d("NotificationsActivity", "Task Name: "+getIntent().getStringExtra(TASK_NAME));
        Log.d("NotificationsActivity", "Task Notiz: "+getIntent().getStringExtra(TASK_INFO));

        taskTitel.setText(getIntent().getStringExtra(TASK_NAME));
        taskInfo.setText(getIntent().getStringExtra(TASK_INFO));

        try{
            getApplicationContext().stopService(new Intent(getApplicationContext(), SearchingUserService.class));
        } catch (Exception e){
            Log.e("NotificationActivity", "Exception: "+e);
        }


        confirmAndStayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                robot.stopMovement();
                finish();
            }
        });

        confirmAndGoToBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo();
                finish();

            }
        });

    }

    public void goTo(){
        robot.goTo("home basis");
    }
    //TODO: Add listener to know if the Robot i already ar home
}