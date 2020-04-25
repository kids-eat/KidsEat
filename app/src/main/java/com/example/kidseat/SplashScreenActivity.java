package com.example.kidseat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.kidseat.organizer_activities.OrganizerMainActivity;
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
    public FirebaseAuth mAuth;
    public FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            updateUI(currentUser);

        } else {
            startActivity(new Intent(SplashScreenActivity.this, FirebaseUIActivity.class));
            finish();
        }

    }

    private void updateUI(FirebaseUser user) {

        mFirestore.collection("users").document(user.getUid())
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        if(Objects.requireNonNull(documentSnapshot.getString("isAdmin")).equals("true")){
                            showAdminUI();
                        }
                        else {
                            showRegularUI();
                        }
                    }
                    else {
                        Log.d(TAG, "Getting document failed: ", task.getException());
                    }
                }
            }
        });

    }

    private void showAdminUI() {
        Intent intent = new Intent(SplashScreenActivity.this, OrganizerMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showRegularUI() {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
