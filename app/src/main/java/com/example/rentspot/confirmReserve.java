package com.example.rentspot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class confirmReserve extends AppCompatActivity {
    TextView description, name, checkIn, checkOut, total, vat, summary;
    ImageView image;
    String numOfDays, price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reserve);
        Intent intent = getIntent();
        description = findViewById(R.id.descriptionID);
        name = findViewById(R.id.nameID);
        checkIn = findViewById(R.id.checkInID);
        checkOut = findViewById(R.id.checkOutID);
        total = findViewById(R.id.totatID);
        vat = findViewById(R.id.vatID);
        image = findViewById(R.id.imageID);
        summary= findViewById(R.id.summary);

        image.setImageURI(Uri.parse(intent.getStringExtra("image")));
        name.setText(intent.getStringExtra("name"));
        description.setText(intent.getStringExtra("description"));
        checkIn.setText("Check in: "+intent.getStringExtra("startDate"));
        checkOut.setText("Check out: "+intent.getStringExtra("endDate"));
        numOfDays = intent.getStringExtra("days");
        price = intent.getStringExtra("price");

        displayInfo();

    }

    private void displayInfo() {
        String totalStr = String.valueOf((Double.valueOf(numOfDays) * Double.valueOf(price)));
        String vatStr = String.valueOf(Double.valueOf(totalStr)*0.15);
        String totatlWithVat = String.valueOf(Double.valueOf(vatStr) + Double.valueOf(totalStr));
        System.out.println(totalStr+ "||" + vatStr + "||" + totatlWithVat);
        total.setText(numOfDays + " nights x" + price + "SAR \t" + totalStr + "SAR");
        vat.setText("VAT: "+ vatStr);
        summary.setText("Total: " + totatlWithVat);
    }

    public void confirmHandler(View view) {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }
}