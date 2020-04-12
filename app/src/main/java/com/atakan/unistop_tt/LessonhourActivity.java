package com.atakan.unistop_tt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LessonhourActivity extends AppCompatActivity /*implements View.OnClickListener*/ {

    Spinner mondayDep, tuesdayDep, wednesdayDep, thursdayDep, fridayDep, mondayRet, tuesdayRet, wednesdayRet, thursdayRet, fridayRet;
    Button timesNext;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextView textdeneme;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_hour);

        mondayDep = findViewById(R.id.mondayDep);
        final String mondayDepS = mondayDep.getSelectedItem().toString();

        textdeneme = findViewById(R.id.textdeneme);

        tuesdayDep = findViewById(R.id.tuesdayDep);
        wednesdayDep = findViewById(R.id.wednesdayDep);
        thursdayDep = findViewById(R.id.thursdayDep);
        fridayDep = findViewById(R.id.fridayDep);
        mondayRet = findViewById(R.id.mondayRet);
        tuesdayRet = findViewById(R.id.tuesdayRet);
        wednesdayRet = findViewById(R.id.wednesdayRet);
        thursdayRet = findViewById(R.id.thursdayRet);
        fridayRet = findViewById(R.id.fridayRet);
        timesNext = findViewById(R.id.times_next);

        timesNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textdeneme.setText(mondayDepS);
            }
        });

    }

    /*@Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mondayDep:{
                fridayRet.
                    enterLessonHours()
                user = firebaseAuth.getCurrentUser();
                //get user email and uid from auth
                String uid = user.getUid();
                //using hashmap
                HashMap<Object, String> hashMap = new HashMap<>();
                //put info in hashmap
                hashMap.put("uid", uid);
                hashMap.put("name", ""); //will add later
                hashMap.put("phone", "");
                hashMap.put("image", "");
                hashMap.put("usertype", "");
                //Firabase database instance
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                //path to store user data named "Users"
                DatabaseReference reference = database.getReference("Users");
                //put data within hashmap in db
                reference.child(uid).setValue(hashMap);
            }

        }
    }*/
}
