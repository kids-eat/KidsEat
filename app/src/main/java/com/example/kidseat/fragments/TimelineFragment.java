package com.example.kidseat.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kidseat.EventDetailActivity;
import com.example.kidseat.R;
import com.example.kidseat.adapters.EventsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.functions.FirebaseFunctions;


public class TimelineFragment extends Fragment implements EventsAdapter.OnEventSelectedListener  {

    private static final String TAG = "TimelineFragment";
    private static int LIMIT = 20;
    public static final String CREATED_AT_KEY = "created_at";

    private FirebaseFirestore dbFirestore;
    private Query query;

    private LinearLayoutManager layoutManager;
    private RecyclerView rvEvents;
    private SwipeRefreshLayout swipeContainer;
    private EventsAdapter adapter;

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvEvents = view.findViewById(R.id.rvEvents);
        swipeContainer = view.findViewById(R.id.swipeContainer);

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

        layoutManager = new LinearLayoutManager(getContext());

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
                Toast.makeText(getContext(), "Error occurred", Toast.LENGTH_SHORT).show();
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
    public void onEventSelected(DocumentSnapshot event) {
        // Go to the details page for the selected event
        Intent i = new Intent(getContext(), EventDetailActivity.class);
        i.putExtra(EventDetailActivity.EVENT_ID, event.getId());
        Log.i(TAG, "Got event ID: " + event.getId());
        startActivity(i);
    }

}
