package ar.edu.itu.biblioteca.model;

import java.time.LocalDate;

public class PrestamoResumen {

    private final int idPrestamo;
    private final String codigoMaterial;
    private final String tituloMaterial;
    private final LocalDate fechaInicio;
    private final LocalDate fechaVencimiento;
    private final LocalDate fechaDevolucion;

    public PrestamoResumen(
            int idPrestamo,
            String codigoMaterial,
            String tituloMaterial,
            LocalDate fechaInicio,
            LocalDate fechaVencimiento,
            LocalDate fechaDevolucion
    ) {
        this.idPrestamo = idPrestamo;
        this.codigoMaterial = codigoMaterial;
        this.tituloMaterial = tituloMaterial;
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaDevolucion = fechaDevolucion;
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public String getCodigoMaterial() {
        return codigoMaterial;
    }

    public String getTituloMaterial() {
        return tituloMaterial;
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

        if (LocalDate.now().isAfter(fechaVencimiento)) {
            return "VENCIDO";
        }

        return "ACTIVO";
    }
}