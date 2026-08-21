package com.example.mibolsillo.historial;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mibolsillo.Movimiento;
import com.example.mibolsillo.R;
import com.example.mibolsillo.registro.FormularioActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvHistorial;
    private FloatingActionButton fabAgregar;
    private MovimientoAdapter adaptador;
    private List<Movimiento> listaMovimientos;

    private FirebaseFirestore db;
    private ListenerRegistration listenerFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvHistorial = findViewById(R.id.rvHistorial);
        fabAgregar = findViewById(R.id.fabAgregar);

        rvHistorial.setLayoutManager(new LinearLayoutManager(this));
        listaMovimientos = new ArrayList<>();
        adaptador = new MovimientoAdapter(listaMovimientos);
        rvHistorial.setAdapter(adaptador);

        db = FirebaseFirestore.getInstance();

        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
            startActivity(intent);
        });

        escucharMovimientosEnTiempoReal();
    }

    private void escucharMovimientosEnTiempoReal() {
        listenerFirestore = db.collection("movimientos")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(MainActivity.this, "Error en tiempo real: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        listaMovimientos.clear();

                        for (QueryDocumentSnapshot doc : snapshots) {
                            Movimiento mov = doc.toObject(Movimiento.class);
                            mov.setId(doc.getId());
                            listaMovimientos.add(mov);
                        }

                        adaptador.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerFirestore != null) {
            listenerFirestore.remove();
        }
    }
}