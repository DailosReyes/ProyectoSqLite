package es.dailos.proyectosqlite.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import es.dailos.proyectosqlite.bbdd.DatabaseHelper;
import es.dailos.proyectosqlite.entity.Refugio;

public class RefugioDAO {

    private final DatabaseHelper dbHelper;

    public RefugioDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long insertar(Refugio refugio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", refugio.getNombre());
        values.put("apellidos", refugio.getApellidos());
        values.put("direccion", refugio.getDireccion());
        values.put("telefono", refugio.getTelefono());

        long id = db.insert("refugio", null, values);
        db.close();
        return id;
    }

    public List<Refugio> obtenerTodos() {
        List<Refugio> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM refugio", null);

        if (cursor.moveToFirst()) {
            do {
                Refugio r = new Refugio(
                        cursor.getInt(cursor.getColumnIndexOrThrow("idRefugio")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("apellidos")),
                        cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                        cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
                );
                lista.add(r);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public Refugio obtenerPorId(int idRefugio) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM refugio WHERE idRefugio = ?", new String[]{String.valueOf(idRefugio)});
        Refugio refugio = null;

        if (cursor.moveToFirst()) {
            refugio = new Refugio(
                    cursor.getInt(cursor.getColumnIndexOrThrow("idRefugio")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cursor.getString(cursor.getColumnIndexOrThrow("apellidos")),
                    cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                    cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
            );
        }
        cursor.close();
        db.close();
        return refugio;
    }

    public int actualizar(Refugio refugio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", refugio.getNombre());
        values.put("apellidos", refugio.getApellidos());
        values.put("direccion", refugio.getDireccion());
        values.put("telefono", refugio.getTelefono());

        int filasAfectadas = db.update("refugio", values, "idRefugio = ?", new String[]{String.valueOf(refugio.getIdRefugio())});
        db.close();
        return filasAfectadas;
    }

    public int eliminar(int idRefugio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filasAfectadas = db.delete("refugio", "idRefugio = ?", new String[]{String.valueOf(idRefugio)});
        db.close();
        return filasAfectadas;
    }
}
