package com.atakan.unistop_tt.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.atakan.unistop_tt.R;
import com.atakan.unistop_tt.models.ModelLessonHours;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LessonhourActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner mondayDep, tuesdayDep, wednesdayDep, thursdayDep, fridayDep, mondayRet, tuesdayRet, wednesdayRet, thursdayRet, fridayRet;
    Spinner district;
    EditText address;
    Button timesNext;
    Context context = this;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, databaseReferenceUsers;

    String lesson_time_mondayDep, lesson_time_tuesdayDep, lesson_time_wednesdayDep,
            lesson_time_thursdayDep, lesson_time_fridayDep, lesson_time_mondayRet,
            lesson_time_tuesdayRet, lesson_time_wednesdayRet, lesson_time_thursdayRet,
            lesson_time_fridayRet;

    String district_string, address_string;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_hour);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("UniStop");

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("LessonHours");
        databaseReferenceUsers = firebaseDatabase.getReference("Users");



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterAddress = ArrayAdapter.createFromResource(this,
                R.array.districts, android.R.layout.simple_spinner_item);
        adapterAddress.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
        district = findViewById(R.id.district);
        address = findViewById(R.id.address);

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
        district.setAdapter(adapterAddress);

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
        district.setOnItemSelectedListener(this);

        timesNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                address_string = address.getText().toString().trim();
                HashMap<String, Object> result = new HashMap<>();
                result.put("address", address_string);
                databaseReferenceUsers.child(user.getUid()).updateChildren(result);

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
        else if(adapterView.getId() == R.id.district)
            district_string = (String) adapterView.getItemAtPosition(i);

        ModelLessonHours modelLessonHours = new ModelLessonHours(user.getUid(), lesson_time_mondayDep, lesson_time_tuesdayDep,
                lesson_time_wednesdayDep, lesson_time_thursdayDep, lesson_time_fridayDep, lesson_time_mondayRet,
                lesson_time_tuesdayRet, lesson_time_wednesdayRet, lesson_time_thursdayRet, lesson_time_fridayRet);
        databaseReference.child(user.getUid()).setValue(modelLessonHours);

        HashMap<String, Object> result = new HashMap<>();
        result.put("district", district_string);
        databaseReferenceUsers.child(user.getUid()).updateChildren(result);
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

}
