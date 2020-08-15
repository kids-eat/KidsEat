package com.kidseat.kidseat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kidseat.kidseat.organizer_activities.OrganizerMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

// Splash Screen is a welcome screen with the app logo

public class SplashScreenActivity extends Activity {

    private static final String TAG = "SplashScreenActivity";
    public static final String IS_ADMIN_KEY = "isAdmin";
    public FirebaseAuth mAuth;
    public FirebaseFirestore dbFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbFirestore = FirebaseFirestore.getInstance();  // connect to Firestore database
        mAuth = FirebaseAuth.getInstance();   // connect to Firebase Auth
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){   // if user is currently signed in,
            updateUI(currentUser);
        } else {
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            finish();   // finish the Splash Activity
        }

    }

    private void updateUI(FirebaseUser user) {
        // update UI for the signed in users
        dbFirestore.collection("users").document(user.getUid())
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        if(Objects.requireNonNull(documentSnapshot.getString(IS_ADMIN_KEY)).equals("true")){
                            showAdminUI();     // Show admin UI if user is an admin
                        } else {
                            showRegularUI();   // Show user UI if user is a regular user
                        }
                    } else {
                        Log.d(TAG, "Getting user document failed: ", task.getException());
                    }
                }
            }
        });

    }

    private void showAdminUI() {
        Intent intent = new Intent(SplashScreenActivity.this, OrganizerMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);    // clears the stack (disables going back with back button)
        startActivity(intent);
        finish();
    }

    private void showRegularUI() {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);    // clears the stack (disables going back with back button)
        startActivity(intent);
        finish();
    }

}
