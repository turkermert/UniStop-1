package com.atakan.unistop_tt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LessonhourActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner mondayDep, tuesdayDep, wednesdayDep, thursdayDep, fridayDep, mondayRet, tuesdayRet, wednesdayRet, thursdayRet, fridayRet;
    Button timesNext;
    Context context = this;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String lesson_time_mondayDep, lesson_time_tuesdayDep, lesson_time_wednesdayDep,
            lesson_time_thursdayDep, lesson_time_fridayDep, lesson_time_mondayRet,
            lesson_time_tuesdayRet, lesson_time_wednesdayRet, lesson_time_thursdayRet,
            lesson_time_fridayRet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_hour);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("LessonHours");


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mondayDep = findViewById(R.id.mondayDep);
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

        mondayDep.setAdapter(adapter);
        tuesdayDep.setAdapter(adapter);
        wednesdayDep.setAdapter(adapter);
        thursdayDep.setAdapter(adapter);
        fridayDep.setAdapter(adapter);
        mondayRet.setAdapter(adapter);
        tuesdayRet.setAdapter(adapter);
        wednesdayRet.setAdapter(adapter);
        thursdayRet.setAdapter(adapter);
        fridayRet.setAdapter(adapter);

        mondayDep.setOnItemSelectedListener(this);
        tuesdayDep.setOnItemSelectedListener(this);
        wednesdayDep.setOnItemSelectedListener(this);
        thursdayDep.setOnItemSelectedListener(this);
        fridayDep.setOnItemSelectedListener(this);
        mondayRet.setOnItemSelectedListener(this);
        tuesdayRet.setOnItemSelectedListener(this);
        wednesdayRet.setOnItemSelectedListener(this);
        thursdayRet.setOnItemSelectedListener(this);
        fridayRet.setOnItemSelectedListener(this);

        timesNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DashboardActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (adapterView.getId() == R.id.mondayDep)
            lesson_time_mondayDep = (String) adapterView.getItemAtPosition(i);
        else if(adapterView.getId() == R.id.tuesdayDep)
            lesson_time_tuesdayDep = (String) adapterView.getItemAtPosition(i);
        else if(adapterView.getId() == R.id.wednesdayDep)
            lesson_time_wednesdayDep = (String) adapterView.getItemAtPosition(i);
        else if(adapterView.getId() == R.id.thursdayDep)
            lesson_time_thursdayDep = (String) adapterView.getItemAtPosition(i);
        else if(adapterView.getId() == R.id.fridayDep)
            lesson_time_fridayDep = (String) adapterView.getItemAtPosition(i);
        else if(adapterView.getId() == R.id.mondayRet)
            lesson_time_mondayRet = (String) adapterView.getItemAtPosition(i);
        else if(adapterView.getId() == R.id.tuesdayRet)
            lesson_time_tuesdayRet = (String) adapterView.getItemAtPosition(i);
        else if(adapterView.getId() == R.id.wednesdayRet)
            lesson_time_wednesdayRet = (String) adapterView.getItemAtPosition(i);
        else if(adapterView.getId() == R.id.thursdayRet)
            lesson_time_thursdayRet = (String) adapterView.getItemAtPosition(i);
        else if(adapterView.getId() == R.id.fridayRet)
            lesson_time_fridayRet = (String) adapterView.getItemAtPosition(i);


        LessonHours lessonHours = new LessonHours(user.getUid(), lesson_time_mondayDep, lesson_time_tuesdayDep,
                lesson_time_wednesdayDep, lesson_time_thursdayDep, lesson_time_fridayDep, lesson_time_mondayRet,
                lesson_time_tuesdayRet, lesson_time_wednesdayRet, lesson_time_thursdayRet, lesson_time_fridayRet);
        databaseReference.child(user.getUid()).setValue(lessonHours);
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

}
