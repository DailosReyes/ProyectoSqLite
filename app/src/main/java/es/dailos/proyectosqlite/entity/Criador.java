package es.dailos.proyectosqlite.entity;

public class Criador {
    private int idCriador;
    private String nombre;
    private String direccion;
    private String telefono;

    public Criador() {
    }

    public Criador(int idCriador, String nombre, String direccion, String telefono) {
        this.idCriador = idCriador;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public Criador(String nombre, String direccion, String telefono) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public int getIdCriador() {
        return idCriador;
    }

    public void setIdCriador(int idCriador) {
        this.idCriador = idCriador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
