package ar.edu.itu.biblioteca.model;

public class Libro extends MaterialBibliografico {

    public Libro(int id, String codigo, String titulo, int cantidadTotal) {
        super(id, codigo, titulo, cantidadTotal);
    }

    @Override
    public String obtenerTipoMaterial() {
        return "LIBRO";
    }
}