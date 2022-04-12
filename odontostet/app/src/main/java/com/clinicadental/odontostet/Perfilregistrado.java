package com.clinicadental.odontostet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;

public class Perfilregistrado extends AppCompatActivity {

    String userLogin;

    ImageButton cirugiaoral;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfilregistrado);

        asignarReferencias();

        userLogin = getIntent().getStringExtra("userName");


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Perfilregistrado.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userSession",userLogin);
        editor.commit();

    }

    private void asignarReferencias() {
        cirugiaoral = findViewById(R.id.cirugiaoral);
        cirugiaoral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Perfilregistrado.this, reservarActivity.class);
                startActivity(intent);
            }
        });
    }


}