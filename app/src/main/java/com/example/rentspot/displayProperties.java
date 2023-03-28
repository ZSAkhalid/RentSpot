package com.example.rentspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class displayProperties extends AppCompatActivity {
    LinearLayout parentLayout;
    String city, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_properties);
        parentLayout = findViewById(R.id.linearLayout);
        Intent info = getIntent();
        city = info.getStringExtra("city");
        type = info.getStringExtra("type");
        List<Property> properties = new ArrayList<>();
        loadAllProperties();
    }

    private List<Property> allProperties = new ArrayList<>();

    private void loadAllProperties() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        db.collection("Properties")
                .whereEqualTo("city", city)
                .whereEqualTo("type", type)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> propertyList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot propertySnapshot : propertyList) {
                            String name = propertySnapshot.getString("name");
                            String description = propertySnapshot.getString("discerption");
                            String ownerNumber = propertySnapshot.getString("ownerNumber");
                            String price = propertySnapshot.getString("price");
                            String city = propertySnapshot.getString("city");
                            String location = propertySnapshot.getString("location");
                            String propertyID = propertySnapshot.getId();
                            Property property = new Property(name, description, ownerNumber, price,
                                    city, location, propertyID);

                            StorageReference propertyImageRef = storage.getReference().child("Images/" + propertyID);
                            propertyImageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap propertyImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    property.propertyImage = propertyImage;
                                    allProperties.add(property);
                                    if (propertyList.indexOf(propertySnapshot) == propertyList.size() - 1) {
                                        generateLinearLayouts();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    property.propertyImage = BitmapFactory.decodeResource(getResources(), R.drawable.warning_sign);
                                    allProperties.add(property);
                                    if (propertyList.indexOf(propertySnapshot) == propertyList.size() - 1) {
                                        generateLinearLayouts();
                                    }
                                }
                            });

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }


    private void generateLinearLayouts() {
        for (Property property : allProperties) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    1250, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackgroundResource(R.drawable.rounded_corner);
            linearLayout.setPadding(25, 15, 25, 10);
            linearLayout.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.setMargins(0, 100, 0, 0);
            linearLayout.setLayoutParams(layoutParams);

            //Property image
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(1200, 800));
            imageView.setImageBitmap(property.propertyImage);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundResource(R.drawable.rounded_corner_image);
            //circular display of the image
            CardView cardView = new CardView(this);
            cardView.setLayoutParams(new LinearLayout.LayoutParams(1200, 800));
            cardView.setRadius(30);
            cardView.setCardElevation(6);
            cardView.addView(imageView);

            //discreption of the property
            TextView descriptionTextView = new TextView(this);
            descriptionTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 80);
            descriptionTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            descriptionTextView.setText(property.discerption);
            descriptionTextView.setTextColor(getResources().getColor(R.color.black));

            //Property name && price
            LinearLayout firstLinearLayout = new LinearLayout(this);
            firstLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            firstLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            //Property name
            TextView propertyNameTextView = new TextView(this);
            propertyNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 60);
            RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            propertyNameTextView.setLayoutParams(textViewParams);
            propertyNameTextView.setText(property.name);
            propertyNameTextView.setTextColor(Color.parseColor("#909090"));
            propertyNameTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            //price
            TextView priceTextView = new TextView(this);
            priceTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 60);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 0, 0, 0);
            priceTextView.setLayoutParams(params);
            priceTextView.setText(property.price + " SAR");
            priceTextView.setTextColor(Color.parseColor("#AE70B3"));
            priceTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

            firstLinearLayout.addView(propertyNameTextView);
            firstLinearLayout.addView(priceTextView);

            //location && owner number
            LinearLayout secondLinearLayout = new LinearLayout(this);
            secondLinearLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            ));

            //location
            TextView locationTextView = new TextView(this);
            locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 60);
            locationTextView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            ));
            locationTextView.setText(property.city + ", " + property.location);
            locationTextView.setTextColor(Color.parseColor("#909090"));
            locationTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            locationTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            //owner number
            TextView ownerNumberTextView = new TextView(this);
            ownerNumberTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 60);
            ownerNumberTextView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            ));
            ownerNumberTextView.setText("0" + property.ownerNumber);
            ownerNumberTextView.setTextColor(Color.parseColor("#AE70B3"));
            locationTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            ownerNumberTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

            secondLinearLayout.addView(locationTextView);
            secondLinearLayout.addView(ownerNumberTextView);

            Button button = new Button(this);
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setText("Resirve");
            button.setTextColor(Color.parseColor("#FFFFFF"));
            button.setBackgroundColor(Color.parseColor("#AE70B3"));

            button.setBackgroundResource(R.drawable.rounded_button);


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToReserve(property.propertyID);
                }
            });

            linearLayout.addView(cardView);
            linearLayout.addView(descriptionTextView);
            linearLayout.addView(firstLinearLayout);
            linearLayout.addView(secondLinearLayout);
            linearLayout.addView(button);

            //Add linearLayout to the parent view
            parentLayout.addView(linearLayout);
        }
    }

    public void moveToReserve(String propertyID){
        Intent intent = new Intent(this, reservProperty.class);
        intent.putExtra("propertyID", propertyID);
        startActivity(intent);
    }

    public void goBackButton(View view) {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }
}