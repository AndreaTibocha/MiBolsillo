package com.example.mibolsillo;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AuditoriaActivity extends AppCompatActivity {

    private ListView lvMovimientos;
    private TextView tvTotalBalance;
    private FirebaseFirestore db;
    private List<String> listaTextos;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditoria);

        lvMovimientos = findViewById(R.id.lvMovimientos);
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        db = FirebaseFirestore.getInstance();

        listaTextos = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaTextos);
        lvMovimientos.setAdapter(adapter);

        cargarDatosDesdeFirestore();
    }

    private void cargarDatosDesdeFirestore() {
        db.collection("movimientos").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                listaTextos.clear();
                double balance = 0.0;

                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Movimiento m = doc.toObject(Movimiento.class);
                    double monto = m.getMonto();
                    if ("GASTO".equalsIgnoreCase(m.getTipo())) {
                        balance -= monto;
                    } else {
                        balance += monto;
                    }

                    String item = String.format(Locale.getDefault(),
                            "[%s] %s: $%.2f\nFecha: %s | GPS: (%.4f, %.4f)",
                            m.getTipo(), m.getDescripcion(), m.getMonto(), m.getFecha(), m.getLatitud(), m.getLongitud()
                    );
                    listaTextos.add(item);
                }

                adapter.notifyDataSetChanged();
                tvTotalBalance.setText(String.format(Locale.getDefault(), "Balance Total: $%.2f", balance));
            } else {
                Toast.makeText(this, "Error al cargar movimientos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

