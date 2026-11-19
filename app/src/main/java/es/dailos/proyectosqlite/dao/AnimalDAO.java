package es.dailos.proyectosqlite.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import es.dailos.proyectosqlite.bbdd.DatabaseHelper;
import es.dailos.proyectosqlite.entity.Animal;

public class AnimalDAO {

    private final DatabaseHelper dbHelper;

    public AnimalDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long insertar(Animal animal) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", animal.getNombre());
        values.put("edad", animal.getEdad());
        values.put("raza", animal.getRaza());
        values.put("sexo", animal.getSexo());

        if (animal.getUsuarioId() > 0) {
            values.put("usuario_id", animal.getUsuarioId());
        } else {
            values.putNull("usuario_id");
        }

        if (animal.getRefugioId() > 0) {
            values.put("refugio_id", animal.getRefugioId());
        } else {
            values.putNull("refugio_id");
        }

        long id = -1;
        try {
            id = db.insert("animal", null, values);
        } catch (Exception e) {
            Log.e("AnimalDAO", "Error insertando animal", e);
        } finally {
            db.close();
        }
        return id;
    }

    public List<Animal> obtenerTodos() {
        List<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM animal", null);

        if (cursor.moveToFirst()) {
            do {
                int idUsr = cursor.isNull(cursor.getColumnIndexOrThrow("usuario_id")) ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow("usuario_id"));
                int idRef = cursor.isNull(cursor.getColumnIndexOrThrow("refugio_id")) ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow("refugio_id"));

                Animal a = new Animal(
                        cursor.getInt(cursor.getColumnIndexOrThrow("idAnimales")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("edad")),
                        cursor.getString(cursor.getColumnIndexOrThrow("raza")),
                        cursor.getString(cursor.getColumnIndexOrThrow("sexo")),
                        idUsr,
                        idRef
                );
                lista.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public List<Animal> obtenerNoAdoptados() {
        List<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM animal WHERE usuario_id IS NULL OR usuario_id <= 0", null);

        if (cursor.moveToFirst()) {
            do {
                int idUsr = cursor.isNull(cursor.getColumnIndexOrThrow("usuario_id")) ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow("usuario_id"));
                int idRef = cursor.isNull(cursor.getColumnIndexOrThrow("refugio_id")) ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow("refugio_id"));

                Animal a = new Animal(
                        cursor.getInt(cursor.getColumnIndexOrThrow("idAnimales")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("edad")),
                        cursor.getString(cursor.getColumnIndexOrThrow("raza")),
                        cursor.getString(cursor.getColumnIndexOrThrow("sexo")),
                        idUsr,
                        idRef
                );
                lista.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public Animal obtenerPorId(int idAnimal) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM animal WHERE idAnimales = ?", new String[]{String.valueOf(idAnimal)});
        Animal animal = null;

        if (cursor.moveToFirst()) {
            int idUsr = cursor.isNull(cursor.getColumnIndexOrThrow("usuario_id")) ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow("usuario_id"));
            int idRef = cursor.isNull(cursor.getColumnIndexOrThrow("refugio_id")) ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow("refugio_id"));

            animal = new Animal(
                    cursor.getInt(cursor.getColumnIndexOrThrow("idAnimales")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("edad")),
                    cursor.getString(cursor.getColumnIndexOrThrow("raza")),
                    cursor.getString(cursor.getColumnIndexOrThrow("sexo")),
                    idUsr,
                    idRef
            );
        }
        cursor.close();
        db.close();
        return animal;
    }

    public List<Animal> obtenerPorUsuario(int usuarioId) {
        List<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM animal WHERE usuario_id = ?", new String[]{String.valueOf(usuarioId)});

        if (cursor.moveToFirst()) {
            do {
                int idUsr = cursor.isNull(cursor.getColumnIndexOrThrow("usuario_id")) ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow("usuario_id"));
                int idRef = cursor.isNull(cursor.getColumnIndexOrThrow("refugio_id")) ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow("refugio_id"));

                Animal a = new Animal(
                        cursor.getInt(cursor.getColumnIndexOrThrow("idAnimales")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("edad")),
                        cursor.getString(cursor.getColumnIndexOrThrow("raza")),
                        cursor.getString(cursor.getColumnIndexOrThrow("sexo")),
                        idUsr,
                        idRef
                );
                lista.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public int actualizar(Animal animal) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", animal.getNombre());
        values.put("edad", animal.getEdad());
        values.put("raza", animal.getRaza());
        values.put("sexo", animal.getSexo());

        if (animal.getUsuarioId() > 0) {
            values.put("usuario_id", animal.getUsuarioId());
        } else {
            values.putNull("usuario_id");
        }

        if (animal.getRefugioId() > 0) {
            values.put("refugio_id", animal.getRefugioId());
        } else {
            values.putNull("refugio_id");
        }

        int filasAfectadas = 0;
        try {
            filasAfectadas = db.update("animal", values, "idAnimales = ?", new String[]{String.valueOf(animal.getIdAnimales())});
        } catch (Exception e) {
            Log.e("AnimalDAO", "Error actualizando animal", e);
        } finally {
            db.close();
        }
        return filasAfectadas;
    }

    public int eliminar(int idAnimal) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filasAfectadas = db.delete("animal", "idAnimales = ?", new String[]{String.valueOf(idAnimal)});
        db.close();
        return filasAfectadas;
    }
}
