package com.example.rentspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class publishOrDeletePage extends AppCompatActivity {
    private Intent publishPage;
    private Intent deletePage;

    FirebaseUser user;
    DatabaseReference reference;
    private FirebaseAuth mAuth;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_or_delete_page);
        mAuth = FirebaseAuth.getInstance();
    }

    public void Publish(View view) {
        publishPage = new Intent(this, publishProperty.class);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        db.collection("Properties")
                .whereEqualTo("owner", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int propertyCount = task.getResult().size();
                            if (propertyCount <= 3) {
                                startActivity(publishPage);
                            } else {
                                toast("You have exceeded the allowed number of properties please delete one to publish!");
                            }
                        } else {
                            toast("There was an error!");
                        }
                    }
                });
    }

    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    public void Delete(View view) {
        deletePage = new Intent(this, deleteProperty.class);
        startActivity(deletePage);

    }

    public void goBackButton(View view) {
        Intent intent = new Intent(this, account.class);
        startActivity(intent);
    }


}