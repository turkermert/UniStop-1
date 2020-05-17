package com.atakan.unistop_tt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CarInformationActivity extends AppCompatActivity {

    EditText colorInput, modelInput, plateInput, licenseDateInput;
    Button nextButtonCD;
    Context context = this;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String colorInputString, modelInputString, plateInputString, licenseDateInputString, uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_information);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("UniStop");

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("CarInformation");

        colorInput = findViewById(R.id.colorInput);
        modelInput = findViewById(R.id.modelInput);
        plateInput = findViewById(R.id.plateInput);
        licenseDateInput = findViewById(R.id.dateInput);
        nextButtonCD = findViewById(R.id.nextButtonCD);

        nextButtonCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorInputString = colorInput.getText().toString().trim();
                modelInputString = modelInput.getText().toString().trim();
                plateInputString = plateInput.getText().toString().trim();
                licenseDateInputString = licenseDateInput.getText().toString().trim();
                uid = user.getUid();


                HashMap<String,Object> result = new HashMap<>();
                result.put("uid", uid);
                result.put("color", colorInputString);
                result.put("model", modelInputString);
                result.put("plate", plateInputString);
                result.put("licensedate", licenseDateInputString);
                databaseReference.child(user.getUid()).updateChildren(result);

                startActivity(new Intent(context, LessonhourActivity.class));
                finish();
            }
        });
    }
}
