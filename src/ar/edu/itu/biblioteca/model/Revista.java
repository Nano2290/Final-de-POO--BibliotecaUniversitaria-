package ar.edu.itu.biblioteca.model;

public class Revista extends MaterialBibliografico {

    public Revista(int id, String codigo, String titulo, int cantidadTotal) {
        super(id, codigo, titulo, cantidadTotal);
    }

    @Override
    public String obtenerTipoMaterial() {
        return "REVISTA";
    }
}