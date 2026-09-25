package ar.edu.itu.biblioteca.model;

import java.time.LocalDate;

public class Multa {

    private int id;
    private Prestamo prestamo;
    private int diasAtraso;
    private double monto;
    private LocalDate fechaGeneracion;
    private boolean pagada;

    public Multa(int id, Prestamo prestamo, int diasAtraso) {
        this.id = id;
        this.prestamo = prestamo;
        this.diasAtraso = diasAtraso;
        this.monto = prestamo.getUsuario().calcularMulta(diasAtraso);
        this.fechaGeneracion = LocalDate.now();
        this.pagada = false;
    }

    public int getId() {
        return id;
    }

    public Prestamo getPrestamo() {
        return prestamo;
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

    public void marcarComoPagada() {
        this.pagada = true;
    }
}