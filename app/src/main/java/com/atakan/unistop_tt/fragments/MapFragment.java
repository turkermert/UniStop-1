package com.atakan.unistop_tt.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.atakan.unistop_tt.R;
import com.atakan.unistop_tt.activities.ChatActivity;
import com.atakan.unistop_tt.activities.DriverProfileActivity;
import com.atakan.unistop_tt.activities.MainActivity;
import com.atakan.unistop_tt.activities.PassengerProfileActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    //private static MapFragment INSTANCE = null;

    View view;

    GoogleMap map;
    MapView mapView;

    Location mLastLocation;
    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();;
    String uid = firebaseAuth.getInstance().getCurrentUser().getUid();

    Button hitchhikeBtn;

    LatLng pickupLocation;

    LinearLayout userInfoMapAll, userInfoMapClick;
    ImageView userProfileImage;
    TextView userName, phoneNumber;
    Button sendMessageBtn;

    Boolean requestBool = false;

    Marker passengerRequestMarker;


    public MapFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); //to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mapView = view.findViewById(R.id.mapsView);
        hitchhikeBtn = view.findViewById(R.id.hitchhikeBtn);

        userInfoMapAll = view.findViewById(R.id.userInfoMapAll);
        userInfoMapClick = view.findViewById(R.id.userInfoMapClick);
        userProfileImage = view.findViewById(R.id.userProfileImage);
        userName = view.findViewById(R.id.userName);
        sendMessageBtn = view.findViewById(R.id.sendMessageBtn);

        mapView.getMapAsync(this);

        //take data from database
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users");
        Query query = referenceUser.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    String userType = "" + ds.child("usertype").getValue();
                    if (userType.equals("driver")) {
                        hitchhikeBtn.setVisibility(View.INVISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        hitchhikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //cancel request
                if (requestBool){
                    requestBool = false;
                    //geoQuery.removeAllListeners();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PassengerRequest");
                    GeoFire geoFire = new GeoFire(reference);

                    try {
                            if (!(geoFire == null)) {
                                geoFire.removeLocation(uid, new GeoFire.CompletionListener() {
                                    @Override
                                    public void onComplete(String key, DatabaseError error) {
                                        if (error != null) {
                                            System.err.println("There was an error removing the location from GeoFire: " + error);

                                        } else {
                                            System.out.println("Location removed on server successfully!");

                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    if(passengerRequestMarker != null){
                        passengerRequestMarker.remove();
                    }

                    hitchhikeBtn.setText("Hitchike");
                }

                //request
                else{
                    requestBool = true;
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PassengerRequest");
                    GeoFire geoFire = new GeoFire(reference);
                    geoFire.setLocation(uid, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                        }
                    });

                    pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    passengerRequestMarker = map.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup here"));

                    hitchhikeBtn.setText("Hitchiked...");
                }

            }
        });
        return view;
    }

    GeoQuery geoQuery;

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        MapsInitializer.initialize(getContext());
        map = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); //refresh map per 1 seconds, then it will change with 15 min
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                checkLocationPermission();
            }
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        map.setMyLocationEnabled(true);
    }

    //on location changed
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (final Location location : locationResult.getLocations()) {

                //take data from database
                DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users");
                Query query = referenceUser.orderByChild("uid").equalTo(uid);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //check until required data get
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            //get data
                            String userType = "" + ds.child("usertype").getValue();
                            mLastLocation = location;

                            if (userType.equals("driver")) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DriversLocation");
                                GeoFire geoFire = new GeoFire(reference);
                                geoFire.setLocation(uid, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                                    @Override
                                    public void onComplete(String key, DatabaseError error) {
                                    }
                                });
                                getPassengerRequestAround();
                            } else if (userType.equals("passenger")) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PassengersLocation");
                                GeoFire geoFire = new GeoFire(reference);
                                geoFire.setLocation(uid, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                                    @Override
                                    public void onComplete(String key, DatabaseError error) {
                                    }
                                });
                                getDriversAround();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    };


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Give permission")
                        .setMessage("You should give permission in order to use map")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create().show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        map.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getContext(), "Please provide the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapsView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

//    //drawing circle and show available users using GeoQuery library
//    List<Marker> markerList = new ArrayList<Marker>();
//
//    private void getDriversAround() {
//        DatabaseReference driversLocation = FirebaseDatabase.getInstance().getReference().child("DriversLocation");
//        GeoFire geoFire = new GeoFire(driversLocation);
//        geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 50);
//        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//
//            //will return all drivers locations
//            //key is uid, location is user location on db
//            @Override
//            public void onKeyEntered(final String key, GeoLocation location) {
//                for (Marker markerIt : markerList) {
//                    if (markerIt.getTag().equals(key))
//                        return;
//                }
//
//                LatLng userLocation = new LatLng(location.latitude, location.longitude);
//
//                final Marker driverMarker = map.addMarker(new MarkerOptions().position(userLocation).title(key)
//                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_car_vector)));
//
//                driverMarker.setTag(key);
//
//                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                    @Override
//                    public boolean onMarkerClick(Marker marker) {
////                        int keyUid = (int)(driverMarker.getTag());
//                        userInfoMapAll.setVisibility(View.VISIBLE);
//                        userInfoMapClick.setVisibility(View.VISIBLE);
//                        hitchhikeBtn.setVisibility(View.INVISIBLE);
//
//                        userInfoMapClick.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                //take data from database
//                                DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users");
//                                Query query = referenceUser.orderByChild("uid").equalTo(key);
//                                query.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        //check until required data get
//                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                                            //get data
//                                            String userType = "" + ds.child("usertype").getValue();
//
//                                            //check users type
//                                            if(userType.equals("passenger")){
//                                                Intent intent = new Intent(getActivity(), PassengerProfileActivity.class);
//                                                intent.putExtra("receiverUid", key);
//                                                getActivity().startActivity(intent);
//                                            }
//                                            else{
//                                                Intent intent = new Intent(getActivity(), DriverProfileActivity.class);
//                                                intent.putExtra("receiverUid", key);
//                                                getActivity().startActivity(intent);
//                                            }
//                                        }
//                                    }
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }
//                        });
//
//                        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent(getActivity(), ChatActivity.class);
//                                intent.putExtra("receiverUid", key);
//                                getActivity().startActivity(intent);
//                            }
//                        });
//
//                        //take data from database
//                        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users");
//                        Query query = referenceUser.orderByChild("uid").equalTo(key);
//                        query.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                //check until required data get
//                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                                    //get data
//                                    String name = "" + ds.child("name").getValue();
//                                    String image = "" + ds.child("image").getValue();
//                                    //Todo: phone will be added here
//
//                                    //set data
//                                    userName.setText(name);
//                                    try {
//                                        //if image is received then set
//                                        Picasso.get().load(image).into(userProfileImage);
//                                    } catch (Exception e) {
//                                        //if there is any exception while getting image then set default
//                                        Picasso.get().load(R.drawable.ic_default_img_white).into(userProfileImage);
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                            }
//                        });
//                        return false;
//                    }
//                });
//
//                markerList.add(driverMarker);
//            }
//
//            //when user stop using application marker will be deleted on the map
//            @Override
//            public void onKeyExited(String key) {
//                for (Marker markerIt : markerList) {
//                    if (markerIt.getTag().equals(key)) {
//                        markerIt.remove();
//                        markerList.remove(markerIt);
//                        return;
//                    }
//                }
//            }
//
//            //when users change locations
//            @Override
//            public void onKeyMoved(String key, GeoLocation location) {
//                for (Marker markerIt : markerList) {
//                    if (markerIt.getTag().equals(key)) {
//                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
//                    }
//                }
//            }
//
//            @Override
//            public void onGeoQueryReady() {
//
//            }
//
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//
//            }
//        });
//    }

    boolean getDriversAroundStarted = false;
    //drawing circle and show available users using GeoQuery library
    List<Marker> markerList = new ArrayList<Marker>();

    private void getDriversAround() {
        getDriversAroundStarted = true;
        DatabaseReference driversLocation = FirebaseDatabase.getInstance().getReference().child("DriversLocation");
        GeoFire geoFire = new GeoFire(driversLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 50);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            //will return all drivers locations
            //key is uid, location is user location on db
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag().equals(key))
                        return;
                }

                DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users");
                Query query1 = referenceUser.orderByChild("uid").equalTo(key);
                query1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //check until required data get
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            //get data
                            String name = "" + ds.child("name").getValue();
                            final String uid = "" + ds.child("uid").getValue();


                            LatLng userLocation = new LatLng(location.latitude, location.longitude);
                            final Marker driverMarker = map.addMarker(new MarkerOptions().position(userLocation).title(name)
                                    .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_car_vector)));

                            driverMarker.setTag(key);
                            markerList.add(driverMarker);

                        }}

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            //when user stop using application marker will be deleted on the map
            @Override
            public void onKeyExited(String key) {
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.remove();
                        markerList.remove(markerIt);
                        return;
                    }
                }
            }

            //when users change locations
            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    boolean getRequestAroundStarted = false;
    //drawing circle and show available users using GeoQuery library
    List<Marker> markerListPr = new ArrayList<Marker>();

    private void getPassengerRequestAround() {
        getRequestAroundStarted = true;
        DatabaseReference usersLocation = FirebaseDatabase.getInstance().getReference().child("PassengerRequest");
        GeoFire geoFire = new GeoFire(usersLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 50);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            //will return all users locations
            //key is uid, location is user location on db
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                for (Marker markerIt : markerListPr) {
                    if (markerIt.getTag().equals(key))
                        return;
                }

                DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users");
                Query query1 = referenceUser.orderByChild("uid").equalTo(key);
                query1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //check until required data get
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            //get data
                            String name = "" + ds.child("name").getValue();

                            LatLng userLocation = new LatLng(location.latitude, location.longitude);

                            final Marker passengerRequestMarker = map.addMarker(new MarkerOptions().position(userLocation).title(name));
                            passengerRequestMarker.setTag(key);
                            markerListPr.add(passengerRequestMarker);

                        }}

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            //when user stop using application marker will be deleted on the map
            @Override
            public void onKeyExited(String key) {
                for (Marker markerIt : markerListPr) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.remove();
                        markerListPr.remove(markerIt);
                        return;
                    }
                }
            }

            //when users change locations
            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker markerIt : markerListPr) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user signed in, stay here
            //set email of logged in user

        } else {
            //user not signed in, go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        //get item id
//        int id = item.getItemId();
//        if (id == R.id.action_logout) {
//            firebaseAuth.signOut();
//            checkUserStatus();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    //inflate options menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //set search menu item invisible
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem action_search = menu.findItem(R.id.action_search);
        MenuItem action_filter = menu.findItem(R.id.action_filter);
        MenuItem action_logout = menu.findItem(R.id.action_logout);
        action_search.setVisible(false);
        action_filter.setVisible(false);
        action_logout.setVisible(false);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

//    //when application stop, drivers and passengers location will be deleted
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users");
//        Query query = referenceUser.orderByChild("uid").equalTo(uid);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //check until required data get
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    //get data
//                    String userType = "" + ds.child("usertype").getValue();
//                    if (userType.equals("driver")) {
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DriversLocation");
//                        GeoFire geoFire = new GeoFire(reference);
//
//                        try {
//                            if (!(geoFire == null)) {
//                                geoFire.removeLocation(uid, new GeoFire.CompletionListener() {
//                                    @Override
//                                    public void onComplete(String key, DatabaseError error) {
//                                        if (error != null) {
//                                            System.err.println("There was an error removing the location from GeoFire: " + error);
//
//                                        } else {
//                                            System.out.println("Location removed on server successfully!");
//
//                                        }
//                                    }
//                                });
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    else{
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PassengersLocation");
//                        GeoFire geoFire = new GeoFire(reference);
//
//                        try {
//                            if (!(geoFire == null)) {
//                                geoFire.removeLocation(uid, new GeoFire.CompletionListener() {
//                                    @Override
//                                    public void onComplete(String key, DatabaseError error) {
//                                        if (error != null) {
//                                            System.err.println("There was an error removing the location from GeoFire: " + error);
//
//                                        } else {
//                                            System.out.println("Location removed on server successfully!");
//
//                                        }
//                                    }
//                                });
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }
}

