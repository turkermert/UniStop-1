package com.atakan.unistop_tt.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atakan.unistop_tt.R;
import com.atakan.unistop_tt.activities.ChatActivity;
import com.atakan.unistop_tt.activities.DriverProfileActivity;
import com.atakan.unistop_tt.activities.PassengerProfileActivity;
import com.atakan.unistop_tt.models.ModelUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    List<ModelUser> userList;

    //for getting current user's uid
    FirebaseAuth firebaseAuth;
    String myUid;

    //constructor
    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;

        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout (row_user.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int position) {
        //get data
        final String receiverUid = userList.get(position).getUid();
        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getName();
        final String userType = userList.get(position).getUsertype();
        String userDistrict = userList.get(position).getDistrict();

//        //set usertype's first letter capital
//        String userTypeCapital;
//        userTypeCapital = userType.substring(0, 1).toUpperCase() + userType.substring(1).toLowerCase();

        //set data
        myHolder.mNameTv.setText(userName);
        myHolder.mUsertypeTv.setText(userType);
        myHolder.mDistrictTv.setText(userDistrict);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_default_img)
                    .into(myHolder.mAvatarIv);
        }catch (Exception e){

        }

        myHolder.blockIv.setImageResource(R.drawable.ic_unblocked_green);
        //check each user if is blocked or not
        checkIsBlocked(receiverUid, myHolder, position);

        //handle item click
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //click user from user list to show user's profile

                //check users type
                if(userType.equals("passenger")){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    ref.child(receiverUid).child("BlockedUsers").orderByChild("uid").equalTo(myUid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds:dataSnapshot.getChildren()){
                                if(ds.exists()){
                                    Toast.makeText(context, "You're blocked by that user, can't show that profile", Toast.LENGTH_SHORT).show();
                                    //blocked, dont proceed further
                                    return;
                                }
                            }
                            //don't block, start activity
                            Intent intent = new Intent(context, PassengerProfileActivity.class);
                            intent.putExtra("receiverUid", receiverUid);
                            context.startActivity(intent);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                else{
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    ref.child(receiverUid).child("BlockedUsers").orderByChild("uid").equalTo(myUid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds:dataSnapshot.getChildren()){
                                if(ds.exists()){
                                    Toast.makeText(context, "You're blocked by that user, can't send message", Toast.LENGTH_SHORT).show();
                                    //blocked, dont proceed further
                                    return;
                                }
                            }
                            //don't block, start activity
                            Intent intent = new Intent(context, DriverProfileActivity.class);
                            intent.putExtra("receiverUid", receiverUid);
                            context.startActivity(intent);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

            }
        });
        //click to block unblock user
        myHolder.blockIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userList.get(position).isBlocked()){
                    unBlockUser(receiverUid);
                }
                else{
                    blockUser(receiverUid);
                }

            }
        });
    }

    private void checkIsBlocked(String receiverUid, final MyHolder myHolder, final int position) {
        //check each user if is blocked or not
        //if uid of the user exists in "BlockedUsers" then that user is blocked, otherwise not
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUsers").orderByChild("uid").equalTo(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.exists()){
                        myHolder.blockIv.setImageResource(R.drawable.ic_blocked_red);
                        userList.get(position).setBlocked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void blockUser(String receiverUid) {
        //block the user, by adding uid to current user's "BlockedUsers" node
        //put values in hashmap to put in db

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", receiverUid);

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUsers").child(receiverUid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //blocked successfully
                Toast.makeText(context, "Blocked Successfully...", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed block
                Toast.makeText(context, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void unBlockUser(String receiverUid) {
        //unblock the user, by removing uid to current user's "BlockedUsers" node
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUsers").orderByChild("uid").equalTo(receiverUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    if(ds.exists()){
                        ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //unblock successfully
                                Toast.makeText(context, "Unblocked Successfully...", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //failed to unblock
                                Toast.makeText(context, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv, blockIv;
        TextView mNameTv, mUsertypeTv, mDistrictTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            mAvatarIv = itemView.findViewById(R.id.avatarIv);
            blockIv = itemView.findViewById(R.id.blockIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mUsertypeTv = itemView.findViewById(R.id.usertypeTv);
            mDistrictTv = itemView.findViewById(R.id.districtTv);
        }
    }
}

