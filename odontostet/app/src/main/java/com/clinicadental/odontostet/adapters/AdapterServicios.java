package com.clinicadental.odontostet.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.clinicadental.odontostet.R;
import com.clinicadental.odontostet.listeners.OpenServicioListener;
import com.clinicadental.odontostet.modelo.Servicio;

import java.util.ArrayList;

public class AdapterServicios extends RecyclerView.Adapter<AdapterServicios.ViewHolder> {

    private boolean isNavigationViewList = false;

    private ArrayList<Servicio> servicios;
    private Context context;
    private OpenServicioListener listener;

    public AdapterServicios(ArrayList<Servicio> servicios, Context context, OpenServicioListener listener) {
        this.servicios = servicios;
        this.context = context;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView serviceImg;
        public TextView serviceNameTxt;
        public LinearLayout rootLyt;

        public ViewHolder(View view) {
            super(view);

            serviceImg = view.findViewById(R.id.serviceImg);
            serviceNameTxt = view.findViewById(R.id.serviceNameTxt);
            rootLyt = view.findViewById(R.id.rootLyt);
        }
    }

    @Override
    public AdapterServicios.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_layout, parent, false);

        return new AdapterServicios.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdapterServicios.ViewHolder holder, final int position) {
        final Servicio servicio = servicios.get(position);

        holder.serviceNameTxt.setText(servicio.getNombreServicio());

        if (!TextUtils.isEmpty(servicio.getImgServicio())) {
            Glide.with(context).load(servicio.getImgServicio()).into(holder.serviceImg);
        }

        holder.rootLyt.setOnClickListener(view -> listener.abrirServicio(servicio.getIdServicio()));
    }

    @Override
    public int getItemCount() {
        return servicios == null ? 0 : servicios.size();
    }

    public void setServicios(ArrayList<Servicio> servicios) {
        this.servicios = servicios;

        notifyDataSetChanged();
    }
}
