package com.example.mibolsillo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.example.mibolsillo.AuditoriaActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etDescripcion, etMonto;
    private RadioButton rbGasto;
    private TextView tvCoordenadas;
    private ImageView ivFotoPreview;
    private Button btnCapturarUbicacion, btnTomarFoto, btnGuardar, btnIrAuditoria, btnCerrarSesion;

    private FusedLocationProviderClient locationClient;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private double latitud = 0.0;
    private double longitud = 0.0;
    private String currentPhotoPath = "";

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        etDescripcion = findViewById(R.id.etDescripcion);
        etMonto = findViewById(R.id.etMonto);
        rbGasto = findViewById(R.id.rbGasto);
        tvCoordenadas = findViewById(R.id.tvCoordenadas);
        ivFotoPreview = findViewById(R.id.ivFotoPreview);
        btnCapturarUbicacion = findViewById(R.id.btnCapturarUbicacion);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnIrAuditoria = findViewById(R.id.btnIrAuditoria);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        if (btnIrAuditoria != null) {
            btnIrAuditoria.setVisibility(View.GONE);
            verificarRolUsuario();
            btnIrAuditoria.setOnClickListener(v -> startActivity(new Intent(this, AuditoriaActivity.class)));
        }

        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        ivFotoPreview.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
                    }
                }
        );

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    Boolean fineLoc = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean camera = permissions.getOrDefault(Manifest.permission.CAMERA, false);
                    if (fineLoc != null && fineLoc) capturarCoordenadas();
                    if (camera != null && camera) abrirCamara();
                }
        );

        if (btnCapturarUbicacion != null) btnCapturarUbicacion.setOnClickListener(v -> verificarPermisoYCapturarUbicacion());
        if (btnTomarFoto != null) btnTomarFoto.setOnClickListener(v -> verificarPermisoYTomarFoto());
        if (btnGuardar != null) btnGuardar.setOnClickListener(v -> guardarMovimientoEnFirestore());
    }

    private void verificarRolUsuario() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            if (btnIrAuditoria != null) btnIrAuditoria.setVisibility(View.GONE);
            return;
        }

        db.collection("usuarios")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (btnIrAuditoria != null) {
                        if (documentSnapshot.exists() && "admin".equalsIgnoreCase(documentSnapshot.getString("rol"))) {
                            btnIrAuditoria.setVisibility(View.VISIBLE);
                        } else {
                            btnIrAuditoria.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (btnIrAuditoria != null) btnIrAuditoria.setVisibility(View.GONE);
                });
    }

    private void verificarPermisoYCapturarUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            capturarCoordenadas();
        } else {
            permissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }

    private void capturarCoordenadas() {
        try {
            locationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    latitud = location.getLatitude();
                    longitud = location.getLongitude();
                    tvCoordenadas.setText(String.format(Locale.getDefault(), "Lat: %.5f, Lon: %.5f", latitud, longitud));
                    Toast.makeText(this, "GPS capturado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Activa el GPS en el dispositivo", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            Toast.makeText(this, "Error de permisos", Toast.LENGTH_SHORT).show();
        }
    }

    private void verificarPermisoYTomarFoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            permissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
        }
    }

    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                photoFile = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
                currentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                Toast.makeText(this, "Error al crear archivo de imagen", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.mibolsillo.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraLauncher.launch(takePictureIntent);
            }
        }
    }

    private void guardarMovimientoEnFirestore() {
        String desc = etDescripcion.getText().toString().trim();
        String montoStr = etMonto.getText().toString().trim();

        if (desc.isEmpty() || montoStr.isEmpty()) {
            Toast.makeText(this, "Ingresa descripción y monto", Toast.LENGTH_SHORT).show();
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipo = rbGasto.isChecked() ? "GASTO" : "INGRESO";
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        String userEmail = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : "anonimo";

        Movimiento mov = new Movimiento("", desc, monto, tipo, latitud, longitud, currentPhotoPath, fecha, userEmail);

        db.collection("movimientos")
                .add(mov)
                .addOnSuccessListener(documentReference -> {
                    mov.setId(documentReference.getId());
                    documentReference.update("id", documentReference.getId());
                    Toast.makeText(MainActivity.this, "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                    limpiarFormulario();
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void limpiarFormulario() {
        etDescripcion.setText("");
        etMonto.setText("");
        tvCoordenadas.setText("GPS: No capturado");
        ivFotoPreview.setImageResource(0);
        latitud = 0.0;
        longitud = 0.0;
        currentPhotoPath = "";
    }
}