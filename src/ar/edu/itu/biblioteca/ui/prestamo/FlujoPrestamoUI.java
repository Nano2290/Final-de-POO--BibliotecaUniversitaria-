package ar.edu.itu.biblioteca.ui.prestamo;

import ar.edu.itu.biblioteca.ui.ConsolaUI;
import ar.edu.itu.biblioteca.ui.MenuMultas;

import ar.edu.itu.biblioteca.dao.PrestamoDAO;
import ar.edu.itu.biblioteca.exception.BibliotecaException;
import ar.edu.itu.biblioteca.model.MaterialBibliografico;
import ar.edu.itu.biblioteca.model.MotivoSuspension;
import ar.edu.itu.biblioteca.model.Prestamo;
import ar.edu.itu.biblioteca.model.Usuario;
import ar.edu.itu.biblioteca.service.PrestamoService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class FlujoPrestamoUI {

    private final Scanner scanner;
    private final ConsolaUI ui;
    private final SelectorMaterial selectorMaterial;
    private final ConsultaPrestamosUI consultas;


    public FlujoPrestamoUI(
            Scanner scanner
    ) {

        this.scanner = scanner;
        this.ui = new ConsolaUI(scanner);
        this.selectorMaterial =
                new SelectorMaterial(scanner);
        this.consultas =
                new ConsultaPrestamosUI(scanner);
    }


    public void registrarPrestamo(
            Usuario usuario
    ) {

        ui.limpiarPantalla();


        System.out.println(
                "╔══════════════════════════════════════════════╗"
        );

        System.out.println(
                "║              REGISTRAR PRESTAMO             ║"
        );

        System.out.println(
                "╚══════════════════════════════════════════════╝"
        );


        System.out.println();

        System.out.println(
                "Usuario: "
                        + usuario.getNombre()
                        + " "
                        + usuario.getApellido()
        );

        System.out.println(
                "DNI: "
                        + usuario.getDni()
        );

        System.out.println(
                "Estado: "
                        + (
                                usuario.isActivo()
                                        ? "ACTIVO"
                                        : "SUSPENDIDO"
                        )
        );


        PrestamoDAO prestamoDAO =
                new PrestamoDAO();


        int prestamosActivos;


        try {

            prestamosActivos =
                    prestamoDAO
                            .listarPrestamosActivosPorUsuario(
                                    usuario.getId()
                            )
                            .size();


        } catch (SQLException e) {

            ui.mostrarMensaje(
                    "No se pudo consultar el estado de prestamos del usuario: "
                            + e.getMessage()
            );

            return;
        }


        /*
         * Antes de pedir un material verificamos si el usuario
         * ya tiene alguna condicion que impida el prestamo.
         *
         * El Service volvera a validar estas reglas al confirmar.
         * Esta validacion previa existe para mejorar la experiencia
         * de navegacion y no hacer buscar un material innecesariamente.
         */
        if (
                !usuario.isActivo()
        ) {

            mostrarBloqueoUsuarioSuspendido(
                    usuario,
                    prestamosActivos
            );

            return;
        }


        if (
                prestamosActivos
                        >= usuario.obtenerLimitePrestamos()
        ) {

            mostrarBloqueoLimitePrestamos(
                    usuario,
                    prestamosActivos
            );

            return;
        }


        System.out.println();

        System.out.println(
                "Usuario habilitado para solicitar prestamos."
        );

        System.out.println(
                "Prestamos activos: "
                        + prestamosActivos
                        + " de "
                        + usuario.obtenerLimitePrestamos()
        );


        PrestamoService prestamoService =
                new PrestamoService();


        try {

            MaterialBibliografico material =
                    selectorMaterial
                            .seleccionarParaPrestamo();


            if (
                    material == null
            ) {

                return;
            }


            Prestamo prestamo =
                    prestamoService
                            .registrarPrestamo(
                                    0,
                                    usuario,
                                    material,
                                    prestamosActivos
                            );


            ui.limpiarPantalla();


            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║            CONFIRMAR PRESTAMO               ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.printf(
                    "║ Usuario: %-33s║%n",
                    ui.recortarTexto(
                            usuario.getNombre()
                                    + " "
                                    + usuario.getApellido(),
                            33
                    )
            );

            System.out.printf(
                    "║ DNI:     %-33s║%n",
                    usuario.getDni()
            );

            System.out.printf(
                    "║ Material:%-33s║%n",
                    ui.recortarTexto(
                            material.getTitulo(),
                            33
                    )
            );

            System.out.printf(
                    "║ Codigo:  %-33s║%n",
                    material.getCodigo()
            );

            System.out.printf(
                    "║ Tipo:    %-33s║%n",
                    material.obtenerTipoMaterial()
            );

            System.out.printf(
                    "║ Inicio:  %-33s║%n",
                    formatearFecha(
                            prestamo.getFechaInicio()
                    )
            );

            System.out.printf(
                    "║ Vence:   %-33s║%n",
                    formatearFecha(
                            prestamo.getFechaVencimiento()
                    )
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println(
                    "║  1. Confirmar                               ║"
            );

            System.out.println(
                    "║  0. Cancelar                                ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════╝"
            );

            System.out.print(
                    "\nSeleccione una opcion: "
            );


            int confirmacion =
                    ui.leerEntero();


            if (
                    confirmacion == 0
            ) {

                return;
            }


            if (
                    confirmacion != 1
            ) {

                ui.mostrarMensaje(
                        "Opcion invalida."
                );

                return;
            }


            prestamoDAO.registrarPrestamo(
                    prestamo
            );


            ui.mostrarMensaje(
                    "Prestamo registrado correctamente."
            );


        } catch (BibliotecaException e) {

            mostrarErrorPrestamoContextual(
                    usuario,
                    e.getMessage()
            );


        } catch (SQLException e) {

            ui.mostrarMensaje(
                    "No se pudo registrar el prestamo: "
                            + e.getMessage()
            );
        }
    }

    private void mostrarBloqueoUsuarioSuspendido(
            Usuario usuario,
            int prestamosActivos
    ) {

        MotivoSuspension motivo =
                usuario.getMotivoSuspension() == null
                        ? MotivoSuspension.SIN_ESPECIFICAR
                        : usuario.getMotivoSuspension();


        int opcion;

        do {

            ui.limpiarPantalla();


            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║          PRESTAMO NO DISPONIBLE             ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.printf(
                    "║ Usuario: %-33s║%n",
                    ui.recortarTexto(
                            usuario.getNombre()
                                    + " "
                                    + usuario.getApellido(),
                            33
                    )
            );

            System.out.printf(
                    "║ Estado:  %-33s║%n",
                    "SUSPENDIDO"
            );

            System.out.printf(
                    "║ Motivo:  %-33s║%n",
                    motivo.name()
            );

            System.out.printf(
                    "║ Prestamos activos: %-24d║%n",
                    prestamosActivos
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );


            if (
                    motivo == MotivoSuspension.DEUDA
            ) {

                System.out.println(
                        "║ La suspension corresponde a deuda pendiente.║"
                );

                System.out.println(
                        "║ No se puede registrar un nuevo prestamo.    ║"
                );

                System.out.println(
                        "╠══════════════════════════════════════════════╣"
                );

                System.out.println(
                        "║  1. Ver multas                              ║"
                );

                System.out.println(
                        "║  2. Ver prestamos activos                   ║"
                );

                System.out.println(
                        "║  3. Ver historial de prestamos              ║"
                );

            } else {

                System.out.println(
                        "║ La suspension no corresponde a deuda.       ║"
                );

                System.out.println(
                        "║ Revise el estado desde la ficha del usuario.║"
                );

                System.out.println(
                        "╠══════════════════════════════════════════════╣"
                );

                System.out.println(
                        "║  1. Ver prestamos activos                   ║"
                );

                System.out.println(
                        "║  2. Ver historial de prestamos              ║"
                );
            }


            System.out.println(
                    "║                                              ║"
            );

            System.out.println(
                    "║  0. Volver a la ficha                       ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════╝"
            );

            System.out.print(
                    "\nSeleccione una opcion: "
            );


            opcion =
                    ui.leerEntero();


            if (
                    motivo == MotivoSuspension.DEUDA
            ) {

                switch (opcion) {

                    case 1: {

                        MenuMultas menuMultas =
                                new MenuMultas(
                                        scanner
                                );

                        menuMultas.mostrarMultasDeUsuario(
                                usuario
                        );

                        break;
                    }


                    case 2:

                        consultas.mostrarPrestamosActivos(
                                usuario
                        );

                        break;


                    case 3:

                        consultas.mostrarHistorialPrestamos(
                                usuario
                        );

                        break;


                    case 0:

                        break;


                    default:

                        ui.mostrarMensaje(
                                "Opcion invalida."
                        );

                        break;
                }

            } else {

                switch (opcion) {

                    case 1:

                        consultas.mostrarPrestamosActivos(
                                usuario
                        );

                        break;


                    case 2:

                        consultas.mostrarHistorialPrestamos(
                                usuario
                        );

                        break;


                    case 0:

                        break;


                    default:

                        ui.mostrarMensaje(
                                "Opcion invalida."
                        );

                        break;
                }
            }

        } while (
                opcion != 0
        );
    }

    private void mostrarBloqueoLimitePrestamos(
            Usuario usuario,
            int prestamosActivos
    ) {

        int opcion;

        do {

            ui.limpiarPantalla();


            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║          LIMITE DE PRESTAMOS                ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.printf(
                    "║ Usuario: %-33s║%n",
                    ui.recortarTexto(
                            usuario.getNombre()
                                    + " "
                                    + usuario.getApellido(),
                            33
                    )
            );

            System.out.printf(
                    "║ Prestamos activos: %-24d║%n",
                    prestamosActivos
            );

            System.out.printf(
                    "║ Limite permitido: %-24d║%n",
                    usuario.obtenerLimitePrestamos()
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println(
                    "║ El usuario alcanzo su limite de prestamos.  ║"
            );

            System.out.println(
                    "║ Debe devolver material antes de continuar.  ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println(
                    "║  1. Ver prestamos activos                   ║"
            );

            System.out.println(
                    "║  2. Ver historial de prestamos              ║"
            );

            System.out.println(
                    "║                                              ║"
            );

            System.out.println(
                    "║  0. Volver a la ficha                       ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════╝"
            );

            System.out.print(
                    "\nSeleccione una opcion: "
            );


            opcion =
                    ui.leerEntero();


            switch (opcion) {

                case 1:

                    consultas.mostrarPrestamosActivos(
                            usuario
                    );

                    break;


                case 2:

                    consultas.mostrarHistorialPrestamos(
                            usuario
                    );

                    break;


                case 0:

                    break;


                default:

                    ui.mostrarMensaje(
                            "Opcion invalida."
                    );

                    break;
            }

        } while (
                opcion != 0
        );
    }

    private void mostrarErrorPrestamoContextual(
            Usuario usuario,
            String mensaje
    ) {

        int opcion;

        do {

            ui.limpiarPantalla();


            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║            PRESTAMO RECHAZADO               ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println();

            System.out.println(
                    mensaje
            );

            System.out.println();

            System.out.println(
                    "1. Intentar con otro material"
            );

            System.out.println(
                    "2. Ver prestamos activos del usuario"
            );

            System.out.println(
                    "0. Volver a la ficha"
            );

            System.out.print(
                    "\nSeleccione una opcion: "
            );


            opcion =
                    ui.leerEntero();


            switch (opcion) {

                case 1:

                    registrarPrestamo(
                            usuario
                    );

                    return;


                case 2:

                    consultas.mostrarPrestamosActivos(
                            usuario
                    );

                    break;


                case 0:

                    break;


                default:

                    ui.mostrarMensaje(
                            "Opcion invalida."
                    );

                    break;
            }

        } while (
                opcion != 0
        );
    }

    private String formatearFecha(
            LocalDate fecha
    ) {

        if (fecha == null) {
            return "-";
        }

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                );

        return fecha.format(
                formato
        );
    }
}
