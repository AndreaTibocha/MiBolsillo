package com.example.mibolsillo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class AdminSQLiteHelper extends SQLiteOpenHelper {

    // Nombre genérico de base de datos
    private static final String DATABASE_NAME = "misiones_bolsillo.db";
    private static final int DATABASE_VERSION = 1;

    public AdminSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // ERROR INTENCIONADO: SQL hardcoded sin constantes. Esto genera warnings amarillos.
        db.execSQL("CREATE TABLE movimientos (id INTEGER PRIMARY KEY AUTOINCREMENT, descripcion TEXT, valor REAL, tipo TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS movimientos");
        onCreate(db);
    }

    // Método para insertar
    public boolean insertarMovimiento(String desc, double val, String tipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("descripcion", desc);
        valores.put("valor", val);
        valores.put("tipo", tipo);
        long resultado = db.insert("movimientos", null, valores);
        return resultado != -1;
    }

    // Método para leer (Obtener historial)
    public ArrayList<Movimiento> obtenerHistorial() {
        ArrayList<Movimiento> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta directa hecha a mano
        Cursor cursor = db.rawQuery("SELECT * FROM movimientos", null);

        if (cursor.moveToFirst()) {
            do {
                Movimiento mov = new Movimiento(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getString(3)
                );
                lista.add(mov);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // Método para editar
    public boolean actualizarMovimiento(int id, String desc, double val, String tipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("descripcion", desc);
        valores.put("valor", val);
        valores.put("tipo", tipo);

        int filasAfectadas = db.update("movimientos", valores, "id = ?", new String[]{String.valueOf(id)});
        return filasAfectadas > 0;
    }
}