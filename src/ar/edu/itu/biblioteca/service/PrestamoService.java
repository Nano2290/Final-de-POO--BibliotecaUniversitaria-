package ar.edu.itu.biblioteca.service;

import java.time.LocalDate;

import ar.edu.itu.biblioteca.exception.BibliotecaException;
import ar.edu.itu.biblioteca.exception.LimitePrestamosExcedidoException;
import ar.edu.itu.biblioteca.exception.MaterialNoDisponibleException;
import ar.edu.itu.biblioteca.exception.UsuarioSuspendidoException;

import ar.edu.itu.biblioteca.model.Docente;
import ar.edu.itu.biblioteca.model.Estudiante;
import ar.edu.itu.biblioteca.model.MaterialBibliografico;
import ar.edu.itu.biblioteca.model.Prestamo;
import ar.edu.itu.biblioteca.model.Tesis;
import ar.edu.itu.biblioteca.model.Usuario;

public class PrestamoService {

    public Prestamo registrarPrestamo(
            int idPrestamo,
            Usuario usuario,
            MaterialBibliografico material,
            int prestamosActivos
    ) throws BibliotecaException {

        /*
         * 1. Validamos que el usuario exista.
         */
        if (
                usuario == null
        ) {

            throw new BibliotecaException(
                    "El usuario no existe."
            );
        }


        /*
         * 2. El usuario debe estar activo.
         */
        if (
                !usuario.isActivo()
        ) {

            throw new UsuarioSuspendidoException(
                    "El usuario se encuentra suspendido."
            );
        }


        /*
         * 3. Controlamos el limite de prestamos
         * segun el tipo de usuario.
         */
        if (
                prestamosActivos
                        >= usuario.obtenerLimitePrestamos()
        ) {

            throw new LimitePrestamosExcedidoException(
                    "El usuario alcanzo el limite de prestamos."
            );
        }


        /*
         * 4. Validamos que el material exista.
         */
        if (
                material == null
        ) {

            throw new BibliotecaException(
                    "El material no existe."
            );
        }


        /*
         * 5. Validamos que tenga ejemplares disponibles.
         */
        if (
                !material.estaDisponible()
        ) {

            throw new MaterialNoDisponibleException(
                    "El material no posee ejemplares disponibles."
            );
        }


        /*
         * 6. Compatibilidad entre tipo de usuario
         * y tipo de material.
         *
         * Regla definida para el sistema:
         *
         * ESTUDIANTE:
         * - Libro
         * - Revista
         *
         * DOCENTE:
         * - Libro
         * - Revista
         * - Tesis
         */
        validarCompatibilidad(
                usuario,
                material
        );


        /*
         * Si todas las validaciones fueron correctas,
         * creamos el prestamo.
         */
        Prestamo prestamo =
                new Prestamo(
                        idPrestamo,
                        usuario,
                        material,
                        LocalDate.now()
                );


        /*
         * El objeto refleja que un ejemplar
         * deja de estar disponible.
         */
        material.prestar();


        return prestamo;
    }


    private void validarCompatibilidad(
            Usuario usuario,
            MaterialBibliografico material
    ) throws BibliotecaException {

        /*
         * Los estudiantes no pueden retirar tesis.
         */
        if (
                usuario instanceof Estudiante
                        && material instanceof Tesis
        ) {

            throw new BibliotecaException(
                    "Los estudiantes no pueden solicitar tesis en prestamo."
            );
        }


        /*
         * Los docentes pueden retirar todos los
         * tipos de material actualmente definidos.
         */
        if (
                usuario instanceof Docente
        ) {

            return;
        }


        /*
         * Esta validacion protege al sistema si en el
         * futuro aparece un tipo de usuario no contemplado.
         */
        if (
                !(usuario instanceof Estudiante)
        ) {

            throw new BibliotecaException(
                    "Tipo de usuario no compatible con el material."
            );
        }
    }
}