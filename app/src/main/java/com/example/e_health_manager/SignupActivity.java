package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText emailText;
    private EditText passwordText;
    private EditText confirmpasswordText;
    private EditText usernameText;
    private Button signupBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        emailText = findViewById(R.id.email_field_signup);
        passwordText = findViewById(R.id.password_field_signup);
        confirmpasswordText = findViewById(R.id.confirmpassword_field_signup);
        usernameText = findViewById(R.id.username_field_signup);
        signupBtn = findViewById(R.id.button_signup);
    }

    public void store_user_info(String email, String username, String uid){
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("username", username);

        Log.d("sign up activity", "new user email: "+email);
        Log.d("sign up activity", "new user username: "+username);

        mFirestore.collection("users").document(uid).set(data);

    }

    public void onClick_trySignup(View view) {

        signupBtn.setEnabled(false);
        final String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String confirm = confirmpasswordText.getText().toString();
        final String username = usernameText.getText().toString();

        //hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        if(email.isEmpty()){
            Toast toast = Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_LONG);
            toast.show();
            signupBtn.setEnabled(true);
        }
        else if(password.isEmpty()){
            Toast toast = Toast.makeText(this, "password cannot be empty", Toast.LENGTH_LONG);
            toast.show();
            signupBtn.setEnabled(true);
        }
        else if(confirm.isEmpty()){
            Toast toast = Toast.makeText(this, "please confirm password", Toast.LENGTH_LONG);
            toast.show();
            signupBtn.setEnabled(true);
        }
        else if(password.equals(confirm) == false){
            Toast toast = Toast.makeText(this, "password does not match", Toast.LENGTH_LONG);
            toast.show();
            signupBtn.setEnabled(true);
        }
        else if(username.isEmpty()){
            Toast toast = Toast.makeText(this, "username cannot be empty", Toast.LENGTH_LONG);
            toast.show();
            signupBtn.setEnabled(true);
        }
        //all inputs are valid
        else{
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //set display name
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username).build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("sign up activity", "User profile updated.");
                                                }
                                            }
                                        });
                                //Store user's info into firestore
                                store_user_info(email, username, user.getUid());
                                //redirect to the profile activity
                                signupBtn.setEnabled(true);
                                Intent intent = new Intent(SignupActivity.this, ProfileActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast toast = Toast.makeText(SignupActivity.this,
                                        "This email has already belonged to another account", Toast.LENGTH_SHORT);
                                toast.show();
                                signupBtn.setEnabled(true);
                            }
                        }
                    });
        }
    }

}
