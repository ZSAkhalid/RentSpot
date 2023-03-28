package com.example.rentspot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class selectType extends AppCompatActivity {
    String city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type);
        Intent info = getIntent();
        city = info.getStringExtra("city");
    }

    public void campHandler(View view) {
        Intent intent = new Intent(this, displayProperties.class);
        intent.putExtra("city", city);
        intent.putExtra("type", "CAMPS");
        startActivity(intent);
    }
    public void cabanaHandler(View view) {
        Intent intent = new Intent(this, displayProperties.class);
        intent.putExtra("city", city);
        intent.putExtra("type", "CABANA");
        startActivity(intent);
    }
    public void apartmentHandler(View view) {
        Intent intent = new Intent(this, displayProperties.class);
        intent.putExtra("city", city);
        intent.putExtra("type", "APARTMENT");
        startActivity(intent);
    }
}