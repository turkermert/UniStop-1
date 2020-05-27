package com.atakan.unistop_tt.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atakan.unistop_tt.R;
import com.atakan.unistop_tt.activities.MainActivity;
import com.atakan.unistop_tt.models.ModelLessonHours;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, databaseReferenceLessonHours, databaseReferenceCarInformation;
    //storage
    StorageReference storageReference;
    //path where images of user profile pic will be stored
    String storagePath = "Users_Profile_Imgs/";
    String uid;
    String district, address;
    String lesson_time_mondayDep, lesson_time_tuesdayDep, lesson_time_wednesdayDep,
            lesson_time_thursdayDep, lesson_time_fridayDep, lesson_time_mondayRet,
            lesson_time_tuesdayRet, lesson_time_wednesdayRet, lesson_time_thursdayRet,
            lesson_time_fridayRet;
    String colorString, modelString, plateString, licenseDateString;


    ImageView avatarTv;
    TextView nameTv, districtTv, addressTv, userTypeTv;
    TextView mondayDepTv, tuesdayDepTv, wednesdayDepTv, thursdayDepTv, fridayDepTv, mondayRetTv, tuesdayRetTv, wednesdayRetTv, thursdayRetTv, fridayRetTv;
    TextView carBrandTv, carPlateTv, carColorTv, driverLicenseTv;
    Button fab;
    RelativeLayout carInformation;

    ProgressDialog progressDialog;

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    //arrays of permissions to be requested
    String cameraPermissions[];
    String storagePermissions[];

    //uri of picked image
    Uri image_uri;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //init firabase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        databaseReferenceLessonHours = firebaseDatabase.getReference("LessonHours");
        databaseReferenceCarInformation = firebaseDatabase.getReference("CarInformation");
        storageReference = getInstance().getReference(); //firebase storage reference
        uid = user.getUid();

        //init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        avatarTv = view.findViewById(R.id.avatarIv);
        nameTv = view.findViewById(R.id.nameTv);
        districtTv = view.findViewById(R.id.districtTv);
        addressTv = view.findViewById(R.id.addressTv);
        userTypeTv = view.findViewById(R.id.userTypeTv);
        fab = view.findViewById(R.id.fab);
        carInformation = view.findViewById(R.id.carInformation);
        carBrandTv = view.findViewById(R.id.carBrandTv);
        carPlateTv = view.findViewById(R.id.carPlateTv);
        carColorTv = view.findViewById(R.id.carColorTv);
        driverLicenseTv = view.findViewById(R.id.driverLicenseTv);

        mondayDepTv = view.findViewById(R.id.mondayDepTv);
        tuesdayDepTv = view.findViewById(R.id.tuesdayDepTv);
        wednesdayDepTv = view.findViewById(R.id.wednesdayDepTv);
        thursdayDepTv = view.findViewById(R.id.thursdayDepTv);
        fridayDepTv = view.findViewById(R.id.fridayDepTv);
        mondayRetTv = view.findViewById(R.id.mondayRetTv);
        tuesdayRetTv = view.findViewById(R.id.tuesdayRetTv);
        wednesdayRetTv = view.findViewById(R.id.wednesdayRetTv);
        thursdayRetTv = view.findViewById(R.id.thursdayRetTv);
        fridayRetTv = view.findViewById(R.id.fridayRetTv);

        //init progress dialog
        progressDialog = new ProgressDialog(getActivity());

        //take data from database
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    String name = "" + ds.child("name").getValue();
                    String district = "" + ds.child("district").getValue();
                    String address = "" + ds.child("address").getValue();
                    String userType = "" + ds.child("usertype").getValue();
                    String image = "" + ds.child("image").getValue();

                    //set data
                    nameTv.setText(name);
                    districtTv.setText(district);
                    addressTv.setText(address);
                    userTypeTv.setText(userType);
                    /*String realName=TextUtils.split(email,"@")[0].split("\\.")[1];       //wite name and surname automatically
                    String realSurname=TextUtils.split(email,"@")[0].split("\\.")[0];
                    emailTv.setText(email+"\n"+realName.substring(0, 1).toUpperCase()+ realName.substring(1)+" "+realSurname.substring(0, 1).toUpperCase()+ realSurname.substring(1));*/

                    try {
                        //if image is received then set
                        Picasso.get().load(image).into(avatarTv);
                    } catch (Exception e) {
                        //if there is any exception while getting image then set default
                        Picasso.get().load(R.drawable.ic_add_image).into(avatarTv);
                    }

                    if (userType.equals("passenger")) {
                        carInformation.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query_lh = databaseReferenceLessonHours.orderByChild("uid").equalTo(user.getUid());
        query_lh.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
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

        Query query_ci = databaseReferenceCarInformation.orderByChild("uid").equalTo(uid);
        query_ci.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    String car_color = "" + ds.child("color").getValue();
                    String car_model = "" + ds.child("model").getValue();
                    String car_plate = "" + ds.child("plate").getValue();
                    String driver_license_date = "" + ds.child("licensedate").getValue();

                    //set data
                    carPlateTv.setText(car_plate);
                    carBrandTv.setText(car_model);
                    carColorTv.setText(car_color);
                    driverLicenseTv.setText(driver_license_date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userTypeTv.getText().toString().trim().equals("passenger")){
                    showEditProfileDialog();
                }
                else if (userTypeTv.getText().toString().trim().equals("driver")){
                    showEditDriverProfileDialog();
                }
            }
        });

        return view;
    }


    private boolean checkStoragePermissions() {
        //check storage permission, if enabled return true
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        //request runtime storage permission
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {
        //check storage permission, if enabled return true
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        //request runtime storage permission
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void showEditProfileDialog() {
        //options to show in dialog
        String options[] = {"Edit Profile Picture", "Edit Name", "Edit Address", "Edit Schedule"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //handle dialog item clicks
                if (i == 0) {
                    //Edit Profile photo clicked
                    progressDialog.setMessage("Updating Profile Picture");
                    showImagePicDialog();
                } else if (i == 1) {
                    //Edit name clicked
                    progressDialog.setMessage("Updating Name");
                    //calling method and pass key "name" as parameter to update it's value in database
                    showNameUpdateDialog();
                } else if (i == 2) {
                    //Edit address clicked
                    progressDialog.setMessage("Updating Address");
                    showAddressUpdateDialog();
                } else if (i == 3) {
                    //Edit schedule clicked
                    progressDialog.setMessage("Updating Schedule");
                    showScheduleUpdateDialog();
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void showEditDriverProfileDialog() {
        //options to show in dialog
        String options[] = {"Edit Profile Picture", "Edit Name", "Edit Address", "Edit Schedule", "Edit Car Details"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //handle dialog item clicks
                if (i == 0) {
                    //Edit Profile photo clicked
                    progressDialog.setMessage("Updating Profile Picture");
                    showImagePicDialog();
                } else if (i == 1) {
                    //Edit name clicked
                    progressDialog.setMessage("Updating Name");
                    //calling method and pass key "name" as parameter to update it's value in database
                    showNameUpdateDialog();
                } else if (i == 2) {
                    //Edit address clicked
                    progressDialog.setMessage("Updating Address");
                    showAddressUpdateDialog();
                } else if (i == 3) {
                    //Edit schedule clicked
                    progressDialog.setMessage("Updating Schedule");
                    showScheduleUpdateDialog();
                }
                else if (i == 4) {
                    //Edit schedule clicked
                    progressDialog.setMessage("Updating Driver Details");
                    showCarDetailsUpdateDialog();
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void showCarDetailsUpdateDialog() {
        //custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update car information");
        View view = getLayoutInflater().inflate(R.layout.dialog_update_car, null);

        //add edittexts
        final EditText colorInput = view.findViewById(R.id.colorInput);
        final EditText modelInput = view.findViewById(R.id.modelInput);
        final EditText plateInput = view.findViewById(R.id.plateInput);
        final EditText licenseDateInput = view.findViewById(R.id.dateInput);

        Query query_ci = databaseReferenceCarInformation.orderByChild("uid").equalTo(uid);
        query_ci.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    String car_color = "" + ds.child("color").getValue();
                    String car_model = "" + ds.child("model").getValue();
                    String car_plate = "" + ds.child("plate").getValue();
                    String driver_license_date = "" + ds.child("licensedate").getValue();

                    //set data
                    plateInput.setText(car_plate);
                    modelInput.setText(car_model);
                    colorInput.setText(car_color);
                    licenseDateInput.setText(driver_license_date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //add button in dialog to update
        builder.setPositiveButton("Update ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                colorString = colorInput.getText().toString().trim();
                modelString = modelInput.getText().toString().trim();
                plateString = plateInput.getText().toString().trim();
                licenseDateString = licenseDateInput.getText().toString().trim();

                HashMap<String, Object> result = new HashMap<>();
                result.put("color", colorString);
                result.put("licensedate", licenseDateString);
                result.put("model", modelString);
                result.put("plate", plateString);
                databaseReferenceCarInformation.child(user.getUid()).updateChildren(result);
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

    private void showScheduleUpdateDialog() {
        //custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update schedule");
        View view = getLayoutInflater().inflate(R.layout.dialog_update_schedule, null);

        ArrayAdapter<CharSequence> adapterSchedule = ArrayAdapter.createFromResource(getActivity(),
                R.array.times, android.R.layout.simple_spinner_item);
        adapterSchedule.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //add spinners
        final Spinner mondayDep = view.findViewById(R.id.mondayDep);
        final Spinner tuesdayDep = view.findViewById(R.id.tuesdayDep);
        final Spinner wednesdayDep = view.findViewById(R.id.wednesdayDep);
        final Spinner thursdayDep = view.findViewById(R.id.thursdayDep);
        final Spinner fridayDep = view.findViewById(R.id.fridayDep);
        final Spinner mondayRet = view.findViewById(R.id.mondayRet);
        final Spinner tuesdayRet = view.findViewById(R.id.tuesdayRet);
        final Spinner wednesdayRet = view.findViewById(R.id.wednesdayRet);
        final Spinner thursdayRet = view.findViewById(R.id.thursdayRet);
        final Spinner fridayRet = view.findViewById(R.id.fridayRet);

        mondayDep.setAdapter(adapterSchedule);
        tuesdayDep.setAdapter(adapterSchedule);
        wednesdayDep.setAdapter(adapterSchedule);
        thursdayDep.setAdapter(adapterSchedule);
        fridayDep.setAdapter(adapterSchedule);
        mondayRet.setAdapter(adapterSchedule);
        tuesdayRet.setAdapter(adapterSchedule);
        wednesdayRet.setAdapter(adapterSchedule);
        thursdayRet.setAdapter(adapterSchedule);
        fridayRet.setAdapter(adapterSchedule);

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

        builder.setPositiveButton("Update ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.setView(view);
        builder.create().show();
    }

    private void showAddressUpdateDialog() {
        //custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update address");
        View view = getLayoutInflater().inflate(R.layout.dialog_update_address, null);

        //add spinner
        final Spinner spinner = view.findViewById(R.id.districtUpdate);
        ArrayAdapter<CharSequence> adapterAddress = ArrayAdapter.createFromResource(getActivity(),
                R.array.districts, android.R.layout.simple_spinner_item);
        adapterAddress.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterAddress);

        //add edittext
        final EditText editText = view.findViewById(R.id.addressUpdate);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getId() == R.id.districtUpdate)
                    district = (String) adapterView.getItemAtPosition(i);

                HashMap<String, Object> result = new HashMap<>();
                result.put("district", district);
                databaseReference.child(user.getUid()).updateChildren(result);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //add button in dialog to update
        builder.setPositiveButton("Update ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                address = editText.getText().toString().trim();
                HashMap<String, Object> result = new HashMap<>();
                result.put("address", address);
                databaseReference.child(user.getUid()).updateChildren(result);
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

    private void showNameUpdateDialog() {
        //custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update name");
        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);
        //add edit text
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter name");
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //add button in dialog to update
        builder.setPositiveButton("Update ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //input text from edit text
                String value = editText.getText().toString().trim();
                //validate id user enter something
                if (!TextUtils.isEmpty(value)) {
                    //update database name field
                    progressDialog.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("name", value);

                    databaseReference.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //updated message
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Updated...", Toast.LENGTH_SHORT).show();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //failed, show error message
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Please enter name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //add button in dialog to cancel
        builder.setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void showImagePicDialog() {
        //show dialog containing options camera and gallery to pick the image
        String options[] = {"Camera", "Gallery"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //handle dialog item clicks
                if (i == 0) {
                    //Camera clicked
                    if (!checkCameraPermissions()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }

                } else if (i == 1) {
                    //Gallery clicked
                    if (!checkStoragePermissions()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //when user press allow or deny permission request dialog
        //handle permission cases
        //open camera, open gallery

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                //picking from camera, check if camera and storage permissions allowed
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        //permissions enabled
                        pickFromCamera();
                    } else {
                        //permissions denied
                        Toast.makeText(getActivity(), "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                //picking from gallery, check if storage permissions allowed
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        //permissions enabled
                        pickFromGallery();
                    } else {
                        //permissions denied
                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //will be called after picking image from camera or gallery
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //image is picked from gallery, get uri of image
                image_uri = data.getData();

                uploadProfilePhoto(image_uri);

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //image is picked from camera, get uri of image

                uploadProfilePhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePhoto(Uri uri) {
        //show progress
        progressDialog.show();

        //path and mane of image to be stored in firabase storage
        String filePathAndName = storagePath + "_" + user.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image is uploaded to storage, now get it's url and storage in user's db
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();

                        //check if image is uploaded or not and url is received
                        if (uriTask.isSuccessful()) {
                            //image uploaded
                            //add/update url in user's db
                            //second parameter contains the url of the image stored in fb storage
                            HashMap<String, Object> results = new HashMap<>();
                            results.put("image", downloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //url in db od user is added succesfully
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), "Image Updated...", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //error adding url in db of user
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Error Updating Image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            //error
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //there were some errors, show error message, dismiss pd
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void pickFromCamera() {
        //intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); //to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    //inflate options menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //handle menu item click

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
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

        ModelLessonHours modelLessonHours = new ModelLessonHours(user.getUid(), lesson_time_mondayDep, lesson_time_tuesdayDep,
                lesson_time_wednesdayDep, lesson_time_thursdayDep, lesson_time_fridayDep, lesson_time_mondayRet,
                lesson_time_tuesdayRet, lesson_time_wednesdayRet, lesson_time_thursdayRet, lesson_time_fridayRet);
        databaseReferenceLessonHours.child(user.getUid()).setValue(modelLessonHours);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
