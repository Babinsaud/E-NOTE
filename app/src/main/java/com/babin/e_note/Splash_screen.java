package com.babin.e_note;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Splash_screen extends AppCompatActivity {

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fAuth = FirebaseAuth.getInstance();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {

                // check if user is logged in
                if (fAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    //  Anonymous account
                    fAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Splash_screen.this, "Logged in With Temporary Account.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Splash_screen.this, "Error ! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        },2000);



    }
}

