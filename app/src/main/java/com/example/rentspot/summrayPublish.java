package com.example.rentspot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;

public class summrayPublish extends AppCompatActivity {
    TextView propertyName, propertyDiscerption, propertyLocation, propertyType, propertyPrice, cityTV;
    ImageView propertyImage;
    String propertyNameStr,propertyDiscerptionStr,propertyLocationStr,priceStr,type, city;
    Uri image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summray_publish);

        propertyName = findViewById(R.id.propertyNameID);
        propertyDiscerption = findViewById(R.id.propertyDiscerptionID);
        cityTV = findViewById(R.id.cityID);
        propertyLocation = findViewById(R.id.propertyLocationID);
        propertyType = findViewById(R.id.propertyTypeID);
        propertyPrice = findViewById(R.id.propertyPriceID);
        propertyImage = findViewById(R.id.propertyImageId);

        String uriStr;
        Intent intent = getIntent();
        propertyNameStr = intent.getStringExtra("property name");
        propertyDiscerptionStr = intent.getStringExtra("property discerption");
        city = intent.getStringExtra("city");
        propertyLocationStr = intent.getStringExtra("property location");
        priceStr = intent.getStringExtra("price");
        type = intent.getStringExtra("type");
        uriStr = intent.getStringExtra("image taken");
        image = Uri.parse(uriStr);
        updateScreen();
    }

    private void updateScreen() {
        propertyName.setText(propertyNameStr);
        propertyDiscerption.setText(propertyDiscerptionStr);
        cityTV.setText(city);
        propertyLocation.setText(propertyLocationStr);
        propertyType.setText(type);
        propertyPrice.setText(priceStr);
        propertyImage.setImageURI(image);
    }

    public void doneHandler(View view) {
        Intent publishOrDelete = new Intent(this, publishOrDeletePage.class);
        startActivity(publishOrDelete);
    }
}