package com.clinicadental.odontostet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.clinicadental.odontostet.modelo.Horario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReservacionExitosaActivity extends AppCompatActivity {

    private String idHorario;

    private TextView infoTxt;
    private Button exitBtn;

    private MaterialDialog progressDialog;

    private Horario horarioSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservacion_exitosa);

        getExtras();
        initUI();
        setValues();
    }

    private void getExtras() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("id_horario"))
                idHorario = getIntent().getExtras().getString("id_horario");
        }
    }

    private void initUI() {
        infoTxt = findViewById(R.id.infoTxt);

        exitBtn = findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(view -> onBackPressed());
    }

    private void setValues() {
        showProgress("Validando...", "Espere por favor");

        FirebaseFirestore.getInstance().collection("Horarios").document(idHorario).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    horarioSeleccionado = new Horario();
                    horarioSeleccionado.setFecha(task.getResult().getString("fecha"));
                    horarioSeleccionado.setHora(task.getResult().getString("hora"));
                    horarioSeleccionado.setDoctor(task.getResult().getString("doctor"));

                    String info = getString(R.string.reservacion_mensaje, horarioSeleccionado.getFecha(),
                            horarioSeleccionado.getHora(),
                            horarioSeleccionado.getDoctor());

                    infoTxt.setText(info);
                }
                else {

                }

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

    private void hideProgress() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

}
