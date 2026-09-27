package ar.edu.itu.biblioteca.ui;

public final class Colores {

    public static final String RESET = "\u001B[0m";

    public static final String AZUL = "\u001B[34m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String CIAN = "\u001B[36m";
    public static final String VERDE = "\u001B[32m";
    public static final String ROJO = "\u001B[31m";

    private Colores() {
        // Evita crear objetos de esta clase utilitaria.
    }

    public static String tipoMaterial(
            String tipo,
            int ancho
    ) {

        String textoAlineado =
                String.format(
                        "%-" + ancho + "s",
                        tipo
                );

        return switch (
                tipo.toUpperCase()
        ) {

            case "LIBRO" ->
                    AZUL
                            + textoAlineado
                            + RESET;

            case "REVISTA" ->
                    AMARILLO
                            + textoAlineado
                            + RESET;

            case "TESIS" ->
                    CIAN
                            + textoAlineado
                            + RESET;

            default ->
                    textoAlineado;
        };
    }


    public static String disponibilidad(
            int disponibles,
            int total,
            int ancho
    ) {

        String texto =
                disponibles
                        + "/"
                        + total;

        String textoAlineado =
                String.format(
                        "%-" + ancho + "s",
                        texto
                );

        if (
                disponibles == 0
        ) {

            return ROJO
                    + textoAlineado
                    + RESET;
        }

        if (
                disponibles == 1
        ) {

            return AMARILLO
                    + textoAlineado
                    + RESET;
        }

        return VERDE
                + textoAlineado
                + RESET;
    }
}
