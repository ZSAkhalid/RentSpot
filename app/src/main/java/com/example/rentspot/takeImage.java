package com.example.rentspot;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class takeImage extends AppCompatActivity {
    FirebaseUser user;
    DatabaseReference reference;
    String userID;
    Property newProperty;
    Bitmap imageTaken;
    FirebaseStorage storage;
    StorageReference storageRef;
    String id;
    private Button btnCapture;
    String propertyNameStr, propertyDiscerptionStr, propertyLocationStr, priceStr, type, ownerNumber, city;
    private static final int Image_Capture_Code = 1;
    boolean imageHasBeenTaken = false;
    String lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_image);
        Intent intent = getIntent();
        try {
            propertyNameStr = intent.getStringExtra("property name");
            lng = intent.getStringExtra("lng");
            lat = intent.getStringExtra("lat");
            propertyDiscerptionStr = intent.getStringExtra("property discerption");
            city = intent.getStringExtra("city");
            propertyLocationStr = intent.getStringExtra("property location");
            priceStr = intent.getStringExtra("price");
            type = intent.getStringExtra("type");
            ownerNumber = intent.getStringExtra("ownerNumber");
            btnCapture = findViewById(R.id.capture_button);
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt, Image_Capture_Code);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                imageTaken = (Bitmap) data.getExtras().get("data");
                imageHasBeenTaken = true;
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void nextHandler(View view) {
        if (!imageHasBeenTaken) {
            Toast.makeText(this, "Please take image", Toast.LENGTH_LONG).show();
            return;
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        newProperty = new Property(userID, propertyNameStr, propertyDiscerptionStr,
                propertyLocationStr, ownerNumber, type, priceStr, imageTaken, city);

        Intent nextPage = new Intent(this, summrayPublish.class);
        nextPage.putExtra("property name", propertyNameStr);
        nextPage.putExtra("property discerption", propertyDiscerptionStr);
        nextPage.putExtra("city", city);
        nextPage.putExtra("property location", propertyLocationStr);
        nextPage.putExtra("price", priceStr);
        nextPage.putExtra("type", newProperty.type);
        nextPage.putExtra("image taken", newProperty.imageUri.toString());


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> property = new HashMap<>();
        property.put("owner", userID);
        property.put("name", newProperty.name);
        property.put("discerption", newProperty.discerption);
        property.put("city", city);
        property.put("lng", lng);
        property.put("lat", lat);
        property.put("location", newProperty.location);
        property.put("price", newProperty.price);
        property.put("type", newProperty.type);
        property.put("ownerNumber", newProperty.ownerNumber);


        db.collection("Properties")
                .add(property)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        id = documentReference.getId();
                        uploadImage(id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("NOT DONE!");
                    }
                });
        startActivity(nextPage);
    }


    private void uploadImage(String id) {
        StorageReference riversRf = storageRef.child("Images/" + id);
        riversRf.putFile(newProperty.imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }

    public void goBackButton(View view) {
        Intent back = new Intent(this, publishProperty.class);
        startActivity(back);
    }
}