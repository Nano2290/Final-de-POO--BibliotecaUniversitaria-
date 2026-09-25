package ar.edu.itu.biblioteca.service;

import java.time.LocalDate;

import ar.edu.itu.biblioteca.exception.BibliotecaException;
import ar.edu.itu.biblioteca.exception.LimitePrestamosExcedidoException;
import ar.edu.itu.biblioteca.exception.MaterialNoDisponibleException;
import ar.edu.itu.biblioteca.exception.UsuarioSuspendidoException;
import ar.edu.itu.biblioteca.model.MaterialBibliografico;
import ar.edu.itu.biblioteca.model.Prestamo;
import ar.edu.itu.biblioteca.model.Usuario;

public class PrestamoService {

    public Prestamo registrarPrestamo(
            int idPrestamo,
            Usuario usuario,
            MaterialBibliografico material,
            int prestamosActivos) throws BibliotecaException {

        if (usuario == null) {
            throw new BibliotecaException(
                    "El usuario no existe."
            );
        }

        if (!usuario.isActivo()) {
            throw new UsuarioSuspendidoException(
                    "El usuario se encuentra suspendido."
            );
        }

        if (prestamosActivos >= usuario.obtenerLimitePrestamos()) {
            throw new LimitePrestamosExcedidoException(
                    "El usuario alcanzo el limite de prestamos."
            );
        }

        if (material == null) {
            throw new BibliotecaException(
                    "El material no existe."
            );
        }

        if (!material.estaDisponible()) {
            throw new MaterialNoDisponibleException(
                    "El material no posee ejemplares disponibles."
            );
        }

        Prestamo prestamo = new Prestamo(
                idPrestamo,
                usuario,
                material,
                LocalDate.now()
        );

        material.prestar();

        return prestamo;
    }
}