package com.example.mibolsillo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegistrar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Si ya hay sesión activa, pasa de inmediato sin cargar la vista de login
        if (mAuth.getCurrentUser() != null) {
            irAMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        if (btnLogin != null) btnLogin.setOnClickListener(v -> loginUsuario());
        if (btnRegistrar != null) btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void loginUsuario() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        irAMainActivity();
                    } else {
                        Toast.makeText(this, "Error: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Fallo de autenticación"),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void registrarUsuario() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || password.length() < 6) {
            Toast.makeText(this, "Ingresa un correo y una contraseña de al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Map<String, Object> nuevoUsuario = new HashMap<>();
                            nuevoUsuario.put("email", email);
                            nuevoUsuario.put("rol", "cliente");

                            db.collection("usuarios").document(user.getUid())
                                    .set(nuevoUsuario)
                                    .addOnSuccessListener(aVoid -> irAMainActivity())
                                    .addOnFailureListener(e -> irAMainActivity());
                        }
                    } else {
                        Toast.makeText(this, "Error al registrar: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Error desconocido"),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void irAMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}