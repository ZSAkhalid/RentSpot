package com.example.rentspot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class reservationPage extends AppCompatActivity {
    ImageView home, favorite, reservation, account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_page);
        home = findViewById(R.id.home);
        favorite = findViewById(R.id.favorite);
        reservation = findViewById(R.id.reservation);
        account = findViewById(R.id.account);
    }

    public void bottomButtonHandler(View view) {
        if (view.getId() == home.getId()) {
            Intent intent = new Intent(this, home.class);
            startActivity(intent);
        } else if (view.getId() == favorite.getId()) {
            Intent intent = new Intent(this, favorites.class);
            startActivity(intent);
        } else if (view.getId() == reservation.getId()) {
            //Intent intent = new Intent(this, reservation.class);
            //startActivity(intent);
        } else {
            Intent intent = new Intent(this, account.class);
            startActivity(intent);
        }
    }

    public void logOutHandler(View view) {
    }

    public void hostWithUs(View view) {
    }

    public void reservNow(View view) {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }
}