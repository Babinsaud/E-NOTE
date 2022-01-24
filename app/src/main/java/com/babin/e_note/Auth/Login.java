package com.babin.e_note.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.babin.e_note.MainActivity;
import com.babin.e_note.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {
    EditText lEmail,lPassword;
    Button loginNow;
    TextView forgetPass,createAcc;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");

        lEmail = findViewById(R.id.email);
        lPassword = findViewById(R.id.lPassword);
        loginNow = findViewById(R.id.loginBtn);

        spinner = findViewById(R.id.progressBar3);

        forgetPass = findViewById(R.id.forgotPasword);
        createAcc = findViewById(R.id.createAccount);
        user = FirebaseAuth.getInstance().getCurrentUser();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        showWarning();
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText restmail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDiaglog = new AlertDialog.Builder(v.getContext());
                passwordResetDiaglog.setTitle("Reset Password");
                passwordResetDiaglog.setMessage("Enter your Email to received Reset Link");
                passwordResetDiaglog.setView(restmail);
                passwordResetDiaglog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = restmail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Login.this, "Reset link sent to your email",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error link  not sent to your email", Toast.LENGTH_SHORT).show();

                            }
                        });


                    }
                });
                passwordResetDiaglog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDiaglog.create().show();
            }
        });


        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = lEmail.getText().toString();
                String mPassword = lPassword.getText().toString();

                if(mEmail.isEmpty() || mPassword.isEmpty()){
                    Toast.makeText(Login.this, "Fields Are Required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // delete notes first

                spinner.setVisibility(View.VISIBLE);

                if(fAuth.getCurrentUser().isAnonymous()){
                    FirebaseUser user = fAuth.getCurrentUser();

                    fStore.collection("notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Login.this, "All Temp Notes are Deleted.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // delete Temp user

                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Login.this, "Temp user Deleted.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                fAuth.signInWithEmailAndPassword(mEmail,mPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Login.this, "Success !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Login Failed. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.GONE);
                    }
                });
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });
    }


    private void showWarning() {
        final AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure ?")
                .setMessage("Linking Existing Account Will delete all the temp notes. Create New Account To Save them.")
                .setPositiveButton("Save Notes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),Register.class));
                        finish();
                    }
                }).setNegativeButton("Its Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

        warning.show();
    }

}
