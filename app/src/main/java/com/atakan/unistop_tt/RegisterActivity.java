package com.atakan.unistop_tt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText mEmailEt, mPasswordEt, mPasswordAgain;
    Button mRegisterBtn;
    Context context = this;
    TextView mHaveAccountTv;


    //progressbar to display while registering user
    ProgressDialog progressDialog;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mHaveAccountTv = findViewById(R.id.have_accountTv);
        mPasswordAgain = findViewById(R.id.re_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Registering User...");

        //handle register btn click
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //input email, password
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();
                String passwordAgain = mPasswordAgain.getText().toString().trim();
                //validate
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //give error if its not an email
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                }
                else if(password.length() < 8 ){
                    //give error if password length is lower than 8
                    mPasswordEt.setError("Password length at least 8 characters");
                    mPasswordEt.setFocusable(true);
                }
                else if(!passwordAgain.equals(password)){
                    mPasswordAgain.setError("these passwords are not same.");
                }

                //atılım email check condition
                /*
                else if(!TextUtils.split(email,"@")[1].equals("student.atilim.edu.tr")){

                    Toast.makeText(getApplicationContext(), "Sadece Atılım e-mail geçerlidir.", Toast.LENGTH_LONG).show();
                    return;
                }*/

                else{
                    //register the user, write it db with registerUser method
                    registerUser(email, password);
                }

            }
        });
        //handle login textview click listener
        mHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, LoginActivity.class));
                finish();
            }
        });

    }

    private void registerUser(String email, String password){
        //write user to db
        //first show progress dialog then start registering user
        progressDialog.show();

        //taken from tools > firebase > authentication > 4 sign up new users
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, dismiss dialog and start register activity
                            progressDialog.dismiss();

                            FirebaseUser user = mAuth.getCurrentUser();
                            //get user email and uid from auth
                            String email = user.getEmail();
                            String uid = user.getUid();

                            /*
                            //send email authontication
                            sendEmail();*/

                            //using hashmap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //put info in hashmap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", ""); //will add later
                            hashMap.put("phone", "");
                            hashMap.put("image", "");
                            hashMap.put("usertype", "");
                            hashMap.put("district", "");
                            hashMap.put("address", "");

                            //Firabase database instance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //path to store user data named "Users"
                            DatabaseReference reference = database.getReference("Users");
                            //put data within hashmap in db
                            reference.child(uid).setValue(hashMap);


                            Toast.makeText(context, "Registered...\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(context, UsertypeActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //dismiss progress dialog and get and show error message
                progressDialog.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    /*
    //send email authontication method
    private void sendEmail(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(context,"check email for verification",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }}


     */

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go previous activity
        return super.onSupportNavigateUp();
    }
}
