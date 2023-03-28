package com.example.rentspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class reservProperty extends AppCompatActivity {
    private View locationLayout;
    private DatePickerDialog startDatePickerDialog, endDatePickerDialog;
    private Button startDateButton, endDateButton;
    private TextView description, location, price, type;
    private ImageView propertyImage, locationIcon, priceIcon, typeIcon, line;
    private boolean storeToStartDate;
    private String propertyID, name, priceDoubleInStr;
    private Double lng, lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserv_property);
        Intent intent = getIntent();
        propertyID = intent.getStringExtra("propertyID");
        locationLayout = findViewById(R.id.map);
        locationLayout.setVisibility(View.INVISIBLE);


        description = findViewById(R.id.descriptionID);
        location = findViewById(R.id.locationID);
        price = findViewById(R.id.priceID);
        type = findViewById(R.id.typeID);
        propertyImage = findViewById(R.id.propertyImageID);

        locationIcon = findViewById(R.id.locationIcon);
        priceIcon = findViewById(R.id.priceIcon);
        typeIcon = findViewById(R.id.typeIcon);
        line = findViewById(R.id.line);

        startDateButton = findViewById(R.id.startDatePickerButton);
        endDateButton = findViewById(R.id.endDatePickerButton);
        getPropertyInfo();

        startDateButton.setText(getTodaysDate());
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(v);
            }
        });
        endDateButton.setText(getTodaysDate());
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(v);
            }
        });

        initDatePicker();
    }

    private void getPropertyInfo() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        System.out.println("Searching for " + propertyID);
        firestore.collection("Properties").document(propertyID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            System.out.println("Found " + propertyID);
                            name = documentSnapshot.getString("name");
                            String desriptionStr = documentSnapshot.getString("discerption");
                            String city = documentSnapshot.getString("city");
                            String locationInfoStr = documentSnapshot.getString("location");
                            String locationStr = city + ", " + locationInfoStr;
                            priceDoubleInStr = documentSnapshot.getString("price");
                            String priceStr = documentSnapshot.getString("price") + " SAR";
                            String typeStr = documentSnapshot.getString("type");
                            lng = Double.valueOf(documentSnapshot.getString("lng"));
                            lat = Double.valueOf(documentSnapshot.getString("lat"));
                            System.out.println(typeStr + priceStr + locationStr + desriptionStr + desriptionStr);
                            description.setText(desriptionStr);
                            location.setText(locationStr);
                            price.setText(priceStr);
                            type.setText(typeStr);
                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    if (lat == null) {
                                        lat = 0.0;
                                    } else if (lng == null) {
                                        lng = 0.0;
                                    }
                                    System.out.println(lng + "---------------------------------------------" + lat);
                                    LatLng position = new LatLng(lat, lng);
                                    googleMap.addMarker(new MarkerOptions().position(position).title("The property"));
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                                }
                            });
                            displayImage();
                        } else {
                            Log.d("Property Info", "Document does not exist");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Property Info", "Error getting property info", e);
                    }
                });
    }

    public void displayImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference propertyImageRef = storage.getReference().child("Images/" + propertyID);
        propertyImageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap propertyImageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                propertyImage.setImageBitmap(propertyImageBitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                if (storeToStartDate) {
                    startDateButton.setText(date);
                    storeToStartDate = false;
                } else {
                    endDateButton.setText(date);
                }
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        startDatePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        endDatePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
    }

    String startDate, endDate;

    private String makeDateString(int day, int month, int year) {
        if (storeToStartDate) {
            return startDate = month + "-" + day + "-" + year;
        } else {
            return endDate = month + "-" + day + "-" + year;
        }
    }


    private void openDatePicker(View view) {
        int id = view.getId();
        if (id == R.id.startDatePickerButton) {
            storeToStartDate = true;
            startDatePickerDialog.show();
        } else if (id == R.id.endDatePickerButton) {
            storeToStartDate = false;
            endDatePickerDialog.show();
        }
    }

    boolean flag = false;

    public void moveLine(View view) {
        animateLine();
        if (!flag) {
            description.setVisibility(View.GONE);
            location.setVisibility(View.GONE);
            price.setVisibility(View.GONE);
            type.setVisibility(View.GONE);
            typeIcon.setVisibility(View.GONE);
            priceIcon.setVisibility(View.GONE);
            locationIcon.setVisibility(View.GONE);
            locationLayout.setVisibility(View.VISIBLE);
            flag = true;
            animateLine();
        } else {
            description.setVisibility(View.VISIBLE);
            location.setVisibility(View.VISIBLE);
            price.setVisibility(View.VISIBLE);
            type.setVisibility(View.VISIBLE);
            typeIcon.setVisibility(View.VISIBLE);
            priceIcon.setVisibility(View.VISIBLE);
            locationIcon.setVisibility(View.VISIBLE);
            locationLayout.setVisibility(View.INVISIBLE);
            flag = false;
            animateLine();
        }
    }

    public void animateLine() {
        if (line.getX() == 45) {
            ObjectAnimator animateByX = ObjectAnimator.ofFloat(line, "x", 745).setDuration(200);
            animateByX.start();
        } else {
            ObjectAnimator animateByX = ObjectAnimator.ofFloat(line, "x", 45).setDuration(200);
            animateByX.start();
        }
    }

    public void takeMeToGoogleMaps(View view) {
        String label = "Property";
        String uriBegin = "geo:" + lat + "," + lng;
        String query = lat + "," + lng + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(mapIntent);
    }

    public void goBackButton(View view) {
        Intent previusePage = new Intent(this, home.class);
        startActivity(previusePage);
    }

    public void reserveNow(View view) {
        startDate = startDateButton.getText().toString();
        endDate = endDateButton.getText().toString();
        if (isFirstDateBeforeSecond(startDate, endDate)) {
            Intent nextPage = new Intent(this, confirmReserve.class);
            nextPage.putExtra("image", getImageUriFromImageView().toString());
            nextPage.putExtra("name", name);
            nextPage.putExtra("description", description.getText());
            nextPage.putExtra("startDate", startDate);
            nextPage.putExtra("endDate", endDate);
            nextPage.putExtra("price", priceDoubleInStr);
            nextPage.putExtra("days", daysBetween(startDate, endDate) + "");
            startActivity(nextPage);
        } else {
            Toast.makeText(this, "Error start date must be before end date", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isFirstDateBeforeSecond(String firstDate, String secondDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm-dd-yy");
        try {
            Date first = dateFormat.parse(firstDate);
            Date second = dateFormat.parse(secondDate);
            return first.before(second);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private long daysBetween(String firstDate, String secondDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy");
        try {
            Date first = dateFormat.parse(firstDate);
            Date second = dateFormat.parse(secondDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(first);
            long firstTime = cal.getTimeInMillis();
            cal.setTime(second);
            long secondTime = cal.getTimeInMillis();
            return Math.abs(TimeUnit.DAYS.convert(secondTime - firstTime, TimeUnit.MILLISECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Uri getImageUriFromImageView() {
        propertyImage.setDrawingCacheEnabled(true);
        propertyImage.buildDrawingCache();
        Bitmap bitmap = propertyImage.getDrawingCache();
        return getBitmapUri(bitmap, this);
    }

    private Uri getBitmapUri(Bitmap bitmap, Context context) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

}