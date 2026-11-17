package es.dailos.proyectosqlite.bbdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "aplicacionzoo.db";
    public static final int DB_VERSION = 2;

    public static final String TABLE_USUARIO =
            "CREATE TABLE usuario (" +
                    "idUsuario INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT," +
                    "apellidos TEXT,"+
                    "dni TEXT,"+
                    "telefono TEXT)";

    public static final String TABLE_ANIMAL =
            "CREATE TABLE animal (" +
                    "idAnimales INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "edad INTEGER," +
                    "raza TEXT," +
                    "sexo TEXT,"+
                    "nombre TEXT,"+
                    "usuario_id INTEGER,"+
                    "refugio_id INTEGER,"+
                    "FOREIGN KEY(usuario_id) REFERENCES usuario(idUsuario) ON DELETE CASCADE,"+
                    "FOREIGN KEY(refugio_id) REFERENCES refugio(idRefugio) ON DELETE CASCADE)";

    public static final String TABLE_REFUGIO =
            "CREATE TABLE refugio (" +
                    "idRefugio INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "telefono INTEGER," +
                    "nombre TEXT," +
                    "apellidos TEXT,"+
                    "direccion TEXT)";

    public static final String TABLE_CRIADOR =
            "CREATE TABLE criador (" +
                    "idCriador INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT," +
                    "direccion TEXT," +
                    "telefono INTEGER)";

    public static final String TABLE_ANIMAL_CRIADOR =
            "CREATE TABLE animal_criador (" +
                    "criador_id INTEGER," +
                    "animal_id INTEGER," +
                    "PRIMARY KEY(criador_id, animal_id)," +
                    "FOREIGN KEY(criador_id) REFERENCES criador(idCriador) ON DELETE CASCADE,"+
                    "FOREIGN KEY(animal_id) REFERENCES animal(idAnimales) ON DELETE CASCADE)";

    public static final String TABLE_CRIADOR_REFUGIO =
            "CREATE TABLE criador_refugio (" +
                    "criador_id INTEGER," +
                    "refugio_id INTEGER," +
                    "PRIMARY KEY(criador_id, refugio_id)," +
                    "FOREIGN KEY(criador_id) REFERENCES criador(idCriador) ON DELETE CASCADE,"+
                    "FOREIGN KEY(refugio_id) REFERENCES refugio(idRefugio) ON DELETE CASCADE)";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(TABLE_USUARIO);
        db.execSQL(TABLE_REFUGIO);
        db.execSQL(TABLE_CRIADOR);
        db.execSQL(TABLE_ANIMAL);
        db.execSQL(TABLE_CRIADOR_REFUGIO);
        db.execSQL(TABLE_ANIMAL_CRIADOR);

        android.util.Log.d("DATABASE", "Tablas creadas correctamente");
    }

    @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuario");
        db.execSQL("DROP TABLE IF EXISTS animal");
        db.execSQL("DROP TABLE IF EXISTS criador");
        db.execSQL("DROP TABLE IF EXISTS refugio");
        db.execSQL("DROP TABLE IF EXISTS animal_criador");
        db.execSQL("DROP TABLE IF EXISTS criador_refugio");
        onCreate(db);
    }

    @Override
    //Activa las claves foráneas
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

}
