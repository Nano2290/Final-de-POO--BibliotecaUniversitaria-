package ar.edu.itu.biblioteca.model;

import java.time.LocalDate;

public class PrestamoVencidoResumen {

    private final int idPrestamo;
    private final Usuario usuario;
    private final String codigoMaterial;
    private final String tituloMaterial;
    private final LocalDate fechaInicio;
    private final LocalDate fechaVencimiento;

    public PrestamoVencidoResumen(
            int idPrestamo,
            Usuario usuario,
            String codigoMaterial,
            String tituloMaterial,
            LocalDate fechaInicio,
            LocalDate fechaVencimiento
    ) {
        this.idPrestamo = idPrestamo;
        this.usuario = usuario;
        this.codigoMaterial = codigoMaterial;
        this.tituloMaterial = tituloMaterial;
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public Usuario getUsuario() {
        return usuario;
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
}