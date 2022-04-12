package com.clinicadental.odontostet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    //declarar acciones
    ListView listaPersonas;
    Button btnNuevo2;

    EditText idUsername, idUserPassword;

    String userEmail, userPassword;
    Button btnIngresar;

    //Aqui se coloca las referencias de FIREBASE
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseauth;

    //Aquí se asigna referencias, se conectan activitys en esta sección
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializamos el objeto firebaseAuth
        firebaseauth = FirebaseAuth.getInstance();

        asignarReferencias();
        inicializarFirebase();
    }



    private void asignarReferencias() {
        listaPersonas = findViewById(R.id.listaPersonas);
        btnNuevo2 = findViewById(R.id.btnNuevo2);
        btnIngresar = findViewById(R.id.btnIngresar);
        idUsername = findViewById(R.id.idUsername);
        idUserPassword = findViewById(R.id.idUserPassword);

        //funcionalidad para que abra el segundo activity
        btnNuevo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, registrarActivity.class);
                startActivity(intent);
            }
        });
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

    }

    private void loginUser() {
        //Validar que se ingrese usuario y contraseña

        userEmail = idUsername.getText().toString();
        userPassword = idUserPassword.getText().toString();
        if (TextUtils.isEmpty(userEmail)){
            Toast.makeText(this, "Se requiere email", Toast.LENGTH_LONG).show();
            return;
        }else if (TextUtils.isEmpty(userPassword)){
            Toast.makeText(this, "Se requiere contraseña", Toast.LENGTH_LONG).show();
            return;
        }else{
            firebaseauth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if (task.isSuccessful()) {
                          Toast.makeText(MainActivity.this, "Sesión iniciada", Toast.LENGTH_SHORT).show();
                          limpiarcajas();

                          Intent intent = new Intent(MainActivity.this, Perfilregistrado.class);
                          intent.putExtra("userName",userEmail);

                          startActivity(intent);
                      }

                      else {

                          Toast.makeText(MainActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                      }
                  }
              }
            );
        }
    }

    private void limpiarcajas() {
        idUsername.setText("");
        idUserPassword.setText("");
    }


    //Con estas lineas se inicializa Firebase
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance(); //se crea una instancia
        databaseReference = firebaseDatabase.getReference();
    }
}