package es.dailos.proyectosqlite.entity;

public class Refugio {
    private int idRefugio;
    private String nombre;
    private String apellidos;
    private String direccion;
    private String telefono;

    public Refugio() {
    }

    public Refugio(int idRefugio, String nombre, String apellidos, String direccion, String telefono) {
        this.idRefugio = idRefugio;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public Refugio(String nombre, String apellidos, String direccion, String telefono) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public int getIdRefugio() {
        return idRefugio;
    }

    public void setIdRefugio(int idRefugio) {
        this.idRefugio = idRefugio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
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
