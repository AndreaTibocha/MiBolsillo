package com.example.mibolsillo;

public class Movimiento {
    private int id;
    private String descripcion; // Sinónimo de concepto
    private double valor;       // Sinónimo de monto
    private String tipo;        // "Ingreso" o "Gasto"

    public Movimiento(int id, String descripcion, double valor, String tipo) {
        this.id = id;
        this.descripcion = descripcion;
        this.valor = valor;
        this.tipo = tipo;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}