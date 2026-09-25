package ar.edu.itu.biblioteca.model;

import java.time.LocalDate;

public class Prestamo {

    private int id;
    private Usuario usuario;
    private MaterialBibliografico material;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private LocalDate fechaDevolucion;

    public Prestamo(
            int id,
            Usuario usuario,
            MaterialBibliografico material,
            LocalDate fechaInicio) {

        this.id = id;
        this.usuario = usuario;
        this.material = material;
        this.fechaInicio = fechaInicio;

        this.fechaVencimiento =
                fechaInicio.plusDays(usuario.obtenerDiasPrestamo());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
    this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public MaterialBibliografico getMaterial() {
        return material;
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

    public void registrarDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean estaActivo() {
        return fechaDevolucion == null;
    }

    public boolean estaVencido() {
        return estaActivo()
                && LocalDate.now().isAfter(fechaVencimiento);
    }
}