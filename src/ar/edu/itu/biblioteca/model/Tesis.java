package ar.edu.itu.biblioteca.model;

public class Tesis extends MaterialBibliografico {

    public Tesis(
            int id,
            String codigo,
            String titulo,
            int cantidadTotal
    ) {

        super(
                id,
                codigo,
                titulo,
                cantidadTotal
        );
    }

    @Override
    public String obtenerTipoMaterial() {

        return "TESIS";
    }
}
