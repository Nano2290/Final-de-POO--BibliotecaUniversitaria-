package ar.edu.itu.biblioteca.model;

public abstract class Usuario {

    private int id;
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private boolean activo;

    public Usuario(
            int id,
            String dni,
            String nombre,
            String apellido,
            String email
    ) {

        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.activo = true;
    }

    public int getId() {
        return id;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public abstract int obtenerLimitePrestamos();

    public abstract int obtenerDiasPrestamo();

    public abstract double calcularMulta(int diasAtraso);
}