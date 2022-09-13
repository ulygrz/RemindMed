package com.example.remindmed.ui.Tasks;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.remindmed.R;

import java.util.List;

public class TasksFragment extends Fragment implements OnSwitchTaskListener{

    private TaskRecyclerViewAdapter taskRecyclerViewAdapter;
    private TaskListViewModel taskListViewModel;
    private RecyclerView recyclerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskRecyclerViewAdapter = new TaskRecyclerViewAdapter(this);
        taskListViewModel = new ViewModelProvider(this).get(TaskListViewModel.class);
        taskListViewModel.getAllTask().observe(this, new Observer<List<Task>>(){

            @Override
            public void onChanged(List<Task> tasks) {
                if (tasks != null){
                    taskRecyclerViewAdapter.setTasks(tasks);
                    Log.d("onChanged", "Task Started: "+tasks);

                    //String toastText = String.format("Task Status Changed");
                    //Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        recyclerView = view.findViewById(R.id.fragment_listalarms_recylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskRecyclerViewAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                try {
                    taskListViewModel.delete(taskRecyclerViewAdapter.getTaskAt(viewHolder.getBindingAdapterPosition()));
                    taskRecyclerViewAdapter.getTaskAt(viewHolder.getBindingAdapterPosition()).cancelTask(getContext());
                } catch (Exception e){
                    Log.d("Delete Task", "Exceptiom: "+e);
                }



                /*AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Task Löschen");
                dialog.setMessage("Wollen Sie den Task löschen")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        taskListViewModel.delete(taskRecyclerViewAdapter.getTaskAt(viewHolder.getBindingAdapterPosition()));
                        Toast.makeText(getContext(),"Task gelöscht", Toast.LENGTH_LONG);
                    }
                })
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                    }
                });*/
            }
        }).attachToRecyclerView(recyclerView);


        return view;
    }

    private void showDialog(RecyclerView.ViewHolder viewHolder){

    }

    @Override
    public void update(Task task) {
        Log.d("update", "TaskFragment update");
        try{
            if (task.isStarted()){
                Log.d("update", "Task Started: "+task.isStarted());

                task.cancelTask(getContext());
                taskListViewModel.update(task);
            } else {
                Log.d("update", "Task Started: "+task.isStarted());
                task.schedule(getContext());
                taskListViewModel.update(task);
            }
        } catch (Exception e){
            Log.d("Task", "cancelTask Exception: "+e);
        }

        String toastText = String.format("Task Status Changed");
        Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
    }
}