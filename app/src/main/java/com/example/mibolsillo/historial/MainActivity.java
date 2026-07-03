package com.example.mibolsillo.historial;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mibolsillo.AdminSQLiteHelper;
import com.example.mibolsillo.Movimiento;
import com.example.mibolsillo.databinding.ActivityMainBinding;
import com.example.mibolsillo.databinding.ItemMovimientoBinding;
import com.example.mibolsillo.registro.FormularioActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AdminSQLiteHelper dbHelper;
    private MovimientoAdapter adapter;
    private ArrayList<Movimiento> listaMovimientos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new AdminSQLiteHelper(this);
        listaMovimientos = dbHelper.obtenerHistorial();

        // Configuración básica del listado
        binding.rvHistorial.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MovimientoAdapter(listaMovimientos, movimiento -> {
            // Clic en una tarjeta para editarla
            Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
            intent.putExtra("id", movimiento.getId());
            intent.putExtra("descripcion", movimiento.getDescripcion());
            intent.putExtra("valor", movimiento.getValor());
            intent.putExtra("tipo", movimiento.getTipo());
            startActivity(intent);
        });

        binding.rvHistorial.setAdapter(adapter);

        // Clic en el botón flotante para crear uno nuevo
        binding.fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ERROR INTENCIONADO: Se vuelven a leer los datos, pero NUNCA se notifica al adaptador.
        // La lista se quedará congelada visualmente en pantalla tras registrar o editar algo.
        if (dbHelper != null) {
            listaMovimientos.clear();
            listaMovimientos.addAll(dbHelper.obtenerHistorial());
            // Aquí falta: adapter.notifyDataSetChanged();
        }
    }
}