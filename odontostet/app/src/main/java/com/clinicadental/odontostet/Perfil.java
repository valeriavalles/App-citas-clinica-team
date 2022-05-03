package com.clinicadental.odontostet;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;


public class Perfil extends AppCompatActivity {

    private Button btnEditar;
    FirebaseAuth firebaseauth;
    FirebaseUser user;
    //FirebaseDatabase firebaseDatabase;
    CollectionReference databaseReference;
    FirebaseFirestore FirebaseFirestore;

    public TextView textUserName;
    public String user_id;

    private Button buttonPlanDate, btnWtp;
    private String fullUserName;
    private Button buttonExit;

    ImageButton btnHome,btnMapa;

    private String imageUrl;
    private ImageView imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        textUserName = findViewById(R.id.textUserName);

        buttonPlanDate = findViewById(R.id.buttonPlanDate);
        buttonPlanDate.setOnClickListener(view -> showReservaciones());
        btnWtp = findViewById(R.id.btnW);

        btnHome = findViewById(R.id.btnHome);
        btnMapa = findViewById(R.id.btnMapa);

        imageView3 = findViewById(R.id.imageView3);

        findViewById(R.id.buttonCancelDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        firebaseauth = FirebaseAuth.getInstance();
        user = firebaseauth.getCurrentUser();
        user_id = firebaseauth.getCurrentUser().getUid();

        FirebaseFirestore = FirebaseFirestore.getInstance();
        //databaseReference = FirebaseFirestore.getInstance().collection("Personas");
        //databaseReference.document("Nombres")
        /*if (user != null){
            textUserName.setText((databaseReference.document()) );
        }*/

        buttonExit = findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Perfil.this, Perfilregistrado.class);
                startActivity(intent);
            }
        });

        FirebaseFirestore.collection("Personas").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String name = task.getResult().getString("Nombres");
                        String lastname = task.getResult().getString(("Apellidos"));
                        fullUserName = name + " " + lastname;
                        textUserName.setText("Hola, " + fullUserName);
                    }
                }
            }
        });
        functionWts();
        btnEditar = findViewById(R.id.btnEditar);
        btnEditar.setOnClickListener(view -> editarPerfil());

        //     editarDatos();

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Perfil.this, Maps.class);
                startActivity(intent);
            }
        });

    }
//
//    private void editarDatos() {
//        btnEditar = findViewById(R.id.btnEditar);
//    btnEditar.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(Perfil.this  , registrarActivity.class);
//                    intent.putExtra("Stringid", firebaseauth.getCurrentUser().getUid());
//                    Perfil.this.startActivity(intent);
//        }
//    });
//
//    }

    private void functionWts(){
        btnWtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_VIEW);
                //sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send."); sendIntent.setType("text/plain");
                String url = "whatsapp://send?phone=+51989737519text=Hola Antes de reservar necesito su ayuda";
                //sendIntent.setPackage("com.whatsapp");
                sendIntent.setData(Uri.parse(url));
                startActivity(sendIntent);
            }
        });
    }

    private void showReservaciones() {
        Intent reservacionesIntent = new Intent(this, ReservacionesActivity.class);
        reservacionesIntent.putExtra("fullUserName", fullUserName);
        startActivity(reservacionesIntent);
    }

    private void editarPerfil() {
        Intent editarPerfilIntent = new Intent(this, ActualizarPerfilActivity.class);
        startActivity(editarPerfilIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
        loadPhoto();
    }

    private void getUserInfo() {
        FirebaseFirestore.collection("Personas").document(user_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (task.getResult().exists()){
                    String name = task.getResult().getString("Nombres");
                    String lastname = task.getResult().getString(("Apellidos"));
                    fullUserName = name + " " + lastname;
                    textUserName.setText("Hola, " + fullUserName);
                }
            }
        });
    }

    private void loadPhoto() {
        Uri photoUri = firebaseauth.getCurrentUser().getPhotoUrl();
        if (photoUri != null) {
            imageUrl = photoUri.toString();
        }

        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(this).load(imageUrl).into(imageView3);
        }
    }
}