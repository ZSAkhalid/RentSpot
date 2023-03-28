package com.example.rentspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.List;


public class deleteProperty extends AppCompatActivity {
    ImageView image1;
    TextView name1;
    TextView location1;

    ImageView image2;
    TextView name2;
    TextView location2;

    ImageView image3;
    TextView name3;
    TextView location3;

    FirebaseUser user;
    DatabaseReference reference;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_property);

        image1 = findViewById(R.id.image1);
        name1 = findViewById(R.id.name1);
        location1 = findViewById(R.id.location1);

        image2 = findViewById(R.id.image2);
        name2 = findViewById(R.id.name2);
        location2 = findViewById(R.id.location2);

        image3 = findViewById(R.id.image3);
        name3 = findViewById(R.id.name3);
        location3 = findViewById(R.id.location3);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        searchPropertyByOwner(userID);
    }

    List<Property> properties = new ArrayList<>();

    public void searchPropertyByOwner(String ownerId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Properties")
                .whereEqualTo("owner", ownerId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> propertyList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot propertySnapshot : propertyList) {
                            String propertyId = propertySnapshot.getId();
                            String name = propertySnapshot.getString("name");
                            String location = propertySnapshot.getString("location");
                            Property property = new Property(ownerId, propertyId, name, location);
                            properties.add(property);
                        }
                        displayProperties();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error
                    }
                });
    }

    public void displayProperties() {

        for (Property property : properties) {
            if (name1.getText().toString().equalsIgnoreCase("........")) {
                name1.setText(property.name);
                location1.setText(property.location);
                displayImage(property.propertyId, image1);
            } else if (name2.getText().toString().equalsIgnoreCase("........")) {
                name2.setText(property.name);
                location2.setText(property.location);
                displayImage(property.propertyId, image2);
            } else if (name3.getText().toString().equalsIgnoreCase("........")) {
                name3.setText(property.name);
                location3.setText(property.location);
                displayImage(property.propertyId, image3);
            }
        }
        if (name1.getText().toString().equalsIgnoreCase("........")) {
            RelativeLayout Relative1 = findViewById(R.id.Relative1);
            ViewGroup parentView = (ViewGroup) Relative1.getParent();
            parentView.removeView(Relative1);
        }
        if (name2.getText().toString().equalsIgnoreCase("........")) {
            RelativeLayout Relative2 = findViewById(R.id.Relative2);
            ViewGroup parentView = (ViewGroup) Relative2.getParent();
            parentView.removeView(Relative2);
        }
        if (name3.getText().toString().equalsIgnoreCase("........")) {
            RelativeLayout Relative3 = findViewById(R.id.Relative3);
            ViewGroup parentView = (ViewGroup) Relative3.getParent();
            parentView.removeView(Relative3);
        }
    }

    public void displayImage(String imageId, ImageView image) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("Images/" + imageId);
        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                image.setImageURI(uri);
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Error
            }
        });
    }

    public void deletePropertyFromDatabase(String propertyId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Properties")
                .document(propertyId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Property successfully deleted!
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error
                    }
                });
    }

    public void deleteImageFromDatabase(String imageId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("Images/" + imageId);
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // image successfully deleted
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Error
            }
        });
    }


    public void goBackButton(View view) {
        Intent intent = new Intent(this, publishOrDeletePage.class);
        startActivity(intent);
    }

    public void deleteProperty1(View view) {
        Property temp;
        temp = properties.get(0);
        deletePropertyFromDatabase(temp.propertyId);
        deleteImageFromDatabase(temp.propertyId);
        RelativeLayout Relative1 = findViewById(R.id.Relative1);
        ViewGroup parentView = (ViewGroup) Relative1.getParent();
        parentView.removeView(Relative1);
    }

    public void deleteProperty2(View view) {
        Property temp;
        temp = properties.get(1);
        deletePropertyFromDatabase(temp.propertyId);
        deleteImageFromDatabase(temp.propertyId);
        RelativeLayout Relative2 = findViewById(R.id.Relative2);
        ViewGroup parentView = (ViewGroup) Relative2.getParent();
        parentView.removeView(Relative2);
    }

    public void deleteProperty3(View view) {
        Property temp;
        temp = properties.get(2);
        deletePropertyFromDatabase(temp.propertyId);
        deleteImageFromDatabase(temp.propertyId);
        RelativeLayout Relative3 = findViewById(R.id.Relative3);
        ViewGroup parentView = (ViewGroup) Relative3.getParent();
        parentView.removeView(Relative3);
    }
}