package es.dailos.proyectosqlite.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import es.dailos.proyectosqlite.bbdd.DatabaseHelper;
import es.dailos.proyectosqlite.entity.Usuario;

public class UsuarioDAO {

    private final DatabaseHelper dbHelper;

    public UsuarioDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long insertar(Usuario usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", usuario.getNombre());
        values.put("apellidos", usuario.getApellidos());
        values.put("dni", usuario.getDni());
        values.put("telefono", usuario.getTelefono());

        long id = db.insert("usuario", null, values);
        db.close();
        return id;
    }

    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuario", null);

        if (cursor.moveToFirst()) {
            do {
                Usuario u = new Usuario(
                        cursor.getInt(cursor.getColumnIndexOrThrow("idUsuario")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("apellidos")),
                        cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                        cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
                );
                lista.add(u);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public Usuario obtenerPorId(int idUsuario) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuario WHERE idUsuario = ?", new String[]{String.valueOf(idUsuario)});
        Usuario usuario = null;

        if (cursor.moveToFirst()) {
            usuario = new Usuario(
                    cursor.getInt(cursor.getColumnIndexOrThrow("idUsuario")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cursor.getString(cursor.getColumnIndexOrThrow("apellidos")),
                    cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                    cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
            );
        }
        cursor.close();
        db.close();
        return usuario;
    }

    public int actualizar(Usuario usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", usuario.getNombre());
        values.put("apellidos", usuario.getApellidos());
        values.put("dni", usuario.getDni());
        values.put("telefono", usuario.getTelefono());

        int filasAfectadas = db.update("usuario", values, "idUsuario = ?", new String[]{String.valueOf(usuario.getIdusuario())});
        db.close();
        return filasAfectadas;
    }

    public int eliminar(int idUsuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filasAfectadas = db.delete("usuario", "idUsuario = ?", new String[]{String.valueOf(idUsuario)});
        db.close();
        return filasAfectadas;
    }
}
