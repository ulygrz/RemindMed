package com.example.remindmed;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.remindmed.ui.AddNewTask.AddNewTaskFragment;
import com.example.remindmed.ui.Locations.LocationsFragment;
import com.example.remindmed.ui.Path.PathFragment;
import com.example.remindmed.ui.Tasks.TaskListViewModel;
import com.example.remindmed.ui.Tasks.TaskRecyclerViewAdapter;
import com.example.remindmed.ui.Tasks.TasksFragment;
import com.example.remindmed.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private TaskRecyclerViewAdapter taskRecyclerViewAdapter;
    private TaskListViewModel taskListViewModel;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        navView.setSelectedItemId(R.id.navigation_home);
        navView.setOnNavigationItemSelectedListener(navListener);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_locations, R.id.navigation_path, R.id.navigation_Tasks, R.id.navigation_add)
                .build();
        /*NavController navController = Navigation.findNavController(this, R.id.fragment_container);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

         */

    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch(item.getItemId()){
                case R.id.navigation_locations :
                    selectedFragment = new LocationsFragment();
                    break;
                case R.id.navigation_path :
                    selectedFragment = new PathFragment();
                    break;
                case R.id.navigation_home :
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.navigation_add :
                    selectedFragment = new AddNewTaskFragment();
                    break;
                case R.id.navigation_Tasks :
                    selectedFragment = new TasksFragment();
                    break;
            }
            try {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            } catch (Exception e){
                Log.d("MainActivity", "BottomNavigationView Exception: "+e);
            }

            return true;
        }
    };
}