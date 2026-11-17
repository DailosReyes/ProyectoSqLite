package es.dailos.proyectosqlite.entity;

public class Animal {
    private int idAnimales;
    private String nombre;
    private int edad;
    private String raza;
    private String sexo;
    private int usuarioId;
    private int refugioId;

    public Animal() {
    }

    public Animal(int idAnimales, String nombre, int edad, String raza, String sexo, int usuarioId, int refugioId) {
        this.idAnimales = idAnimales;
        this.nombre = nombre;
        this.edad = edad;
        this.raza = raza;
        this.sexo = sexo;
        this.usuarioId = usuarioId;
        this.refugioId = refugioId;
    }

    public Animal(String nombre, int edad, String raza, String sexo, int usuarioId, int refugioId) {
        this.nombre = nombre;
        this.edad = edad;
        this.raza = raza;
        this.sexo = sexo;
        this.usuarioId = usuarioId;
        this.refugioId = refugioId;
    }

    public int getIdAnimales() {
        return idAnimales;
    }

    public void setIdAnimales(int idAnimales) {
        this.idAnimales = idAnimales;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getRefugioId() {
        return refugioId;
    }

    public void setRefugioId(int refugioId) {
        this.refugioId = refugioId;
    }

    @Override
    public String toString() {
        return nombre + " (" + raza + ")";
    }
}
