package com.kidseat.kidseat.organizer_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kidseat.kidseat.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ManageEventActivity extends AppCompatActivity implements RemoveEventDialog.RemoveEventDialogListener {

    // Event document fields
    public static final String NAME_KEY = "name";
    public static final String DATE_KEY = "date";
    public static final String RAW_DATE_KEY = "raw_date";
    public static final String TIME_KEY = "time";
    public static final String ADDRESS_KEY = "address";
    public static final String MEAL_TYPE_KEY = "meal_type";
    public static final String DESCRIPTION_KEY = "description";
    public static final String IMAGE_URL_KEY = "image";
    public static final String LAT_LNG_KEY = "latlng";
    public static final String LOCATION_ID_KEY = "location_id";
    public static final String FACEBOOK_LINK_KEY = "facebook_link";
    public static final String INSTAGRAM_LINK_KEY= "instagram_link";

    private static final int PICK_IMAGE_REQUEST = 1;
    public static final String EVENT_ID = "event_id";
    public static final String TAG = "ManageEventActivity";

    FirebaseFirestore dbFirestore;
    DocumentReference eventReference;
    FirebaseStorage mStorageRef;
    public FirebaseAuth mAuth;

    EditText etName;
    EditText etDate;
    EditText etTime;
    TextView tvAddress;
    String placeAddress;
    EditText etMealType;
    EditText etDetails;
    EditText etFacebookLink;
    EditText etInstagramLink;
    String stringUri;  // download Uri of the image in the Cloud Storage
    Button btnChooseImage;
    ImageView ivImage;
    Button btnSave;
    Button btnRemove;
    String placeLocationID;
    LatLng latLng;
    String rawDate;        // stores the date of the event in "MM/DD/YYYY" format
    ProgressBar progBar;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    Boolean isPlaceUpdated = false;

    String eventId;
    private Uri imageUri;   // Uri of the local image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etName = findViewById(R.id.etName);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        tvAddress = findViewById(R.id.tvAddress);
        etMealType = findViewById(R.id.etMealType);
        etDetails = findViewById(R.id.etDetails);
        etFacebookLink = findViewById(R.id.etFacebookLink);
        etInstagramLink = findViewById(R.id.etInstagramLink);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        ivImage = findViewById(R.id.ivImage);
        btnRemove = findViewById(R.id.btnRemove);
        btnSave = findViewById(R.id.btnSave);
        progBar = findViewById(R.id.progBar);
        final String currentAddressText = "Current Address: ";

        dbFirestore = FirebaseFirestore.getInstance();    // Initialize Cloud Firestore instance
        mStorageRef = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();              // Initialize Firebase Auth Instance

        // Retrieve the ID of the document being referenced
        eventId = getIntent().getExtras().getString(EVENT_ID);
        if (eventId == null) { throw new IllegalArgumentException(EVENT_ID);}

        // Get reference to the event
        eventReference = dbFirestore.collection("events").document(eventId);

        eventReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        // get the field values
                        etName.setText(documentSnapshot.getString(NAME_KEY));
                        setTitle(documentSnapshot.getString(NAME_KEY));
                        etDate.setText(documentSnapshot.getString(DATE_KEY));
                        etTime.setText(documentSnapshot.getString(TIME_KEY));
                        placeAddress = documentSnapshot.getString(ADDRESS_KEY);
                        tvAddress.setText(currentAddressText + placeAddress);
                        etMealType.setText(documentSnapshot.getString(MEAL_TYPE_KEY));
                        etDetails.setText(documentSnapshot.getString(DESCRIPTION_KEY));
                        etFacebookLink.setText(documentSnapshot.getString(FACEBOOK_LINK_KEY));
                        etInstagramLink.setText(documentSnapshot.getString(INSTAGRAM_LINK_KEY));
                        rawDate = documentSnapshot.getString(RAW_DATE_KEY);
                        stringUri = documentSnapshot.getString(IMAGE_URL_KEY);
                        if (stringUri != null) {
                            Glide.with(ivImage.getContext()).load(stringUri).into(ivImage);
                        }

                        Log.i(TAG, "Name: " + documentSnapshot.getString("address"));
                    } else {
                        Log.d(TAG, "Document doesn't exist");
                    }
                } else {
                    Log.d(TAG, "Getting document failed: ", task.getException());
                }
            }
        });

        // Pick Date by Clicking on the EditText
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date datePickerDialog dialog
                datePickerDialog = new DatePickerDialog(ManageEventActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // stores the date of the event in "MM/DD/YYYY" format
                            rawDate = (monthOfYear+1) + "/" + dayOfMonth + "/" + year;
                            // format the date to "MMM DD, YYYY" format
                            String[] monthsArray = new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                            String formattedDate = monthsArray[monthOfYear] + " " + dayOfMonth + ", " + year;
                            etDate.setText(formattedDate);
                        }
                    }, year, month, day);
                datePickerDialog.show();
            }
        });
        // Pick Time
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                timePickerDialog = new TimePickerDialog(ManageEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                // Convert time from 24-hour format to 12-hour format
                                String time = sHour + ":" + sMinute;
                                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
                                Date date = null;
                                try {
                                    date = fmt.parse(time);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");
                                String formattedTime = fmtOut.format(date);
                                etTime.setText(formattedTime);
                            }
                        }, hour, minutes, false);
                timePickerDialog.show();
            }
        });

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new RemoveEventDialog();
                dialog.show(getSupportFragmentManager(), "remove_event");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calls uploadFile() which calls getStorageFileDownloadUri() which calls updateEvent()
                uploadFile();
            }
        });

        // Initialize Places.
        String secretValue = getString(R.string.google_maps_api_key);
        Log.i(TAG, "Secret Val " + secretValue);
        Places.initialize(getApplicationContext(), secretValue);

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

                // retrieve location address and name from the organizer
                placeAddress = place.getName() +", " + place.getAddress();
                placeLocationID = place.getId();
                latLng = place.getLatLng();
                tvAddress.setText(currentAddressText + placeAddress);
                isPlaceUpdated = true;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the RemoveEventDialog.RemoveEventDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        removeEvent();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button: Do Nothing
    }

    private void openFileChooser() {
        // opens local device to choose an image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            Toast.makeText(ManageEventActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
            Glide.with(this).load(imageUri).into(ivImage);   // Displays the newly selected image
        }
    }

    private void uploadFile() {
        // method to upload files to Firebase Storage
        progBar.setVisibility(ProgressBar.VISIBLE);
        if(imageUri != null){
            final StorageReference fileReference = mStorageRef.getReference("uploads").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            final UploadTask uploadTask;
            uploadTask = fileReference.putFile(imageUri);
            uploadTask
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // get and save the download url of the image
                        getStorageFileDownloadUri(uploadTask, fileReference);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ManageEventActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                });
        } else {
            // Notify user that no image is selected and directly call updateEvent()
            updateEvent();
        }

    }

    private String getFileExtension(Uri uri){
        // returns the extension of the Uri object
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void getStorageFileDownloadUri(UploadTask uploadTask, final StorageReference fileReference){
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    stringUri = Objects.requireNonNull(task.getResult()).toString();
                }
                updateEvent();
            }
        });
    }

    private void removeEvent() {
        // remove the event doc from the database
        eventReference.delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    Toast.makeText(ManageEventActivity.this, "Event Removed Successfully", Toast.LENGTH_SHORT).show();
                    removeFile();    // removes the image associated with the event from Cloud Storage
                    redirectMainPage();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error deleting document", e);
                }
            });
    }

    /**
     * remove the image associated with the event from Cloud Storage
     */
    private void removeFile() {

        // Create a reference to the file to delete
        StorageReference imageRef = mStorageRef.getReferenceFromUrl(stringUri);

        // Delete the file
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

    private void updateEvent() {
        // pushes all the updates to the database
        String etNameText = etName.getText().toString();
        String etDateText = etDate.getText().toString();
        String etTimeText = etTime.getText().toString();
        String etMealTypeText = etMealType.getText().toString();
        String etDetailsText = etDetails.getText().toString();
        String etFacebookLinkText = etFacebookLink.getText().toString();
        String etInstagramLinkText = etInstagramLink.getText().toString();

        placeAddress = (placeAddress == null) ? "" : placeAddress;
        stringUri = (stringUri == null) ? "" : stringUri;

        Map<String, Object> eventToSave = new HashMap<String, Object>();

        if(isPlaceUpdated){
            eventToSave.put(ADDRESS_KEY, placeAddress);
            eventToSave.put(LAT_LNG_KEY, latLng);
            eventToSave.put(LOCATION_ID_KEY, placeLocationID);
        }

        eventToSave.put(NAME_KEY, etNameText);
        eventToSave.put(DATE_KEY, etDateText);
        eventToSave.put(TIME_KEY, etTimeText);
        eventToSave.put(MEAL_TYPE_KEY, etMealTypeText);
        eventToSave.put(DESCRIPTION_KEY, etDetailsText);
        eventToSave.put(FACEBOOK_LINK_KEY, etFacebookLinkText);
        eventToSave.put(INSTAGRAM_LINK_KEY, etInstagramLinkText);
        eventToSave.put(IMAGE_URL_KEY, stringUri);
        eventToSave.put(RAW_DATE_KEY, rawDate);

        eventReference.update(eventToSave)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ManageEventActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                    redirectMainPage();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error updating document", e);
                }
            });

        progBar.setVisibility(ProgressBar.INVISIBLE);    // hide the progress bar after the event creation process
    }

    private void redirectMainPage(){
        // Switches to admin Main Activity
        Intent intent = new Intent(ManageEventActivity.this, OrganizerMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // clears the stack (disables going back with back button)
        startActivity(intent);
    }
}
