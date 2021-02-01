package com.kidseat.kidseat;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.kidseat.kidseat.organizer_activities.OrganizerMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
    // initialization of constants
    private static final String TAG = "LoginActivity";
    public static final String IS_ADMIN_KEY = "isAdmin";
    public static final String FCM_TOKEN = "fcmToken";

    // declaration of variables
    public FirebaseAuth mAuth;
    public FirebaseFirestore dbFirestore;
    public DocumentReference userInfo;

    private EditText emailField;
    private EditText passwordField;
    public ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setProgressBar(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();    // Initialize Firebase Authentication
        dbFirestore = FirebaseFirestore.getInstance();    // Initialize Cloud Firestore

        // Access UI widgets
        emailField = findViewById(R.id.fieldEmail);
        passwordField = findViewById(R.id.fieldPassword);

        // Access buttons
        Button signInButton = findViewById(R.id.emailSignInButton);
        Button createAccountButton = findViewById(R.id.emailCreateAccountButton);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(emailField.getText().toString(), passwordField.getText().toString());
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(emailField.getText().toString(), passwordField.getText().toString());
            }
        });
    }

    private void createAccount(String email, String password) {
        // Creates a new account
        if (!validateForm()) {
            return;
        }
        showProgressBar();    // show the progress before the account formation process

        // Create user with email using the Firebase Auth's method
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
                    String user_Id = user.getUid();
                    // Create a hashmap of user admin privileges to add to 'users' collection in Firestore
                    Map<String, Object> userAccessLevel = new HashMap<String, Object>();
                    userAccessLevel.put(IS_ADMIN_KEY, "false");     // By default, a new user is not an admin

                    // Add user admin privileges to 'users' collection in Firestore
                    dbFirestore.collection("users").document(user_Id).set(userAccessLevel);
                    updateUI(user);

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Account formation failed. Please try again.", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
                hideProgressBar();   // hide the progress bar after the account formation process
            }
        });
    }

    private void signIn(String email, String password) {
        // Signs in a user

        if (!validateForm()) {
            return;
        }
        showProgressBar();    // show the progress bar before the sign-in

        // Sign in with email
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
                hideProgressBar();   // hide the progress bar after signing in
            }
        });
    }

    private boolean validateForm() {
        // Makes sure that email and password are entered

        boolean valid = true;
        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");  // Require Email
            valid = false;
        } else {
            emailField.setError(null);
        }
        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");   // Require Email
            valid = false;
        } else {
            passwordField.setError(null);
        }
        return valid;
    }

    private void generateAndSaveFCMToken(final FirebaseUser user) {
        // Generates the FCM (Firebase Cloud Messaging) registration token and stores in a document associated with the user

        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();  // Get new FCM registration token

                    Map<String, Object> userToken = new HashMap<String, Object>();
                    userToken.put(FCM_TOKEN, token);
                    dbFirestore.collection("users").document(user.getUid()).update(userToken);  // Add token to Firestore
                }
            });
    }


    private void updateUI(final FirebaseUser user) {
        // Goes to next page depending on the user type

        hideProgressBar();    // hide the progress bar

        if (user != null) {
            userInfo = dbFirestore.collection("users").document(user.getUid());
            userInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null) {
                            if(Objects.requireNonNull(documentSnapshot.getString(IS_ADMIN_KEY)).equals("true")){
                                showAdminUI();    // Show admin UI if user is an admin
                            } else {
                                generateAndSaveFCMToken(user);  // generate new token for users only when signing in and creating a new account
                                showRegularUI();  // Show user UI if user is a regular user
                            }
                        } else {
                            Log.d(TAG, "Getting user document failed: ", task.getException());
                        }
                    }
                }
            });
        } else {
            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
        }
    }

    private void showAdminUI() {
        // Switches to admin (organizer) UI
        Intent intent = new Intent(LoginActivity.this, OrganizerMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // clears the stack (disables going back with back button)
        startActivity(intent);
    }

    private void showRegularUI() {
        // Switches to regular user UI
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // clears the stack (disables going back with back button)
        startActivity(intent);
    }

    public void setProgressBar(int resId) {
        progressBar = findViewById(resId);
    }

    public void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
