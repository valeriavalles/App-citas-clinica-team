package com.clinicadental.odontostet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.clinicadental.odontostet.modelo.Persona;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class registrarActivity extends AppCompatActivity {

       //Aqui se hace referencia a los objetos
    EditText txtNombres, txtApellidos, txtEmail, txtContra;
    Button btnRegistrar,btnRegresar;

    //Aqui se coloca las referencias de FIREBASE
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseauth;

    //Aquí se asigna referencias, se conectan activitys en esta sección
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        //Inicializamos el objeto firebaseAuth
        firebaseauth = FirebaseAuth.getInstance();

        asignarReferencias();
        inicializarFirebase();

    }

    //Con estas lineas se inicializa Firebase
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance(); //se crea una instancia
        databaseReference = firebaseDatabase.getReference();
    }

    private void asignarReferencias() {
        txtNombres = findViewById(R.id.txtNombres);
        txtApellidos = findViewById(R.id.txtApellidos);
        txtEmail = findViewById(R.id.txtEmail);
        txtContra = findViewById(R.id.txtContra);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegresar = findViewById(R.id.btnRegresar);
        //la siguiente linea es para dar funcionalidad al boton

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(registrarActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar();
            }
        });
    }
    //para dar acción al boton
    private void registrar(){
        String nombres = txtNombres.getText().toString();
        String apellidos = txtApellidos.getText().toString();
        String correo = txtEmail.getText().toString();
        String contra = txtContra.getText().toString();

        if (TextUtils.isEmpty(nombres)){
            Toast.makeText(this, "Se debe ingresar los nombres", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(apellidos)){
            Toast.makeText(this, "Se debe ingresar los apellidos", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(correo)){
            Toast.makeText(this, "Se debe ingresar un correo", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(contra)){
                Toast.makeText(this, "Se debe ingresar una contraseña", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseauth.createUserWithEmailAndPassword(correo,contra).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
         @Override
         public void onComplete(@NonNull Task<AuthResult> task) {
             if (task.isSuccessful()){
                 Toast.makeText(registrarActivity.this, "Se ha registrado el correo", Toast.LENGTH_LONG).show();
                 limpiarcajas();
             }
             else{
                 if (task.getException() instanceof FirebaseAuthUserCollisionException) {//Esto sirve para ver si ya existe el usuario
                    Toast.makeText(registrarActivity.this, "Ese usuario ya existe", Toast.LENGTH_SHORT).show();
                    }else{
                     Toast.makeText(registrarActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_LONG).show();
                 }


             }
             Dialog progressDialog = null;
             progressDialog.dismiss();
         }

            private void limpiarcajas() {
             txtNombres.setText("");
             txtApellidos.setText("");
             txtEmail.setText("");
             txtContra.setText("");
            }
        });



        //se creara un objeto
        //Persona p = new Persona();
        //p.setId(UUID.randomUUID().toString());
        //p.setNombres(nombres);
        //p.setApellidos(apellidos);
        //p.setCorreo(correo);
        //p.setContrasenia(contra);

        //aquí se crea un nuevo nodo en la BD
        //databaseReference.child("Persona").child(p.getId()).setValue(p); //Se crea la tabla principal y luego su hijo
        //Toast.makeText(this, "Persona Agregada", Toast.LENGTH_SHORT).show();
    }
}