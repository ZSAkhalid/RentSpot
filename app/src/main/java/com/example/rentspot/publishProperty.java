package com.example.rentspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.provider.Settings;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class publishProperty extends AppCompatActivity implements LocationListener {
    EditText propertyName;
    EditText propertyDiscerption;
    EditText propertyLocation;
    EditText price;
    CheckBox agreement;
    RadioButton apartmentRB;
    RadioButton cabanaRB;
    RadioButton campsRB;
    RadioButton jeddahRB;
    RadioButton ryadhRB;
    String propertyNameStr, propertyDiscerptionStr, propertyLocationStr, priceStr, typeStr, ownerNumber, city;
    Intent takeImageIntent;
    LocationManager locationManager;
    String lng = null, lat = null;


    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]+" +
            "(?:-[a-zA-Z-]+" +
            ")*$");

    FirebaseUser user;
    DatabaseReference refrence;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_property);
        propertyName = findViewById(R.id.propertyNameID);
        propertyDiscerption = findViewById(R.id.propertyDiscerptionID);
        ryadhRB = findViewById(R.id.ryadhID);
        jeddahRB = findViewById(R.id.jeddahID);
        propertyLocation = findViewById(R.id.propertyLocationID);
        price = findViewById(R.id.priceID);
        agreement = findViewById(R.id.agreementID);
        apartmentRB = findViewById(R.id.apartmentID);
        cabanaRB = findViewById(R.id.cabanID);
        campsRB = findViewById(R.id.campID);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationEnabled();
        takeImageIntent = new Intent(this, takeImage.class);
    }

    private void locationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    public void nextHandler(View view) {
        storeData();
        if (!inputIsEmpty()) {
            return;
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        refrence = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        refrence.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                User userInstance = datasnapshot.getValue(User.class);
                ownerNumber = userInstance.phoneNumber;
                takeImageIntent.putExtra("property name", propertyNameStr);
                takeImageIntent.putExtra("property discerption", propertyDiscerptionStr);
                takeImageIntent.putExtra("city", city);
                takeImageIntent.putExtra("lng", lng);
                takeImageIntent.putExtra("lat", lat);
                takeImageIntent.putExtra("property location", propertyLocationStr);
                takeImageIntent.putExtra("price", priceStr);
                takeImageIntent.putExtra("type", typeStr);
                takeImageIntent.putExtra("ownerNumber", ownerNumber);
                startActivity(takeImageIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void storeData() {
        propertyNameStr = propertyName.getText().toString();
        propertyDiscerptionStr = propertyDiscerption.getText().toString();
        propertyLocationStr = propertyLocation.getText().toString();
        priceStr = price.getText().toString();
        if (apartmentRB.isChecked()) {
            typeStr = "APARTMENT";
        } else if (cabanaRB.isChecked()) {
            typeStr = "CABANA";
        } else if (campsRB.isChecked()) {
            typeStr = "CAMPS";
        }
        if (ryadhRB.isChecked()) {
            city = "ryadh";
        }
        if (jeddahRB.isChecked()) {
            city = "jeddah";
        }
    }

    private boolean inputIsEmpty() {
        if (propertyNameStr.isEmpty()) {
            Toast.makeText(this, "Please fill the name", Toast.LENGTH_LONG).show();
            propertyName.requestFocus();
            return false;
        } else if (propertyDiscerptionStr.isEmpty()) {
            Toast.makeText(this, "Please fill the discerption", Toast.LENGTH_LONG).show();
            propertyDiscerption.requestFocus();
            return false;
        } else if (!ryadhRB.isChecked() && !jeddahRB.isChecked()) {
            Toast.makeText(this, "Please select the city", Toast.LENGTH_LONG).show();
            return false;
        } else if (propertyLocationStr.isEmpty()) {
            Toast.makeText(this, "Please fill the location", Toast.LENGTH_LONG).show();
            propertyLocation.requestFocus();
            return false;
        } else if (priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill the price", Toast.LENGTH_LONG).show();
            price.requestFocus();
            return false;
        } else if (!apartmentRB.isChecked() && !cabanaRB.isChecked() && !campsRB.isChecked()) {
            Toast.makeText(this, "Please chose type", Toast.LENGTH_LONG).show();
            return false;
        } else if (!agreement.isChecked()) {
            Toast.makeText(this, "Please check the agreement", Toast.LENGTH_LONG).show();
            return false;
        } else if (lng == null && lat == null) {
            Toast.makeText(this, "Please set the location!", Toast.LENGTH_LONG).show();
        } else {
            return validate();
        }
        return false;
    }

    public boolean validate() {
        if (!NAME_PATTERN.matcher(propertyNameStr).matches()) {
            Toast.makeText(this, "Please enter a valid property name", Toast.LENGTH_LONG).show();
            propertyName.requestFocus();
            return false;
        } else if (!NAME_PATTERN.matcher(propertyLocationStr).matches()) {
            Toast.makeText(this, "Please enter a valid location", Toast.LENGTH_LONG).show();
            propertyLocation.requestFocus();
            return false;
        } else {
            return true;
        }
    }


    public void goBackButton(View view) {
        Intent back = new Intent(this, publishOrDeletePage.class);
        startActivity(back);
    }

    public void getLocation(View view) {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            lat = String.valueOf(addresses.get(0).getLatitude());
            lng = String.valueOf(addresses.get(0).getLongitude());

        } catch (Exception e) {
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}