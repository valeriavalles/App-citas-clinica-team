package com.clinicadental.odontostet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clinicadental.odontostet.R;
import com.clinicadental.odontostet.listeners.SelectHorarioListener;
import com.clinicadental.odontostet.modelo.Horario;
import com.clinicadental.odontostet.modelo.Reservacion;

import java.util.ArrayList;

public class ReservacionesAdapter extends RecyclerView.Adapter<ReservacionesAdapter.ViewHolder> {

    private ArrayList<Reservacion> reservaciones;

    public ReservacionesAdapter(ArrayList<Reservacion> reservaciones) {
        this.reservaciones = reservaciones;
    }

    @NonNull
    @Override
    public ReservacionesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservacion_layout, parent, false);

        return new ReservacionesAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservacionesAdapter.ViewHolder holder, int position) {
        final Reservacion reservacion = reservaciones.get(position);

        holder.fechaTxt.setText(reservacion.getHorario().getFecha());
        holder.drTxt.setText(reservacion.getHorario().getDoctor());
    }

    @Override
    public int getItemCount() {
        return reservaciones == null ? 0 : reservaciones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView fechaTxt;
        public TextView drTxt;

        public ViewHolder(View view) {
            super(view);

            fechaTxt = view.findViewById(R.id.fechaTxt);
            drTxt = view.findViewById(R.id.doctorTxt);
        }
    }
}
