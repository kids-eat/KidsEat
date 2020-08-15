package com.kidseat.kidseat.organizer_activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kidseat.kidseat.LoginActivity;
import com.kidseat.kidseat.R;
import com.kidseat.kidseat.adapters.EventsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;


public class OrganizerMainActivity extends AppCompatActivity implements EventsAdapter.OnEventSelectedListener {

    private static final String TAG = "OrganizerMainActivity";

    public static int LIMIT = 20;
    public static final String CREATED_AT_KEY = "created_at";

    FirebaseFirestore dbFirestore;
    FirebaseAuth mAuth;
    private Query query;

    LinearLayoutManager layoutManager;
    EventsAdapter adapter;
    private RecyclerView rvEvents;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_main);

        mAuth = FirebaseAuth.getInstance();
        rvEvents = findViewById(R.id.rvEvents);
        swipeContainer = findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data");
                adapter.setQuery(query);
                swipeContainer.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        layoutManager = new LinearLayoutManager(this);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Initialize Firestore and the main RecyclerView
        initFirestore();
        initRecyclerView();

    }

    private void initFirestore() {
        dbFirestore = FirebaseFirestore.getInstance();
        // Get latest 20 events
        query = dbFirestore.collection("events")
                .orderBy(CREATED_AT_KEY, Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (query == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }
        adapter = new EventsAdapter(query, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    rvEvents.setVisibility(View.GONE);
                } else {
                    rvEvents.setVisibility(View.VISIBLE);
                }
            }
            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a message on errors
                Toast.makeText(OrganizerMainActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
            }
        };
        rvEvents.setLayoutManager(layoutManager);
        rvEvents.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create the menu on action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_organizer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_event:   // goes to Add Event Activity when "plus" sign is clicked
                Intent intent = new Intent(OrganizerMainActivity.this, AddEventActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.action_sign_out:
                mAuth.signOut();
                Intent i = new Intent(OrganizerMainActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onEventSelected(DocumentSnapshot event) {
        // Go to the update event page for the selected event
        Intent i = new Intent(this, ManageEventActivity.class);
        i.putExtra(ManageEventActivity.EVENT_ID, event.getId());
        Log.i(TAG, "Got event ID: " + event.getId());
        startActivity(i);
    }

}
