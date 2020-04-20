package com.example.kidseat.organizer_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kidseat.R;
import com.example.kidseat.models.Event;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class AddEventActivity extends AppCompatActivity {
    public static final String NAME_KEY = "name";
    public static final String DATE_KEY = "date";
    public static final String TIME_KEY = "time";
    public static final String ADDRESS_KEY = "address";
    public static final String MEAL_TYPE_KEY = "meal_type";
    public static final String DESCRIPTION_KEY = "description";
    public static final String IMAGE_URL_KEY = "image";
    public static final String LAT_LNG_KEY = "latlng";

    private static final int PICK_IMAGE_REQUEST = 1;

    public static final String EVENT_ID = "event_id";
    public static final String TAG = "AddEventActivity";
    private static Context context;

    FirebaseFirestore mFirestore;
    StorageReference mStorageRef;

    EditText etName;
    EditText etDate;
    EditText etTime;
    String placeAddress;
    EditText etMealType;
    EditText etDetails;
    Button btnChooseImage;
    Button btnSave;
    String placeLocationID;
    Uri imageDownloadUrl;   // stores the download url of image from the firebase storage
    LatLng latLng;
    ProgressBar progBar;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        AddEventActivity.context = getApplicationContext();

        etName = findViewById(R.id.etName);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etMealType = findViewById(R.id.etMealType);
        etDetails = findViewById(R.id.etDetails);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);
        progBar = findViewById(R.id.progBar);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mFirestore = FirebaseFirestore.getInstance();

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addEvent();
//            }
//        });

        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyAQJ9uNb7Yf1hnYiUg8eLjgtUdchw59QVM");

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
                latLng = place.getLatLng();

                Log.i(TAG, "Place: " + place.getId() + ", " + place.getName() + ", " + place.getLatLng() + ", " + place.getAddress());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        // method to upload files to Firebase Storage
        if(imageUri != null){
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progBar.setProgress(0);
                                }
                            }, 5000);
                            progBar.setProgress(0);
                            // get and save the download url of the image
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageDownloadUrl = uri;
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddEventActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    // Show the progress of the upload
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
//            imageUri = data.getData();
//            Toast.makeText(AddEventActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void initFirestore() {
//        mFirestore = FirebaseFirestore.getInstance();
//    }

    public static Context getAppContext() {
        return AddEventActivity.context;
    }


    public void addEvent(View view) {
        Log.i(TAG, "before uploadfile executed");
//        uploadFile();  // uploads image to storage
        Log.i(TAG, "uploadfile executed");
        String etNameText = etName.getText().toString();
        String etDateText = etDate.getText().toString();
        String etTimeText = etTime.getText().toString();
        String etMealTypeText = etMealType.getText().toString();
        String etDetailsText = etDetails.getText().toString();
        String imageUrl = imageDownloadUrl.toString();

        if(etNameText.isEmpty() || etDetailsText.isEmpty()){
            return;
        }

        Map<String, Object> eventToSave = new HashMap<String, Object>();

        eventToSave.put(NAME_KEY, etNameText);
        eventToSave.put(DATE_KEY, etDateText);
        eventToSave.put(TIME_KEY, etTimeText);
//        eventToSave.put(ADDRESS_KEY, placeAddress);
        eventToSave.put(MEAL_TYPE_KEY, etMealTypeText);
        eventToSave.put(DESCRIPTION_KEY, etDetailsText);
//        eventToSave.put(IMAGE_URL_KEY, imageUrl);
//        eventToSave.put(LAT_LNG_KEY, latLng);
//        eventToSave.put("location_id", placeLocationID);


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
