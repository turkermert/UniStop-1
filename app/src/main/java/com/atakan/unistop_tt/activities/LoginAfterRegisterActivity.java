package com.atakan.unistop_tt.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atakan.unistop_tt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginAfterRegisterActivity extends AppCompatActivity {

    EditText mEmailEt, mPasswordEt;
    Button mLoginBtn;
    Context context = this;
    TextView notHaveAccntTv, mRecoverPassTv;
    String receiverEmail;

    private CheckBox mCheckBoxRemember;
    private SharedPreferences mPrefs;
    private static final String PREFS_NAME = "PrefsFile";

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    FirebaseUser user;

    //progress dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mLoginBtn = findViewById(R.id.loginBtn);
        mRecoverPassTv = findViewById(R.id.recoverPassTv);
        notHaveAccntTv = findViewById(R.id.nothave_accountTv);
        mCheckBoxRemember = findViewById(R.id.checkBoxRememberMe);

        getPreferencesData();

        Intent intent= getIntent();
        receiverEmail = intent.getStringExtra("email");

        mEmailEt.setText(receiverEmail);

        //login button click
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //input data
                String email = mEmailEt.getText().toString();
                String password = mPasswordEt.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //check if email type is invalid
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                }
                else {
                    //valid email, check it from db
                    loginUser(email, password);

                    if(mCheckBoxRemember.isChecked()){
                        Boolean boolIsChecked=mCheckBoxRemember.isChecked();
                        SharedPreferences.Editor editor=mPrefs.edit();
                        editor.putString("pref_name", mEmailEt.getText().toString());
                        editor.putString("pref_pass", mPasswordEt.getText().toString());
                        editor.putBoolean("pref_check", boolIsChecked);
                        editor.apply();
                        Toast.makeText(getApplicationContext(),"Saved.",Toast.LENGTH_SHORT).show();
                    }else{
                        mPrefs.edit().clear().apply();
                    }

                }
            }
        });

        //not have an acount textview click
        notHaveAccntTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, RegisterActivity.class));
                finish();
            }
        });

        //recover pass textview click
        mRecoverPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswordDialog();
            }
        });

        //init progress dialog
        progressDialog = new ProgressDialog(context);
    }

    private void getPreferencesData() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if(sp.contains("pref_name")){
            String u = sp.getString("pref_name", "not found.");
            mEmailEt.setText(u.toString());
        }
        if (sp.contains("pref_pass")){
            String p = sp.getString("pref_pass", "not found.");
            mPasswordEt.setText(p.toString());
        }
        if(sp.contains("pref_check")){
            Boolean b = sp.getBoolean("pref_check", false);
            mCheckBoxRemember.setChecked(b);
        }
    }

    private void showRecoverPasswordDialog() {
        //Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Recover Password");

        //set layout linear layout
        LinearLayout linearLayout = new LinearLayout(context);
        //views to set in dialog
        final EditText emailEt = new EditText(context);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        //textsize min 16
        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        //buttons recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //input email
                String email = emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });
        //buttons cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dismiss dialog
                dialogInterface.dismiss();
            }
        });

        //show dialog
        builder.create().show();
    }

    private void beginRecovery(String email) {
        progressDialog.setMessage("Sending email...");
        progressDialog.show();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Email sent", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context, "Failed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                //get and show proper error message
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email, String password) {
        //show progress dialog
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //dismiss progress dialog
                            progressDialog.dismiss();

                            if (user.isEmailVerified()){
                                // Sign in success, update UI with the signed-in user's information
                                //Go to other page
                                startActivity(new Intent(context, UsertypeActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(context, "Please check your mail", Toast.LENGTH_LONG).show();
                            }


                        } else {
                            //dismiss progress dialog
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //dismiss progress dialog
                progressDialog.dismiss();
                //error, get and show error message
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go previous activity
        return super.onSupportNavigateUp();
    }
}
