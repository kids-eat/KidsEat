package com.kidseat.kidseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.text.method.LinkMovementMethod;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";
    public static final String EVENT_ID = "event_id";

    public DocumentReference eventReference;
    public FirebaseFirestore dbFirestore;

    TextView tvAddress;
    TextView tvName;
    TextView tvDate;
    TextView tvTime;
    TextView tvDescription;
    ImageView ivImage;
    TextView tvMealType;
    TextView tvFacebookLink;
    TextView tvInstagramLink;

    public EventDetailActivity() {}   // Empty constructor is needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Access UI widgets
        ivImage = findViewById(R.id.ivImage);
        tvName = findViewById(R.id.tvName);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvAddress = findViewById(R.id.tvAddress);
        tvDescription = findViewById(R.id.tvDescription);
        tvMealType = findViewById(R.id.tvMealType);
        tvFacebookLink = findViewById(R.id.tvFacebookLink);
        tvInstagramLink = findViewById(R.id.tvInstagramLink);

        dbFirestore = FirebaseFirestore.getInstance();  // connect to Firestore database

        String eventId = getIntent().getExtras().getString(EVENT_ID);
        if (eventId == null) {
            throw new IllegalArgumentException(EVENT_ID);
        }

        eventReference = dbFirestore.collection("events").document(eventId);  // Get reference to the event

        eventReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null) {
                            // get the field values and set them to corresponding UI fields
                            tvName.setText(documentSnapshot.getString("name"));
                            setTitle(documentSnapshot.getString("name"));
                            tvDate.setText(documentSnapshot.getString("date"));
                            String time = " | " + documentSnapshot.getString("time");
                            tvTime.setText(time);
                            tvAddress.setText(documentSnapshot.getString("address"));
                            tvDescription.setText(documentSnapshot.getString("description"));
                            tvMealType.setText((documentSnapshot.getString("meal_type")));

                            String facebookLink = documentSnapshot.getString("facebook_link");
                            assert facebookLink != null;

                            String instagramLink = documentSnapshot.getString("instagram_link");
                            assert instagramLink != null;

                            if (!(facebookLink.equals(""))) {
                                String facebookPost = String.format("<a href=\"%s\">View Facebook Post</a>", facebookLink);
                                tvFacebookLink.setText(Html.fromHtml(facebookPost));
                                tvFacebookLink.setMovementMethod(LinkMovementMethod.getInstance());
                            }

                            if (!(instagramLink.equals("")) && facebookLink.equals("")) {
                                String instagramPost = String.format("<a href=\"%s\">View Instagram Post</a>", instagramLink);
                                // Show the instagram link in the position of fb link if there is no fb link
                                tvFacebookLink.setText(Html.fromHtml(instagramPost));
                                tvFacebookLink.setMovementMethod(LinkMovementMethod.getInstance());
                            }

                            if (!instagramLink.equals("") && !(facebookLink.equals(""))) {
                                String instagramPost = String.format("| " + "<a href=\"%s\">View Instagram Post</a>", instagramLink);
                                tvInstagramLink.setText(Html.fromHtml(instagramPost));
                                tvInstagramLink.setMovementMethod(LinkMovementMethod.getInstance());
                            }

                            String image = documentSnapshot.getString("image");
                            if (image != null) {
                                Glide.with(ivImage.getContext()).load(image).into(ivImage);
                            }

                        } else {
                            Log.d(TAG, "Document doesn't exist");
                        }
                } else {
                    Log.d(TAG, "Getting document failed: ", task.getException());  // if it fails to get the document, log the exception
                }
            }
        });

    }

}

