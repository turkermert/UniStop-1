package com.atakan.unistop_tt.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.atakan.unistop_tt.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class PassengerProfileActivity extends AppCompatActivity {

    TextView mondayDepTv, tuesdayDepTv, wednesdayDepTv, thursdayDepTv, fridayDepTv, mondayRetTv, tuesdayRetTv, wednesdayRetTv, thursdayRetTv, fridayRetTv;
    Button sendMessageBtn, rateProfileBtn;
    Context context = this;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceUserInfo;
    DatabaseReference databaseReferenceLessonHours;

    String receiverUid, senderUid;
    ImageView avatarTv;
    TextView nameTv, districtTv, addressTv, userTypeTv;

    //, @Nullable PersistableBundle persistentState
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("UniStop");

        //init
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceUserInfo = firebaseDatabase.getReference("Users");
        databaseReferenceLessonHours = firebaseDatabase.getReference("LessonHours");


        mondayDepTv = findViewById(R.id.mondayDepTv);
        tuesdayDepTv = findViewById(R.id.tuesdayDepTv);
        wednesdayDepTv = findViewById(R.id.wednesdayDepTv);
        thursdayDepTv = findViewById(R.id.thursdayDepTv);
        fridayDepTv = findViewById(R.id.fridayDepTv);
        mondayRetTv = findViewById(R.id.mondayRetTv);
        tuesdayRetTv = findViewById(R.id.tuesdayRetTv);
        wednesdayRetTv = findViewById(R.id.wednesdayRetTv);
        thursdayRetTv = findViewById(R.id.thursdayRetTv);
        fridayRetTv = findViewById(R.id.fridayRetTv);
        sendMessageBtn = findViewById(R.id.sendMessageBtn);
        avatarTv = findViewById(R.id.avatarIv);
        nameTv = findViewById(R.id.nameTv);
        districtTv = findViewById(R.id.districtTv);
        addressTv = findViewById(R.id.addressTv);
        userTypeTv = findViewById(R.id.userTypeTv);
        //rateProfileBtn = findViewById(R.id.rateProfileBtn);


        //get user id from adapter user when user click from user list
        Intent intent= getIntent();
        receiverUid = intent.getStringExtra("receiverUid");

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("receiverUid", receiverUid);
                context.startActivity(intent);
            }
        });

//        rateProfileBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Rate user");
//                view = getLayoutInflater().inflate(R.layout.dialog_rate_profile, null);
//
//                RatingBar ratingBar = view.findViewById(R.id.ratingBar);
//                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//                    @Override
//                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
//
//
//                        //Firabase database instance
//                        FirebaseDatabase database = FirebaseDatabase.getInstance();
//                        //path to store user data named "Users"
//                        DatabaseReference reference = database.getReference("Ratings");
//                        //put data within hashmap in db
//
//                     if (reference)
//                        //using hashmap
//                        HashMap<Object, Float> hashMap = new HashMap<>();
//                        //put info in hashmap
//                        hashMap.put(senderUid, v);
//
//                        reference.child(receiverUid).setValue(hashMap);
//
//                        reference.child(receiverUid).updateChildren(senderUid,v);
//                    }
//                });
//
//                builder.setView(view);
//                builder.create().show();
//            }
//        });

        //take data from database
        Query query = databaseReferenceUserInfo.orderByChild("uid").equalTo(receiverUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    String name = "" + ds.child("name").getValue();
                    String image = "" + ds.child("image").getValue();
                    String district = "" + ds.child("district").getValue();
                    String address = "" + ds.child("address").getValue();
                    String userType = "" + ds.child("usertype").getValue();

                    //set data
                    nameTv.setText(name);
                    districtTv.setText(district);
                    addressTv.setText(address);
                    userTypeTv.setText(userType);

                    try {
                        //if image is received then set
                        Picasso.get().load(image).into(avatarTv);
                    } catch (Exception e) {
                        //if there is any exception while getting image then set default
                        Picasso.get().load(R.drawable.ic_default_img_white).into(avatarTv);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query_lh= databaseReferenceLessonHours.orderByChild("uid").equalTo(receiverUid);
        query_lh.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required data get
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data

                    String mondayDep = "" + ds.child("mondayDep").getValue();
                    String tuesdayDep = "" + ds.child("tuesdayDep").getValue();
                    String wednesdayDep = "" + ds.child("wednesdayDep").getValue();
                    String thursdayDep = "" + ds.child("thursdayDep").getValue();
                    String fridayDep = "" + ds.child("fridayDep").getValue();
                    String mondayRet = "" + ds.child("mondayRet").getValue();
                    String tuesdayRet = "" + ds.child("tuesdayRet").getValue();
                    String wednesdayRet = "" + ds.child("wednesdayRet").getValue();
                    String thursdayRet = "" + ds.child("thursdayRet").getValue();
                    String fridayRet = "" + ds.child("fridayRet").getValue();

                    //set data
                    mondayDepTv.setText(mondayDep);
                    tuesdayDepTv.setText(tuesdayDep);
                    wednesdayDepTv.setText(wednesdayDep);
                    thursdayDepTv.setText(thursdayDep);
                    fridayDepTv.setText(fridayDep);
                    mondayRetTv.setText(mondayRet);
                    tuesdayRetTv.setText(tuesdayRet);
                    wednesdayRetTv.setText(wednesdayRet);
                    thursdayRetTv.setText(thursdayRet);
                    fridayRetTv.setText(fridayRet);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void checkUserStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user signed in, stay here
            //set email of logged in user
            senderUid = user.getUid(); //currently signed in user's uid

        }
        else{
            //user not signed in, go to main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //hide searcView
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }

        return super.onOptionsItemSelected(item);
    }
}

