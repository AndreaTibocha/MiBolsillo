package com.example.mibolsillo;

public class Movimiento {
    private String id;
    private String descripcion;
    private double monto;
    private String tipo; // "INGRESO" o "GASTO"
    private double latitud;
    private double longitud;
    private String fotoPath;
    private String fecha;
    private String usuarioEmail;

    // Constructor vacío requerido por Cloud Firestore
    public Movimiento() {}

    public Movimiento(String id, String descripcion, double monto, String tipo, double latitud, double longitud, String fotoPath, String fecha, String usuarioEmail) {
        this.id = id;
        this.descripcion = descripcion;
        this.monto = monto;
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fotoPath = fotoPath;
        this.fecha = fecha;
        this.usuarioEmail = usuarioEmail;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public String getFotoPath() { return fotoPath; }
    public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }
}