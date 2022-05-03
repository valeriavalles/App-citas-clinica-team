package com.clinicadental.odontostet.fragments;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.clinicadental.odontostet.R;
import com.clinicadental.odontostet.ReservacionExitosaActivity;
import com.clinicadental.odontostet.adapters.HorariosAdapter;
import com.clinicadental.odontostet.listeners.SelectHorarioListener;
import com.clinicadental.odontostet.modelo.Horario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SeleccionarHorarioFragment extends DialogFragment {

    private View rootView;

    private ArrayList<Horario> horarios;
    private String horarioSeleccionado;

    private HorariosAdapter adapter;
    private SelectHorarioListener listener;

    private RecyclerView listaHorarios;
    private TextView reservarTxt;
    private TextView cancelTxt;

    private String idServicio;

    public MaterialDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        horarios = (ArrayList<Horario>) getArguments().getSerializable("horarios");
        idServicio = getArguments().getString("id_servicio");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_seleccionar_horario, container, false);

        }
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        init();

        return rootView;
    }

    private void init() {
        reservarTxt = rootView.findViewById(R.id.reservarTxt);
        reservarTxt.setOnClickListener(view -> reservar());

        cancelTxt = rootView.findViewById(R.id.cancelTxt);
        cancelTxt.setOnClickListener(view -> cancel());

        listener = idHorario -> {
            horarioSeleccionado = idHorario;
            actualizarSelección(idHorario);
        };

        listaHorarios = rootView.findViewById(R.id.listaHorarios);
        adapter = new HorariosAdapter(horarios, listener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);

        listaHorarios.setLayoutManager(layoutManager);
        listaHorarios.setAdapter(adapter);
    }

    private void reservar() {
        if (!TextUtils.isEmpty(horarioSeleccionado)) {
            showProgress("Reservando...", "Espere por favor");
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Map<String, Object> map = new HashMap<>();
            map.put("id_horario", horarioSeleccionado);
            map.put("id_servicio", idServicio);
            map.put("id_usuario", userId);

            FirebaseFirestore.getInstance().collection("Reservaciones").add(map).addOnCompleteListener(task -> {
                hideProgress();
                if (task.isSuccessful()) {
                    //showAlertWithDismiss("Reservado", "Se ha reservado el horario correctamente.");
                    dismiss();
                    showReservacion();
                }
                else {
                    showAlert("Error", "Error al reservar horario.\nReíntente por favor.");
                }
            });
        }
        else {
            hideProgress();
            showAlert("Falta seleccionar horario", "Debe seleccionar un horario para resrvar");
        }
    }

    private void cancel() {
        dismiss();
    }

    private void showAlert(String title, String message) {
        new MaterialDialog.Builder(getActivity())
                .backgroundColor(ContextCompat.getColor(getActivity(), R.color.blanco))
                .title(title)
                .content(message)
                .positiveText("Aceptar")
                .show();
    }

    private void showAlertWithDismiss(String title, String message) {
        new MaterialDialog.Builder(getActivity())
                .backgroundColor(ContextCompat.getColor(getActivity(), R.color.blanco))
                .title(title)
                .content(message)
                .positiveText("Aceptar")
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .onAny((dialog, which) -> dismiss())
                .show();
    }

    private void actualizarSelección(String horarioId) {
        if (adapter != null) {
            for (int i = 0; i < horarios.size(); i++) {
                horarios.get(i).setSelected(false);

                if (horarios.get(i).getIdHorario().equals(horarioId)) {
                    horarios.get(i).setSelected(true);
                }
            }

            adapter.actualizarSeleccion(horarios);
        }
    }

    public void showProgress(String title, String message) {
        try {
            progressDialog = new MaterialDialog.Builder(getActivity())
                    .backgroundColor(ContextCompat.getColor(getActivity(), R.color.blanco))
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

    private void showReservacion() {
        Intent intent = new Intent(getActivity(), ReservacionExitosaActivity.class);
        intent.putExtra("id_horario", horarioSeleccionado);
        getActivity().startActivity(intent);
    }
}
