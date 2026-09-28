package ar.edu.itu.biblioteca.ui;

import ar.edu.itu.biblioteca.model.MaterialBibliografico;
import ar.edu.itu.biblioteca.model.Usuario;

import ar.edu.itu.biblioteca.ui.prestamo.ConsultaPrestamosUI;
import ar.edu.itu.biblioteca.ui.prestamo.ConsultaVencimientosUI;
import ar.edu.itu.biblioteca.ui.prestamo.FlujoDevolucionUI;
import ar.edu.itu.biblioteca.ui.prestamo.FlujoPrestamoUI;

import java.util.Scanner;

public class MenuPrestamos {

    private final ConsolaUI ui;
    private final FlujoPrestamoUI flujoPrestamo;
    private final FlujoDevolucionUI flujoDevolucion;
    private final ConsultaPrestamosUI consultas;
    private final ConsultaVencimientosUI consultaVencimientos;


    public MenuPrestamos(
            Scanner scanner
    ) {

        this.ui =
                new ConsolaUI(
                        scanner
                );

        this.flujoPrestamo =
                new FlujoPrestamoUI(
                        scanner
                );

        this.flujoDevolucion =
                new FlujoDevolucionUI(
                        scanner
                );

        this.consultas =
                new ConsultaPrestamosUI(
                        scanner
                );

        this.consultaVencimientos =
                new ConsultaVencimientosUI(
                        scanner
                );
    }


    public void iniciar() {

        int opcion;

        do {

            ui.limpiarPantalla();

            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║            GESTION DE PRESTAMOS             ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println(
                    "║  1. Registrar devolucion                    ║"
            );

            System.out.println(
                    "║  2. Prestamos vencidos                      ║"
            );

            System.out.println(
                    "║                                              ║"
            );

            System.out.println(
                    "║  0. Volver                                  ║"
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

                    flujoDevolucion
                            .registrarDevolucion();

                    break;

                case 2:

                    consultaVencimientos
                            .mostrarPrestamosVencidos();

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


    public void registrarPrestamo(
            Usuario usuario
    ) {

        flujoPrestamo.registrarPrestamo(
                usuario
        );
    }


    public void mostrarPrestamosActivos(
            Usuario usuario
    ) {

        consultas.mostrarPrestamosActivos(
                usuario
        );
    }


    public void mostrarHistorialPrestamos(
            Usuario usuario
    ) {

        consultas.mostrarHistorialPrestamos(
                usuario
        );
    }


    public void mostrarPrestamosActivosPorMaterial(
            MaterialBibliografico material
    ) {

        consultas.mostrarPrestamosActivosPorMaterial(
                material
        );
    }


    public void mostrarHistorialPorMaterial(
            MaterialBibliografico material
    ) {

        consultas.mostrarHistorialPorMaterial(
                material
        );
    }
}