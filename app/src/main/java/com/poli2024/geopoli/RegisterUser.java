package com.poli2024.geopoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity {
    Button botonRegistro;
    EditText name;
    EditText lastNme;
    EditText email;
    EditText password;
    FirebaseAuth firebaseAuth;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        botonRegistro = findViewById(R.id.buttonRegistro);
        firebaseAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance().getReference();
        name=findViewById(R.id.editTextTextPersonName);
        lastNme= findViewById(R.id.editTextTextPersonLastName);
        email=findViewById(R.id.editTextTextEmailAddress2);
        password=findViewById(R.id.editTextTextPassword2);
        final LoadingCustomer loadingCustomer = new LoadingCustomer(RegisterUser.this);

        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(name.getText().toString().length()>0&&lastNme.getText().toString().length()>0&& email.getText().toString().length()>0&password.getText().toString().length()>0){
                    loadingCustomer.sartLoading();
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loadingCustomer.endLoading();
                            System.out.println("LA RESPUESTA DEL TASK: " + task.isSuccessful());
                            if(task.isSuccessful()){

                                Mineros mineros = new Mineros(name.getText().toString(),lastNme.getText().toString(),email.getText().toString(),task.getResult().getUser().getUid());
                                Toast.makeText(RegisterUser.this, "Usuario Registrado", Toast.LENGTH_LONG).show();
                                saveAppUser(mineros);
                                Params.UserFirebaseId= mineros.getId();

                                changeThePage();
                            }else{
                                Toast.makeText(RegisterUser.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Ingrese los campos requeridos", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
    public void changeThePage(){
        Intent iniciar = new Intent(this, UsersScreen.class);
        startActivity(iniciar);
    }


    public void IrLoginAct(View view){
        Intent irLog = new Intent(this, LoginScreen.class);
        startActivity(irLog);
    }

    public void IrTabs(View view){
        Intent iniciarTabs = new Intent(this, UsersScreen.class);
        startActivity(iniciarTabs);
    }
    public void saveAppUser(Mineros mineros){
        database.child("users").child(mineros.getId()).setValue(mineros);
    }
}