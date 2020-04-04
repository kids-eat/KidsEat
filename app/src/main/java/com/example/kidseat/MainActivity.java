package com.example.kidseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.kidseat.fragments.MapFragment;
import com.example.kidseat.fragments.TimelineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Listener for the bottom navigation view:
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                // use switch to select the fragments
                switch (item.getItemId()) {
                    case R.id.action_list:
                        fragment = new TimelineFragment();
                        break;
                    case R.id.action_map:
                        fragment = new MapFragment();
                        break;
                    default:
                        fragment = new TimelineFragment();
                        break;
                }
                // replace the container with the appropriate fragment
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        // Set default tab selection to List View
        bottomNavigationView.setSelectedItemId(R.id.action_list);

    }


}
