package com.poli2024.geopoli;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Button button;
    private FirebaseAuth mAuth;
    TimerTask tarea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent iniciar = new Intent(this, UsersScreen.class);
            startActivity(iniciar);
            Params.UserFirebaseId=currentUser.getUid();
            Toast.makeText(getApplicationContext(), "un usuario", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(MainActivity.this, LoginScreen.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "no hay usario", Toast.LENGTH_SHORT).show();
        }
        // Check if user is signed in (non-null) and update UI accordingly.

    }
}