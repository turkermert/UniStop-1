package com.atakan.unistop_tt.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atakan.unistop_tt.R;
import com.atakan.unistop_tt.activities.DriverProfileActivity;
import com.atakan.unistop_tt.activities.PassengerProfileActivity;
import com.atakan.unistop_tt.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    List<ModelUser> userList;

    //constructor
    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout (row_user.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {
        //get data
        final String receiverUid = userList.get(position).getUid();
        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getName();
        final String userType = userList.get(position).getUsertype();
        String userDistrict = userList.get(position).getDistrict();

        //set usertype's first letter capital
        String userTypeCapital;
        userTypeCapital = userType.substring(0, 1).toUpperCase() + userType.substring(1).toLowerCase();

        //set data
        myHolder.mNameTv.setText(userName);
        myHolder.mUsertypeTv.setText(userTypeCapital);
        myHolder.mDistrictTv.setText(userDistrict);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_default_img)
                    .into(myHolder.mAvatarIv);
        }catch (Exception e){

        }
        //handle item click
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //click user from user list to show user's profile

                //check users type
                if(userType.equals("passenger")){
                    Intent intent = new Intent(context, PassengerProfileActivity.class);
                    intent.putExtra("receiverUid", receiverUid);
                    context.startActivity(intent);
                }
                else{
                    Intent intent = new Intent(context, DriverProfileActivity.class);
                    intent.putExtra("receiverUid", receiverUid);
                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv;
        TextView mNameTv, mUsertypeTv, mDistrictTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            mAvatarIv = itemView.findViewById(R.id.avatarIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mUsertypeTv = itemView.findViewById(R.id.usertypeTv);
            mDistrictTv = itemView.findViewById(R.id.districtTv);
        }
    }
}
