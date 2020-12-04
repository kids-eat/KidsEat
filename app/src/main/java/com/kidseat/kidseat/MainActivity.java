package com.kidseat.kidseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kidseat.kidseat.fragments.MapFragment;
import com.kidseat.kidseat.fragments.TimelineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String FCM_TOKEN = "fcmToken";

    public BottomNavigationView bottomNavigationView;
    public FirebaseAuth mAuth;
    public FirebaseFirestore dbFirestore;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        mAuth = FirebaseAuth.getInstance();
        dbFirestore = FirebaseFirestore.getInstance();

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
                FirebaseUser user = mAuth.getCurrentUser();
                assert user != null;
                removeFCMToken(user);
                mAuth.signOut();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeFCMToken(FirebaseUser user) {
        // Remove the 'fcmToken' field from the document associated with the user

        String uid = user.getUid();
        Map<String, Object> updates = new HashMap<>();
        updates.put(FCM_TOKEN, FieldValue.delete());
        dbFirestore.collection("users").document(uid).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Task Completed!
            }
        });
    }


}
