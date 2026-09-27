package ar.edu.itu.biblioteca.ui;

import java.util.Scanner;

public class MenuPrincipal {

    private final Scanner scanner;
    private final ConsolaUI ui;

    public MenuPrincipal() {

        scanner =
                new Scanner(
                        System.in
                );

        ui =
                new ConsolaUI(
                        scanner
                );
    }

    public void iniciar() {

        int opcion;

        do {

            ui.limpiarPantalla();

            mostrarEncabezado();

            System.out.println(
                    "║  1. Gestion de usuarios                     ║"
            );

            System.out.println(
                    "║  2. Gestion de materiales                   ║"
            );

            System.out.println(
                    "║  3. Gestion de prestamos                    ║"
            );

            System.out.println(
                    "║  4. Gestion de multas                       ║"
            );

            System.out.println(
                    "║                                              ║"
            );

            System.out.println(
                    "║  0. Salir                                   ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════╝"
            );

            System.out.print(
                    "\nSeleccione una opcion: "
            );

            opcion =
                    ui.leerEntero();

            procesarOpcion(
                    opcion
            );

        } while (
                opcion != 0
        );

        scanner.close();
    }

    private void mostrarEncabezado() {

        System.out.println(
                "╔══════════════════════════════════════════════╗"
        );

        System.out.println(
                "║       BIBLIOTECA UNIVERSITARIA - ITU        ║"
        );

        System.out.println(
                "╠══════════════════════════════════════════════╣"
        );
    }

    private void procesarOpcion(
            int opcion
    ) {

        switch (opcion) {

            case 1: {

                MenuUsuarios menuUsuarios =
                        new MenuUsuarios(
                                scanner
                        );

                menuUsuarios.iniciar();

                break;
            }

            case 2: {

                MenuMateriales menuMateriales =
                        new MenuMateriales(
                                scanner
                        );

                menuMateriales.iniciar();

                break;
            }

            case 3: {

                MenuPrestamos menuPrestamos =
                        new MenuPrestamos(
                                scanner
                        );

                menuPrestamos.iniciar();

                break;
            }

            case 4:

                ui.mostrarMensaje(
                        "La gestion global de multas se completara en el siguiente bloque."
                );

                break;

            case 0:

                System.out.println();

                System.out.println(
                        "Gracias por utilizar Biblioteca Universitaria."
                );

                break;

            default:

                ui.mostrarMensaje(
                        "Opcion invalida. Intente nuevamente."
                );

                break;
        }
    }
}
