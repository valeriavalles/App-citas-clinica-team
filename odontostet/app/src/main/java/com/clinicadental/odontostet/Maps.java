package com.clinicadental.odontostet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Maps extends AppCompatActivity {

    Button btnLocal1, btnSLocal2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        asignarReferencia();
    }

    private void asignarReferencia() {
        btnLocal1 = findViewById(R.id.btnLocal1);
        btnLocal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Maps.this, MapActivity.class);
                intent.putExtra("latitud", "-12.160033376782804");
                intent.putExtra("longitud", "-76.96800650275318");
                intent.putExtra("titulo", "Odontostetic Medic SEDE SJM");
                startActivity(intent);
            }
        });

        btnSLocal2 = findViewById(R.id.btnLocal2);
        btnSLocal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Maps.this, MapActivity.class);
                intent.putExtra("latitud", "-12.12744965190389");
                intent.putExtra("longitud", "-76.9934567");
                intent.putExtra("titulo", "Odontostetic Medic SEDE SURCO");
                startActivity(intent);
            }
        });
    }
}