package com.example.rentspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class account extends AppCompatActivity {
    private ImageView home, favorite, reservation, account;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference refrence;
    String userID;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        home = findViewById(R.id.home);
        favorite = findViewById(R.id.favorite);
        reservation = findViewById(R.id.reservation);
        account = findViewById(R.id.account);
        name = findViewById(R.id.usernameID);
        mAuth = FirebaseAuth.getInstance();
        getName();
    }

    public void getName(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        refrence = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        refrence.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                User userInstance = datasnapshot.getValue(User.class);
                name.setText(userInstance.fName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                name.setText("to RentSpot");
            }
        });
    }

    public void bottomButtonHandler(View view) {
        if (view.getId() == home.getId()) {
            Intent intent = new Intent(this, home.class);
            startActivity(intent);
        } else if (view.getId() == favorite.getId()) {
            Intent intent = new Intent(this, favorites.class);
            startActivity(intent);
        } else if (view.getId() == reservation.getId()) {
            Intent intent = new Intent(this, reservationPage.class);
            startActivity(intent);
        } else {
//            Intent intent = new Intent(this, account.class);
//            startActivity(intent);
        }
    }

    public void logOutHandler(View view) {
        mAuth.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void hostWithUs(View view) {
        Intent host = new Intent(this, publishOrDeletePage.class);
        startActivity(host);
    }

}