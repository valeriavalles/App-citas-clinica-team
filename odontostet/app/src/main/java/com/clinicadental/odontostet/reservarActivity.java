package com.clinicadental.odontostet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.clinicadental.odontostet.fragments.SeleccionarHorarioFragment;
import com.clinicadental.odontostet.modelo.Horario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ReservarActivity extends AppCompatActivity {

    private ImageView serviceImg;
    private TextView serviceNameTxt;
    private TextView serviceDescTxt;
    private Button btnReservar;
    private TextView serviceCostTxt;

    private String serviceId;
    private String serviceImage;
    private String serviceName;
    private String serviceDesc;
    private String serviceCost;
    private Button btnCancelar;

    public MaterialDialog progressDialog;

    private ArrayList<Horario> horarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getExtras();
        initUI();
        setValues();
    }

    private void initUI() {
        serviceImg = findViewById(R.id.serviceImg);
        serviceNameTxt = findViewById(R.id.serviceNameTxt);
        serviceDescTxt = findViewById(R.id.serviceDescTxt);
        serviceCostTxt = findViewById(R.id.serviceCostTxt);

        btnReservar = findViewById(R.id.btnReservar);
        btnReservar.setOnClickListener(view -> cargarHorarios());

        btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getExtras() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("id_servicio"))
                serviceId = getIntent().getExtras().getString("id_servicio");

            if (getIntent().getExtras().containsKey("nombre_servicio"))
                serviceName = getIntent().getExtras().getString("nombre_servicio");

            if (getIntent().getExtras().containsKey("desc_servicio"))
                serviceDesc = getIntent().getExtras().getString("desc_servicio");

            if (getIntent().getExtras().containsKey("imagen_servicio"))
                serviceImage = getIntent().getExtras().getString("imagen_servicio");

            if (getIntent().getExtras().containsKey("costo_servicio"))
                serviceCost = getIntent().getExtras().getString("costo_servicio");
        }
    }

    private void setValues() {
        if (!TextUtils.isEmpty(serviceImage)) {
            Glide.with(this).load(serviceImage).into(serviceImg);
        }

        serviceNameTxt.setText(serviceName);
        serviceDescTxt.setText(serviceDesc);
        serviceCostTxt.setText(getString(R.string.service_cost_hint) + " " + serviceCost);
    }

    private void cargarHorarios() {
        showProgress("Cargando Horarios", "Espere por favor");

        FirebaseFirestore.getInstance().collection("Horarios").whereEqualTo("id_servicio", serviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                horarios = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Horario horario = new Horario();

                    horario.setIdHorario(document.getId());
                    horario.setDoctor(document.getString("doctor"));
                    horario.setFecha(document.getString("fecha"));
                    horario.setHora(document.getString("hora"));

                    horarios.add(horario);
                }

                hideProgress();

                showHorarios(horarios);
            }
            else {
                hideProgress();
            }
        });
    }

    public void showProgress(String title, String message) {
        try {
            progressDialog = new MaterialDialog.Builder(this)
                    .backgroundColor(ContextCompat.getColor(this, R.color.blanco))
                    .title(title)
                    .content(message)
                    .progress(true, 0)
                    .progressIndeterminateStyle(false)
                    .build();
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgress() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    public void showHorarios(ArrayList<Horario> horariosServicio) {
        if (horariosServicio != null && horariosServicio.size() > 0) {
            SeleccionarHorarioFragment dialogFragment = new SeleccionarHorarioFragment();

            Bundle params = new Bundle();
            params.putSerializable("horarios", horariosServicio);
            params.putString("id_servicio", serviceId);

            dialogFragment.setCancelable(false);
            dialogFragment.setArguments(params);
            dialogFragment.show(getSupportFragmentManager(), "HorariosDialogFragment");
        }
        else {
            showAlert("Sin horarios", "Lo sentimos, por ahora no contamos con horarios disponibles");
        }
    }

    private void showAlert(String title, String message) {
        hideProgress();
        new MaterialDialog.Builder(this)
                .backgroundColor(ContextCompat.getColor(this, R.color.blanco))
                .title(title)
                .content(message)
                .positiveText("Aceptar")
                .show();
    }
}