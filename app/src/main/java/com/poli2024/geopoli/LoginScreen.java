package com.poli2024.geopoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {
    private EditText email;
    private EditText password;

    private Button btnIniciarsesion;
    FirebaseAuth firebaseAuth;
    private TextView goResetScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        firebaseAuth = FirebaseAuth.getInstance();

        email=(EditText) findViewById(R.id.editTextTextEmailAddress);
        password=(EditText) findViewById(R.id.editTextTextPassword);
        goResetScreen=findViewById(R.id.passwordReset);
        btnIniciarsesion= findViewById(R.id.button);
        final LoadingCustomer loadingCustomer = new LoadingCustomer(LoginScreen.this);

        btnIniciarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().length()>0&&password.getText().toString().length()>0){
                    loadingCustomer.sartLoading();
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                loadingCustomer.endLoading();
                                Params.UserFirebaseId=task.getResult().getUser().getUid();
                                changeThePage();
                            }else{
                                Toast.makeText(getApplicationContext(), "Usuario/contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                loadingCustomer.endLoading();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Ingrese sus credenciales", Toast.LENGTH_SHORT).show();
                }


            }
        });
        goResetScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginScreen.this,ResetPasword.class);
                startActivity(intent);
            }
        });
    }
    public void changeThePage(){
        Intent iniciar = new Intent(this, UsersScreen.class);
        startActivity(iniciar);
    }
    //redirigir al registro
    public void IrRegistro(View view){
        Intent irRegistro = new Intent(this, RegisterUser.class);
        startActivity(irRegistro);
    }
}