package ar.edu.itu.biblioteca.model;

public abstract class MaterialBibliografico {

    private int id;
    private String codigo;
    private String titulo;
    private int cantidadTotal;
    private int cantidadDisponible;

    public MaterialBibliografico(
            int id,
            String codigo,
            String titulo,
            int cantidadTotal
    ) {

        this.id = id;
        this.codigo = codigo;
        this.titulo = titulo;
        this.cantidadTotal = cantidadTotal;
        this.cantidadDisponible = cantidadTotal;
    }

    public int getId() {

        return id;
    }

    public String getCodigo() {

        return codigo;
    }

    public String getTitulo() {

        return titulo;
    }

    public int getCantidadTotal() {

        return cantidadTotal;
    }

    public int getCantidadDisponible() {

        return cantidadDisponible;
    }

    public boolean estaDisponible() {

        return cantidadDisponible > 0;
    }

    public void prestar() {

        if (
                cantidadDisponible > 0
        ) {

            cantidadDisponible--;
        }
    }

    public void devolver() {

        if (
                cantidadDisponible < cantidadTotal
        ) {

            cantidadDisponible++;
        }
    }

    public void agregarEjemplares(
            int cantidad
    ) {

        if (
                cantidad <= 0
        ) {

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero."
            );
        }

        cantidadTotal +=
                cantidad;

        cantidadDisponible +=
                cantidad;
    }


    public void quitarEjemplares(
            int cantidad
    ) {

        if (
                cantidad <= 0
        ) {

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero."
            );
        }


        if (
                cantidad > cantidadDisponible
        ) {

            throw new IllegalArgumentException(
                    "No se pueden quitar ejemplares que estan prestados."
            );
        }


        cantidadTotal -=
                cantidad;

        cantidadDisponible -=
                cantidad;
    }


    public abstract String obtenerTipoMaterial();
}
