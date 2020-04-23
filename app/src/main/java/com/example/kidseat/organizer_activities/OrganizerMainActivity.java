package com.example.kidseat.organizer_activities;

import android.app.Activity;
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

import com.example.kidseat.EventDetailActivity;
import com.example.kidseat.FirebaseUIActivity;
import com.example.kidseat.MainActivity;
import com.example.kidseat.R;
import com.example.kidseat.adapters.EventsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;


public class OrganizerMainActivity extends AppCompatActivity implements EventsAdapter.OnEventSelectedListener {

    private static final String TAG = "TimelineFragment";

    private static int LIMIT = 20;

    private FirebaseFirestore dbFirestore;
    FirebaseAuth mAuth;

    private Query query;

    LinearLayoutManager layoutManager;

    private RecyclerView rvEvents;
    private SwipeRefreshLayout swipeContainer;

    EventsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_main);

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

        mAuth = FirebaseAuth.getInstance();
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
                .orderBy("created_at", Query.Direction.DESCENDING)
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
                // Show a snackbar on errors
                Toast.makeText(OrganizerMainActivity.this, "Error: check logs for info.", Toast.LENGTH_SHORT).show();
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_organizer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_event: // goes to Add Event Activity when "plus" sign is clicked
                startActivity(new Intent(OrganizerMainActivity.this, AddEventActivity.class));
                return true;
            case R.id.action_sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(OrganizerMainActivity.this, FirebaseUIActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onEventSelected(DocumentSnapshot event) {
        // Go to the details page for the selected restaurant
        Intent i = new Intent(this, AddEventActivity.class);
        i.putExtra(AddEventActivity.EVENT_ID, event.getId()); // Todo: check event detail activity
        Log.i(TAG, "Got event ID: " + event.getId());
        startActivity(i);
    }


}
