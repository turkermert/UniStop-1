package com.atakan.unistop_tt.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Spinner;

import com.atakan.unistop_tt.R;
import com.atakan.unistop_tt.activities.MainActivity;
import com.atakan.unistop_tt.adapters.AdapterUsers;
import com.atakan.unistop_tt.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;
    String district = "All";

    //firebase auth
    FirebaseAuth firebaseAuth;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        //init
        firebaseAuth = FirebaseAuth.getInstance();

        //init
        recyclerView = view.findViewById(R.id.users_recyclerView);
        //set it's properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));




        //init user list
        userList = new ArrayList<>();

        //getAll users
        getAllUser();

        return  view;
    }

    private void getAllUser() {
        //get current user
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing users info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //GET ALL DATA from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    //get all users except currently signed in user
                    if(!modelUser.getUid().equals(fUser.getUid())){
                        userList.add(modelUser);
                    }

                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(), userList);
                    //set adapter to recyler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchUsers(final String query) {
        //get current user
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing users info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //GET ALL DATA from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data from db into modelUser one by one
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    //get all searched users except currently signed in user
                    if(!modelUser.getUid().equals(fUser.getUid())){

                        if(modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getDistrict().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);
                        }
                    }

                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(), userList);
                    //refresh adapter
                    adapterUsers.notifyDataSetChanged();
                    //set adapter to recyler view
                    recyclerView.setAdapter(adapterUsers);
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

        }
        else{
            //user not signed in, go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); //to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    //inflate options menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main, menu);

        //SearchView
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        MenuItem filter = menu.findItem(R.id.action_filter);

        filter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                showFilterUsersDialog();
                return true;
            }
        });

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when user press search button from keyboard

                //if search query is not empty then search
                if (!TextUtils.isEmpty(s.trim())){
                    //search text contains text, search it
                    searchUsers(s);
                }
                else{
                    //search text empty, get all users
                    getAllUser();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called whenever user press any single letter

                //if search query is not empty then search
                if (!TextUtils.isEmpty(s.trim())){
                    //search text contains text, search it
                    searchUsers(s);
                }
                else{
                    //search text empty, get all users
                    getAllUser();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showFilterUsersDialog() {
        //custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Filter Users");
        View view = getLayoutInflater().inflate(R.layout.dialog_filter_userlist, null);

        //add checkboxes
        final CheckBox driverCB = view.findViewById(R.id.driverCB);
        final CheckBox passengerCB = view.findViewById(R.id.passengerCB);

        //add spinner
        final Spinner spinner = view.findViewById(R.id.districtFilter);
        ArrayAdapter<CharSequence> adapterAddress = ArrayAdapter.createFromResource(getActivity(),
                R.array.districtsFilter, android.R.layout.simple_spinner_item);
        adapterAddress.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterAddress);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getId() == R.id.districtFilter)
                    district = (String) adapterView.getItemAtPosition(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setPositiveButton("Filter ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (driverCB.isChecked() && passengerCB.isChecked() == false){
                    getDrivers(district);
                }
                else if (passengerCB.isChecked() && driverCB.isChecked() == false){
                    getPassengers(district);
                }
                else{
                    getAllUserWithSelectedDistrict(district);
                }
            }
        });
        //add button in dialog to cancel
        builder.setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setView(view);
        builder.create().show();
    }

    private void getAllUserWithSelectedDistrict(final String district) {
        //get current user
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing users info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //GET ALL DATA from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    //get all users except currently signed in user and all user with all district
                    if(!modelUser.getUid().equals(fUser.getUid()) && district.equals("All")){
                        userList.add(modelUser);
                    }
                    else if (!modelUser.getUid().equals(fUser.getUid()) && modelUser.getDistrict().equals(district)){
                        userList.add(modelUser);
                    }

                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(), userList);
                    //set adapter to recyler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPassengers(final String district) {
        //get current user
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing users info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //GET ALL DATA from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    //get all users except currently signed in user and passengers with all district
                    if(!modelUser.getUid().equals(fUser.getUid()) && !modelUser.getUsertype().equals("driver") && district.equals("All")){
                        userList.add(modelUser);
                    }

                    else if (!modelUser.getUid().equals(fUser.getUid()) && !modelUser.getUsertype().equals("driver") && modelUser.getDistrict().equals(district)){
                        userList.add(modelUser);
                    }

                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(), userList);
                    //set adapter to recyler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDrivers(final String district) {
        //get current user
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing users info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //GET ALL DATA from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);


                    //get all users except currently signed in user and passengers with all districts
                    if(!modelUser.getUid().equals(fUser.getUid()) && !modelUser.getUsertype().equals("passenger") && district.equals("All")){
                        userList.add(modelUser);
                    }
                    else if (!modelUser.getUid().equals(fUser.getUid()) && !modelUser.getUsertype().equals("passenger") && modelUser.getDistrict().equals(district)){
                        userList.add(modelUser);
                    }

                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(), userList);
                    //set adapter to recyler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //handle menu item click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
