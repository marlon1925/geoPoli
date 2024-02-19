package com.example.geopoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasword extends AppCompatActivity {
    Button buttonSendEmail;
    EditText emailInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pasword);
        buttonSendEmail=findViewById(R.id.buttonSendEmail);
        emailInput=findViewById(R.id.editTextTextEmailAddress);
        final LoadingCustomer loadingCustomer = new LoadingCustomer(ResetPasword.this);

        buttonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailInput.getText().toString().length()>0){
                    loadingCustomer.sartLoading();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailInput.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    loadingCustomer.endLoading();

                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Email de verficación enviado", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Error al enviar el email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(getApplicationContext(), "Ingrese su email", Toast.LENGTH_SHORT).show();

                }




            }
        });
    }

}