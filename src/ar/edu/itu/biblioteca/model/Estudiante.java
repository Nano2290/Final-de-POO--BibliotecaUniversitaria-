package ar.edu.itu.biblioteca.model;

public class Estudiante extends Usuario {

    public Estudiante(
            int id,
            String dni,
            String nombre,
            String apellido,
            String email
    ) {
        super(id, dni, nombre, apellido, email);
    }

    @Override
    public int obtenerLimitePrestamos() {
        return 3;
    }

    @Override
    public int obtenerDiasPrestamo() {
        return 7;
    }

    @Override
    public double calcularMulta(int diasAtraso) {
        return diasAtraso * 500.0;
    }
}