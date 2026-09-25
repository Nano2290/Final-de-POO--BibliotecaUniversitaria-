package ar.edu.itu.biblioteca.model;

public class Docente extends Usuario {

    public Docente(
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
        return 5;
    }

    @Override
    public int obtenerDiasPrestamo() {
        return 15;
    }

    @Override
    public double calcularMulta(int diasAtraso) {
        return diasAtraso * 300.0;
    }
}