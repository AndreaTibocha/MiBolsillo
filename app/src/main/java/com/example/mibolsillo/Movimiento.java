package com.example.mibolsillo;

public class Movimiento {
    private String id; // ID del documento de Firestore (necesario para editar y borrar)
    private String titulo;
    private double monto;
    private String tipo; // "INGRESO" o "GASTO"

    // Constructor vacío obligatorio para que Firebase pueda mapear los datos
    public Movimiento() {}

    // Constructor completo
    public Movimiento(String id, String titulo, double monto, String tipo) {
        this.id = id;
        this.titulo = titulo;
        this.monto = monto;
        this.tipo = tipo;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}