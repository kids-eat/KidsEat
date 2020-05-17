package com.example.kidseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.kidseat.fragments.MapFragment;
import com.example.kidseat.fragments.TimelineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public BottomNavigationView bottomNavigationView;
    public FirebaseAuth mAuth;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        mAuth = FirebaseAuth.getInstance();

        // Listener for the bottom navigation view:
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                // use switch to select the fragments
                switch (item.getItemId()) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create the menu on action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection from the menu
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                mAuth.signOut();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
