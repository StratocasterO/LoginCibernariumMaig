package com.omarolmedoferrer.logincibernarium3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    EditText email, pass;
    Button signin, login;
    FirebaseAuth firebase;
    TextView recovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // seleccionem elements
        email = findViewById(R.id.editEmail);
        pass = findViewById(R.id.editPassword);
        signin = findViewById(R.id.signin);
        login = findViewById(R.id.login);
        recovery = findViewById(R.id.recovery);

        // inicialitza firebase
        firebase = FirebaseAuth.getInstance();

        // entra al perfil si hi ha usuari
        if (firebase.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            finish();
        }

        // signin trigger
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString().trim();
                String userPass = pass.getText().toString().trim();

                // validació de camps de text
                if(TextUtils.isEmpty(userEmail)){
                    email.setError("Has d'escriure un mail");
                    return;
                }

                if(TextUtils.isEmpty(userPass)){
                    pass.setError("Has d'escriure una contrasenya");
                    return;
                }

                if(userPass.length() < 6){
                    pass.setError("La contrasenya ha de tenir 6 caràcters o més");
                    return;
                }

                Log.d("clic", "has clicat en sign in: " + userEmail + ", " + userPass);

                // registre
                firebase.createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d("signin", "S'ha creat un usuari");

                            // email verification
                            firebase.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this, "S'ha enviat un email de verificació", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "No s'ha pogut enviar l'email de verificació: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            // entra al perfil
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "No s'ha pogut crear l'usuari. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        // login trigger
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString().trim();
                String userPass = pass.getText().toString().trim();

                // validació de camps de text
                if(TextUtils.isEmpty(userEmail)){
                    email.setError("Has d'escriure un mail");
                    return;
                }

                if(TextUtils.isEmpty(userPass)){
                    pass.setError("Has d'escriure una contrasenya");
                    return;
                }

                if(userPass.length() < 6){
                    pass.setError("La contrasenya ha de tenir 6 caràcters o més");
                    return;
                }

                Log.d("clic", "has clicat en log in: " + userEmail + ", " + userPass);

                // login
                firebase.signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // envia al perfil
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "No s'ha pogut loguejar l'usuari. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // contrasenya oblidada
        recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // crea el dialog
                final AlertDialog.Builder passwordReset = new AlertDialog.Builder(MainActivity.this);
                final EditText resetEmail = new EditText(v.getContext());

                passwordReset.setTitle("Recuperació de contrasenya");
                passwordReset.setMessage("Escriu el teu email per rebre un missatge de recuperació:");
                passwordReset.setView(resetEmail);

                // boto de confirmar
                passwordReset.setPositiveButton("Envia", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // sends recovery message
                        String email = resetEmail.getText().toString();
                        firebase.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Comprova la teva safata d'entrada", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Hi ha hagut un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                // boto de cance·lar
                passwordReset.setNegativeButton("Cancel·la", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // mostra el dialog
                passwordReset.create().show();
            }
        });
    }
}