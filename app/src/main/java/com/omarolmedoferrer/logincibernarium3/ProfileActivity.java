package com.omarolmedoferrer.logincibernarium3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    TextView email;
    Button logout;
    FirebaseAuth firebase;
    FirebaseUser user;
    ImageView check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // seleccionar elements
        email = findViewById(R.id.userEmail);
        logout = findViewById(R.id.logout);
        check = findViewById(R.id.check);

        // inicialitzar firebase
        firebase = FirebaseAuth.getInstance();
        user = firebase.getCurrentUser();

        // info de l'usuari
        if (user != null){
            email.setText(user.getEmail());

            if(user.isEmailVerified()){
                check.setVisibility(View.VISIBLE);
            } else {
                check.setVisibility(View.INVISIBLE);
            }
        }

        // logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebase.signOut();

                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}