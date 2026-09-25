package ar.edu.itu.biblioteca.model;

import java.time.LocalDate;

public class MultaResumen {

    private final int idMulta;
    private final String codigoMaterial;
    private final String tituloMaterial;
    private final int diasAtraso;
    private final double monto;
    private final LocalDate fechaGeneracion;
    private final boolean pagada;

    public MultaResumen(
            int idMulta,
            String codigoMaterial,
            String tituloMaterial,
            int diasAtraso,
            double monto,
            LocalDate fechaGeneracion,
            boolean pagada
    ) {

        this.idMulta = idMulta;
        this.codigoMaterial = codigoMaterial;
        this.tituloMaterial = tituloMaterial;
        this.diasAtraso = diasAtraso;
        this.monto = monto;
        this.fechaGeneracion = fechaGeneracion;
        this.pagada = pagada;
    }

    public int getIdMulta() {
        return idMulta;
    }

    public String getCodigoMaterial() {
        return codigoMaterial;
    }

    public String getTituloMaterial() {
        return tituloMaterial;
    }

    public int getDiasAtraso() {
        return diasAtraso;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public boolean isPagada() {
        return pagada;
    }

    public String obtenerEstado() {

        return pagada
                ? "PAGADA"
                : "PENDIENTE";
    }
}