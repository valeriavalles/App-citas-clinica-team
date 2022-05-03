package com.clinicadental.odontostet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.clinicadental.odontostet.adapters.ReservacionesAdapter;
import com.clinicadental.odontostet.modelo.Horario;
import com.clinicadental.odontostet.modelo.Reservacion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class ReservacionesActivity extends AppCompatActivity {

    public MaterialDialog progressDialog;

    private ImageView profileImg;
    private TextView textUserName;
    private RelativeLayout nextCitaLyt;
    private TextView fechaHoraTxt;
    private TextView doctorTxt;

    private RecyclerView citasList;
    private RelativeLayout emptyLyt;
    private RelativeLayout loadingLyt;

    private String userId;

    private ArrayList<Reservacion> reservaciones;
    private ReservacionesAdapter adapter;

    private String fullUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservaciones);

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("fullUserName"))
                fullUserName = getIntent().getExtras().getString("fullUserName");
        }

        initUI();
        cargarReservaciones();
    }

    private void initUI() {
        profileImg = findViewById(R.id.profileImg);

        textUserName = findViewById(R.id.textUserName);
        if (!TextUtils.isEmpty(fullUserName))
            textUserName.setText(fullUserName);

        nextCitaLyt = findViewById(R.id.nextCitaLyt);
        fechaHoraTxt = findViewById(R.id.fechaHoraTxt);
        doctorTxt = findViewById(R.id.doctorTxt);

        citasList = findViewById(R.id.citasList);
        emptyLyt = findViewById(R.id.emptyLyt);
        loadingLyt = findViewById(R.id.loadingLyt);
    }

    private void cargarReservaciones() {
        showProgress("Cargando", "Espere por favor");

        reservaciones = new ArrayList<>();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("Reservaciones").whereEqualTo("id_usuario", userId).get().addOnCompleteListener(task -> {
            hideProgress();
            showLoading();

            if (task.isSuccessful()) {
                int total = task.getResult().size();
                int count = 0;
                if (task.getResult().size() > 0) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        count++;
                        Reservacion reservacion = new Reservacion();
                        reservacion.setIdUsuario(userId);
                        reservacion.setIdHorario(documentSnapshot.getString("id_horario"));
                        reservacion.setIdServicio(documentSnapshot.getString("id_servicio"));

                        int finalCount = count;

                        FirebaseFirestore.getInstance().collection("Horarios").document(reservacion.getIdHorario()).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Horario horario = new Horario();
                                horario.setIdHorario(task1.getResult().getId());
                                horario.setFecha(task1.getResult().getString("fecha"));
                                horario.setHora(task1.getResult().getString("hora"));
                                horario.setDoctor(task1.getResult().getString("doctor"));
                                horario.setIdServicio(task1.getResult().getString("id_servicio"));

                                reservacion.setHorario(horario);
                                reservaciones.add(reservacion);

                                if (finalCount == total)
                                    showReservaciones();

                            } else {
                            }
                        });
                    }
                }
                else {
                    showEmptyLyt();
                }
            }
            else {
                hideProgress();
                showEmptyLyt();
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

    public void showEmptyLyt() {
        citasList.setVisibility(View.GONE);
        loadingLyt.setVisibility(View.GONE);
        nextCitaLyt.setVisibility(View.GONE);
        emptyLyt.setVisibility(View.VISIBLE);
    }

    public void showLoading() {
        citasList.setVisibility(View.GONE);
        loadingLyt.setVisibility(View.VISIBLE);
        nextCitaLyt.setVisibility(View.GONE);
        emptyLyt.setVisibility(View.GONE);
    }

    public void showReservaciones() {
        if (reservaciones != null && reservaciones.size() > 0) {
            citasList.setVisibility(View.VISIBLE);
            loadingLyt.setVisibility(View.GONE);
            nextCitaLyt.setVisibility(View.GONE);
            emptyLyt.setVisibility(View.GONE);

            adapter = new ReservacionesAdapter(reservaciones);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

            citasList.setAdapter(adapter);
            citasList.setLayoutManager(layoutManager);

            showNextReservacion();
        }
        else {
            showEmptyLyt();
        }
    }

    public void showNextReservacion() {
        nextCitaLyt.setVisibility(View.VISIBLE);
        Reservacion nextReservacion = reservaciones.get(0);
        fechaHoraTxt.setText(nextReservacion.getHorario().getFecha() + " - " + nextReservacion.getHorario().getHora());
        doctorTxt.setText(nextReservacion.getHorario().getDoctor());
    }
}
