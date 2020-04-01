package com.example.kidseat.organizer_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.kidseat.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class AddEventActivity extends AppCompatActivity {
    public static final String KEY_NAME = "name";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE_URL = "image";


    public static final String TAG = "EventActivity";
    private static Context context;

    FirebaseFirestore mFirestore;

    EditText etName;
    EditText etDetails;
    EditText etDateTime;
    String placeAddress;
    String placeLocationID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        AddEventActivity.context = getApplicationContext();

        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyA-4D0lYupH0-oCaN_gqVzNYMNU82Qekxw");

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        //Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                // retrieve location address and name from the organizer
                placeAddress = place.getAddress();
                placeLocationID = place.getId();
                Log.i(TAG, "Place: " + place.getId() + ", " + place.getName() + ", " + place.getLatLng() + ", " + place.getAddress());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    public static Context getAppContext() {
        return AddEventActivity.context;
    }

    public void addEvent(View view) throws IOException {

        etName = findViewById(R.id.etName);
        etDetails = findViewById(R.id.etDetails);
        etDateTime = findViewById(R.id.etDateTime);

        String etNameText = etName.getText().toString();
        String etDetailsText = etDetails.getText().toString();
        String etDateTimeText = etDateTime.getText().toString();

        if(etNameText.isEmpty() || etDetailsText.isEmpty()){
            return;
        }

        Map<String, Object> eventToSave = new HashMap<String, Object>();

        eventToSave.put(KEY_NAME, etNameText);
        eventToSave.put(KEY_DESCRIPTION, etDetailsText);
        eventToSave.put(KEY_DATE, etDateTimeText);
        eventToSave.put(KEY_TIME, etDateTimeText);
        eventToSave.put(KEY_IMAGE_URL, etDateTimeText);
        eventToSave.put("location_id", placeLocationID);
        eventToSave.put("address", placeAddress);


        mFirestore.collection("events")
                .add(eventToSave)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Event has been saved!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Event was not saved!", e);
            }
        });

    }

}
