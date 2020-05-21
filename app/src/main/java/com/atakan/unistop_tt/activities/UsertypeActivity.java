package com.atakan.unistop_tt.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.atakan.unistop_tt.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class UsertypeActivity extends AppCompatActivity {

    Button driverBtn, passengerBtn;
    Context context = this;

    //firebase
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String usertype;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usertype);

        driverBtn = findViewById(R.id.driverBtn);
        passengerBtn = findViewById(R.id.passengerBtn);

        //init firabase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        //change usertype as driver on db
        driverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usertype= "driver";
                HashMap<String, Object> result = new HashMap<>();
                result.put("usertype", usertype);

                databaseReference.child(user.getUid()).updateChildren(result);

                startActivity(new Intent(context, CarInformationActivity.class));
                finish();
            }
        });

        //change usertype as passenger on db
        passengerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usertype= "passenger";
                HashMap<String, Object> result = new HashMap<>();
                result.put("usertype", usertype);

                databaseReference.child(user.getUid()).updateChildren(result);

                startActivity(new Intent(context, LessonhourActivity.class));
                finish();
            }
        });


    }

}
