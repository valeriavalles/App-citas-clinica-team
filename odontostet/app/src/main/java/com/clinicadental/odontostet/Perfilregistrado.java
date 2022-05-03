package com.clinicadental.odontostet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.clinicadental.odontostet.adapters.AdapterServicios;
import com.clinicadental.odontostet.listeners.OpenServicioListener;
import com.clinicadental.odontostet.modelo.Servicio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Perfilregistrado extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener{

    String userLogin;
    Button btnCerrar;
    ImageButton btnPerfil,btnMapa;
    FirebaseAuth firebaseauth;
    FirebaseFirestore firebaseFirestore;

    private RelativeLayout loadingLyt;
    private SearchView searchView;

    private ArrayList<Servicio> servicios;
    private ArrayList<Servicio> serviciosFiltrados;

    private OpenServicioListener abrirServicioListener;

    private AdapterServicios adapter;
    private RecyclerView servicesList;

    private String selectedServiceId;

    public MaterialDialog progressDialog;

    ImageButton cirugiaoral;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfilregistrado);

        asignarReferencias();

        loadServices();

        userLogin = getIntent().getStringExtra("userName");


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Perfilregistrado.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userSession",userLogin);
        editor.commit();

    }

    private void asignarReferencias() {
        loadingLyt = findViewById(R.id.loadingLyt);

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.setEnabled(false);

        cirugiaoral = findViewById(R.id.cirugiaoral);
        btnCerrar = findViewById(R.id.btnCerrar);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnMapa = findViewById(R.id.btnMapa);

        cirugiaoral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Perfilregistrado.this, ReservarActivity.class);
                startActivity(intent);
            }
        });

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              firebaseauth.signOut();
              finish();
              Intent intent = new Intent(Perfilregistrado.this, MainActivity.class);
              startActivity(intent);
            }
        });

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Perfilregistrado.this, Perfil.class);
                startActivity(intent);
            }
        });

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Perfilregistrado.this, Maps.class);
                startActivity(intent);
            }
        });

        servicesList = findViewById(R.id.servicesList);

        abrirServicioListener = new OpenServicioListener() {
            @Override
            public void abrirServicio(String idServicio) {
                abrirDetalleServicio(idServicio);
            }
        };
    }

    private void loadServices() {
        FirebaseFirestore.getInstance().collection("Servicios").get().addOnCompleteListener(task -> {
            servicios = new ArrayList<>();

            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Servicio servicio = new Servicio();
                    servicio.setIdServicio(document.getId());
                    servicio.setNombreServicio(document.getString("nombre_servicio"));
                    servicio.setDescServicio(document.getString("desc_servicio"));
                    servicio.setCostoServicio(document.getString("costo_servicio"));
                    servicio.setImgServicio(document.getString("imagen_servicio"));

                    servicios.add(servicio);
                }

                if (servicios != null && servicios.size() > 0) {
                    adapter = new AdapterServicios(servicios, this, abrirServicioListener);

                    GridLayoutManager serviciosLayoutManager = new GridLayoutManager(this, 2);
                    servicesList.setLayoutManager(serviciosLayoutManager);
                    servicesList.setAdapter(adapter);

                    loadingLyt.setVisibility(View.GONE);
                    searchView.setEnabled(true);
                }
            }
        });
    }

    private void abrirDetalleServicio(String idServicio) {
        selectedServiceId = idServicio;

        Servicio servicio = getServicio(idServicio);

        Intent detailIntent = new Intent(this, ReservarActivity.class);
        detailIntent.putExtra("id_servicio", idServicio);

        if (servicio != null) {
            detailIntent.putExtra("nombre_servicio", servicio.getNombreServicio());
            detailIntent.putExtra("desc_servicio", servicio.getDescServicio());
            detailIntent.putExtra("imagen_servicio", servicio.getImgServicio());
            detailIntent.putExtra("costo_servicio", servicio.getCostoServicio());
        }

        startActivity(detailIntent);
    }

    private void setAllServices() {
        if (adapter != null)
            adapter.setServicios(servicios);
    }

    private void setFilteredServices(ArrayList<Servicio> filteredServices) {
        if (adapter != null)
            adapter.setServicios(filteredServices);
    }

    @Override
    public boolean onClose() {
        setAllServices();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        setFilteredServices(getFilteredServices(query));
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        setFilteredServices(getFilteredServices(newText));
        return false;
    }

    private ArrayList<Servicio> getFilteredServices(String query) {
        ArrayList<Servicio> filteredServices = new ArrayList<>();
        if (TextUtils.isEmpty(query))
            return servicios;
        else {
            for (Servicio servicio : servicios) {
                if (servicio.getNombreServicio().toLowerCase().contains(query.toLowerCase())) {
                    filteredServices.add(servicio);
                }
            }
            return filteredServices;
        }
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

    public Servicio getServicio(String idServicio) {
        if (servicios != null) {
            for (Servicio servicio : servicios) {
                if (servicio.getIdServicio().equals(idServicio))
                    return servicio;
            }
        }

        return null;
    }
}