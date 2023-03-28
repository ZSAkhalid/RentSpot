package com.example.rentspot;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class Property {
    public String name, discerption, location, ownerNumber, type,
            ownerUID, locationOnGoogleMap, price, city;
    public Bitmap propertyImage;
    public Uri imageUri;
    String ownerId, propertyId, propertyID;


    //This constructer is for displayProperties page
    public Property(String name, String discerption, String ownerNumber, String price,
                         String city, String location, String propertyID) {
        this.name = name;
        this.discerption = discerption;
        this.ownerNumber = ownerNumber;
        this.price = price;
        this.city = city;
        this.location = location;
        this.propertyID = propertyID;
    }


    public Property(String ownerId, String propertyId, String name, String location) {
        this.ownerId = ownerId;
        this.propertyId = propertyId;
        this.name = name;
        this.location = location;
    }


    public Property(String ownerUID, String name, String discerption, String location, String ownerNumber,
                    String type, String price, Bitmap propertyImage, String city) {
        this.ownerUID = ownerUID;
        this.name = name;
        this.discerption = discerption;
        this.location = location;
        this.ownerNumber = ownerNumber;
        this.type = type;
        this.price = price;
        this.propertyImage = propertyImage;
        this.city = city;
        try {
            convertImage();
        } catch (Exception e) {

        }
    }


    private void convertImage() throws IOException {
        File imageFile = new File(getExternalStoragePublicDirectory(DIRECTORY_PICTURES), "propertyImage.jpg");
        FileOutputStream outputStream = new FileOutputStream(imageFile);
        propertyImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();
        imageUri = Uri.fromFile(imageFile);
    }
}
