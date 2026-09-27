package ar.edu.itu.biblioteca.model;

import java.time.LocalDate;

public class PrestamoMaterialResumen {

    private final int idPrestamo;

    private final String dniUsuario;

    private final String nombreUsuario;

    private final String apellidoUsuario;

    private final LocalDate fechaInicio;

    private final LocalDate fechaVencimiento;

    private final LocalDate fechaDevolucion;


    public PrestamoMaterialResumen(
            int idPrestamo,
            String dniUsuario,
            String nombreUsuario,
            String apellidoUsuario,
            LocalDate fechaInicio,
            LocalDate fechaVencimiento,
            LocalDate fechaDevolucion
    ) {

        this.idPrestamo =
                idPrestamo;

        this.dniUsuario =
                dniUsuario;

        this.nombreUsuario =
                nombreUsuario;

        this.apellidoUsuario =
                apellidoUsuario;

        this.fechaInicio =
                fechaInicio;

        this.fechaVencimiento =
                fechaVencimiento;

        this.fechaDevolucion =
                fechaDevolucion;
    }


    public int getIdPrestamo() {
        return idPrestamo;
    }


    public String getDniUsuario() {
        return dniUsuario;
    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }


    public String getApellidoUsuario() {
        return apellidoUsuario;
    }


    public String getNombreCompletoUsuario() {

        return nombreUsuario
                + " "
                + apellidoUsuario;
    }


    public LocalDate getFechaInicio() {
        return fechaInicio;
    }


    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }


    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }


    public String obtenerEstado() {

        if (fechaDevolucion != null) {

            return "DEVUELTO";
        }


        if (
                LocalDate.now()
                        .isAfter(
                                fechaVencimiento
                        )
        ) {

            return "VENCIDO";
        }


        return "ACTIVO";
    }
}