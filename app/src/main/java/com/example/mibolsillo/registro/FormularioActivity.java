package com.example.mibolsillo.registro;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mibolsillo.AdminSQLiteHelper;
import com.example.mibolsillo.databinding.ActivityFormularioBinding;

public class FormularioActivity extends AppCompatActivity {

    private ActivityFormularioBinding binding;
    private AdminSQLiteHelper dbHelper;
    private int idMovimiento = -1; // -1 significa que es un registro nuevo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormularioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new AdminSQLiteHelper(this);

        // Verificar si viene información de la pantalla principal para editar
        if (getIntent().hasExtra("id")) {
            idMovimiento = getIntent().getIntExtra("id", -1);
            binding.lblTitulo.setText("Editar Movimiento");
            binding.etDescripcion.setText(getIntent().getStringExtra("descripcion"));
            binding.etValor.setText(String.valueOf(getIntent().getDoubleExtra("valor", 0.0)));

            String tipo = getIntent().getStringExtra("tipo");
            if (tipo != null && tipo.equalsIgnoreCase("Gasto")) {
                binding.rbGasto.setChecked(true);
            }
        }

        binding.btnGuardar.setOnClickListener(v -> {
            String desc = binding.etDescripcion.getText().toString().trim();
            String valorStr = binding.etValor.getText().toString().trim();
            String tipo = binding.rbIngreso.isChecked() ? "Ingreso" : "Gasto";

            if (desc.isEmpty() || valorStr.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double valor = Double.parseDouble(valorStr);
            boolean exito;

            if (idMovimiento == -1) {
                // Es una inserción nueva
                exito = dbHelper.insertarMovimiento(desc, valor, tipo);
            } else {
                // Es una actualización de registro existente
                exito = dbHelper.actualizarMovimiento(idMovimiento, desc, valor, tipo);
            }

            if (exito) {
                Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show();
                finish(); // Cierra la pantalla y regresa al MainActivity
            } else {
                Toast.makeText(this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}