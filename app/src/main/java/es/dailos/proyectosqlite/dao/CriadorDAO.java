package es.dailos.proyectosqlite.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import es.dailos.proyectosqlite.bbdd.DatabaseHelper;
import es.dailos.proyectosqlite.entity.Criador;

public class CriadorDAO {

    private final DatabaseHelper dbHelper;

    public CriadorDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long insertar(Criador criador) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", criador.getNombre());
        values.put("direccion", criador.getDireccion());
        values.put("telefono", criador.getTelefono());

        long id = db.insert("criador", null, values);
        db.close();
        return id;
    }

    public List<Criador> obtenerTodos() {
        List<Criador> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM criador", null);

        if (cursor.moveToFirst()) {
            do {
                Criador c = new Criador(
                        cursor.getInt(cursor.getColumnIndexOrThrow("idCriador")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                        cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
                );
                lista.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public Criador obtenerPorId(int idCriador) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM criador WHERE idCriador = ?", new String[]{String.valueOf(idCriador)});
        Criador criador = null;

        if (cursor.moveToFirst()) {
            criador = new Criador(
                    cursor.getInt(cursor.getColumnIndexOrThrow("idCriador")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                    cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
            );
        }
        cursor.close();
        db.close();
        return criador;
    }

    public int actualizar(Criador criador) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", criador.getNombre());
        values.put("direccion", criador.getDireccion());
        values.put("telefono", criador.getTelefono());

        int filasAfectadas = db.update("criador", values, "idCriador = ?", new String[]{String.valueOf(criador.getIdCriador())});
        db.close();
        return filasAfectadas;
    }

    public int eliminar(int idCriador) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filasAfectadas = db.delete("criador", "idCriador = ?", new String[]{String.valueOf(idCriador)});
        db.close();
        return filasAfectadas;
    }
}
