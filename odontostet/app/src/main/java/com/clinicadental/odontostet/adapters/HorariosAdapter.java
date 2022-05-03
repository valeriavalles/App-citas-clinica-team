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

import java.util.ArrayList;

public class HorariosAdapter extends RecyclerView.Adapter<HorariosAdapter.ViewHolder> {

    private ArrayList<Horario> horarios;
    private SelectHorarioListener listener;

    public HorariosAdapter(ArrayList<Horario> horarios, SelectHorarioListener listener) {
        this.horarios = horarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horario_layout, parent, false);

        return new HorariosAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Horario horario = horarios.get(position);

        holder.fechaTxt.setText(horario.getFecha());
        holder.drTxt.setText(horario.getDoctor());
        holder.horaTxt.setText(horario.getHora());

        if (horario.isSelected()) {
            holder.horarioRadioBtn.setChecked(true);
        }
        else {
            holder.horarioRadioBtn.setChecked(false);
        }

        holder.horarioRadioBtn.setOnClickListener(view -> listener.selectHorario(horario.getIdHorario()));
    }

    @Override
    public int getItemCount() {
        return horarios == null ? 0 : horarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView fechaTxt;
        public TextView drTxt;
        public TextView horaTxt;
        public RadioButton horarioRadioBtn;

        public ViewHolder(View view) {
            super(view);

            fechaTxt = view.findViewById(R.id.fechaTxt);
            drTxt = view.findViewById(R.id.drTxt);
            horaTxt = view.findViewById(R.id.horaTxt);
            horarioRadioBtn = view.findViewById(R.id.horarioRadioBtn);
        }
    }

    public void actualizarSeleccion(ArrayList<Horario> horarios) {
        this.horarios = horarios;

        notifyDataSetChanged();
    }
}
