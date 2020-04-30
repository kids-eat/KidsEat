package com.example.kidseat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kidseat.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class EventDetailActivity extends AppCompatActivity {


    private Context context;

    private static final String TAG = "EventDetailActivity";

    public static final String EVENT_ID = "event_id";
    private DocumentReference eventReference;
    private FirebaseFirestore dbFirestore;

    EventListener<DocumentSnapshot> snapshot;

    TextView tvAddress;
    TextView tvName;
    TextView tvDate;
    TextView tvTime;
    TextView tvDescription;
    ImageView ivImage;
    TextView tvMealType;

    public EventDetailActivity(Context context) {
        this.context = context;
    }

    public EventDetailActivity() {}   // Empty constructor is needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        ivImage = findViewById(R.id.ivImage);
        tvName = findViewById(R.id.tvName);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvAddress = findViewById(R.id.tvAddress);
        tvDescription = findViewById(R.id.tvDescription);
        tvMealType = findViewById(R.id.tvMealType);

        dbFirestore = FirebaseFirestore.getInstance();

        String eventId = getIntent().getExtras().getString(EVENT_ID);
        if (eventId == null) {
            throw new IllegalArgumentException(EVENT_ID);
        }

        // Get reference to the restaurant
        eventReference = dbFirestore.collection("events").document(eventId);

        eventReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null) {
                            // get the field values
                            tvName.setText(documentSnapshot.getString("name"));
                            tvDate.setText(documentSnapshot.getString("date"));
                            tvTime.setText(documentSnapshot.getString("time"));
                            tvAddress.setText(documentSnapshot.getString("address"));
                            tvDescription.setText(documentSnapshot.getString("description"));
                            tvMealType.setText((documentSnapshot.getString("meal_type")));

                            String image = documentSnapshot.getString("image");
                            if (image != null) {
                                Glide.with(ivImage.getContext()).load(image).into(ivImage);
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

    }

}

