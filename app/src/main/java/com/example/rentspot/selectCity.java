package com.example.rentspot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class selectCity extends AppCompatActivity {
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        Intent inf = getIntent();
        type = inf.getStringExtra("type");
    }


    public void ryadhHandler(View view) {
        Intent intent = new Intent(this, displayProperties.class);
        intent.putExtra("city", "ryadh");
        intent.putExtra("type", type);
        startActivity(intent);
    }

    public void jeddahHandler(View view) {
        Intent intent = new Intent(this, displayProperties.class);
        intent.putExtra("city", "jeddah");
        intent.putExtra("type", type);
        startActivity(intent);
    }
}