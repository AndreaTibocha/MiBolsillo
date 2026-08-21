package com.example.mibolsillo.registro;

import com.example.mibolsillo.Movimiento;
import com.example.mibolsillo.R;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FormularioActivity extends AppCompatActivity {

    private TextInputLayout tilTitulo, tilMonto;
    private TextInputEditText etTitulo, etMonto;
    private RadioGroup rgTipo;
    private RadioButton rbIngreso, rbGasto;
    private MaterialButton btnGuardar, btnEliminar;
    private ProgressBar pbCarga;

    private FirebaseFirestore db;
    private String movimientoId = null; // Si es nulo crea, si tiene texto edita/elimina

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        // Referencias UI
        tilTitulo = findViewById(R.id.tilTitulo);
        tilMonto = findViewById(R.id.tilMonto);
        etTitulo = findViewById(R.id.etTitulo);
        etMonto = findViewById(R.id.etMonto);
        rgTipo = findViewById(R.id.rgTipo);
        rbIngreso = findViewById(R.id.rbIngreso);
        rbGasto = findViewById(R.id.rbGasto);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminar = findViewById(R.id.btnEliminar);
        pbCarga = findViewById(R.id.pbCargaFormulario);

        db = FirebaseFirestore.getInstance();

        // Configurar validaciones en tiempo real (Pilar 4)
        configurarValidaciones();

        // Verificar si venimos a Editar/Eliminar
        if (getIntent().hasExtra("id")) {
            movimientoId = getIntent().getStringExtra("id");
            etTitulo.setText(getIntent().getStringExtra("titulo"));
            etMonto.setText(String.valueOf(getIntent().getDoubleExtra("monto", 0.0)));

            String tipo = getIntent().getStringExtra("tipo");
            if ("GASTO".equalsIgnoreCase(tipo)) {
                rbGasto.setChecked(true);
            } else {
                rbIngreso.setChecked(true);
            }

            btnGuardar.setText("ACTUALIZAR MOVIMIENTO");
            btnEliminar.setVisibility(View.VISIBLE);
        }

        // Listeners de botones
        btnGuardar.setOnClickListener(v -> {
            if (esFormularioValido()) {
                guardarOActualizarEnFirebase();
            }
        });

        btnEliminar.setOnClickListener(v -> eliminarDeFirebase());
    }

    private void configurarValidaciones() {
        // Validation en tiempo real con TextWatcher (Pilar 4)
        etMonto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    tilMonto.setError("El monto no puede estar vacío");
                } else try {
                    double valor = Double.parseDouble(s.toString().trim());
                    if (valor <= 0) {
                        tilMonto.setError("El monto debe ser mayor a $0");
                    } else {
                        tilMonto.setErrorEnabled(false);
                    }
                } catch (NumberFormatException e) {
                    tilMonto.setError("Monto inválido");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etTitulo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() < 3) {
                    tilTitulo.setError("El título debe tener al menos 3 caracteres");
                } else {
                    tilTitulo.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private boolean esFormularioValido() {
        boolean valido = true;
        String titulo = etTitulo.getText().toString().trim();
        String montoStr = etMonto.getText().toString().trim();

        if (titulo.isEmpty() || titulo.length() < 3) {
            tilTitulo.setError("Ingrese un título válido (mínimo 3 letras)");
            valido = false;
        }

        if (montoStr.isEmpty()) {
            tilMonto.setError("Ingrese un monto");
            valido = false;
        } else {
            try {
                double monto = Double.parseDouble(montoStr);
                if (monto <= 0) {
                    tilMonto.setError("El monto debe ser mayor a 0");
                    valido = false;
                }
            } catch (Exception e) {
                tilMonto.setError("Monto no válido");
                valido = false;
            }
        }
        return valido;
    }

    private void guardarOActualizarEnFirebase() {
        String titulo = etTitulo.getText().toString().trim();
        double monto = Double.parseDouble(etMonto.getText().toString().trim());
        String tipo = rbIngreso.isChecked() ? "INGRESO" : "GASTO";

        // Prevenir doble envío deshabilitando el botón (Pilar 4)
        btnGuardar.setEnabled(false);
        btnEliminar.setEnabled(false);
        pbCarga.setVisibility(View.VISIBLE);

        Map<String, Object> movimientoMap = new HashMap<>();
        movimientoMap.put("titulo", titulo);
        movimientoMap.put("monto", monto);
        movimientoMap.put("tipo", tipo);

        if (movimientoId == null) {
            // CREATE: Agregar nuevo documento
            db.collection("movimientos").add(movimientoMap)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> restaurarBoton("Error al guardar: " + e.getMessage()));
        } else {
            // UPDATE: Actualizar documento existente
            db.collection("movimientos").document(movimientoId).update(movimientoMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> restaurarBoton("Error al actualizar: " + e.getMessage()));
        }
    }

    private void eliminarDeFirebase() {
        if (movimientoId != null) {
            btnGuardar.setEnabled(false);
            btnEliminar.setEnabled(false);
            pbCarga.setVisibility(View.VISIBLE);

            // DELETE: Eliminar documento por Document ID
            db.collection("movimientos").document(movimientoId).delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Movimiento eliminado", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> restaurarBoton("Error al eliminar: " + e.getMessage()));
        }
    }

    private void restaurarBoton(String mensajeError) {
        pbCarga.setVisibility(View.GONE);
        btnGuardar.setEnabled(true);
        btnEliminar.setEnabled(true);
        Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show();
    }
}