package ar.edu.itu.biblioteca.ui;

import ar.edu.itu.biblioteca.ui.prestamo.ConsultaVencimientosUI;
import ar.edu.itu.biblioteca.ui.prestamo.FlujoDevolucionUI;

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

            mostrarMenuPrincipal();

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


    private void mostrarMenuPrincipal() {

        System.out.println(
                "╔══════════════════════════════════════════════╗"
        );

        System.out.println(
                "║       BIBLIOTECA UNIVERSITARIA - ITU        ║"
        );

        System.out.println(
                "╠══════════════════════════════════════════════╣"
        );

        System.out.println(
                "║              OPERACIONES DIARIAS            ║"
        );

        System.out.println(
                "║                                              ║"
        );

        System.out.println(
                "║  1. Registrar prestamo                      ║"
        );

        System.out.println(
                "║  2. Registrar devolucion                    ║"
        );

        System.out.println(
                "║  3. Buscar usuario                          ║"
        );

        System.out.println(
                "║  4. Buscar material                         ║"
        );

        System.out.println(
                "║                                              ║"
        );

        System.out.println(
                "║                  CONTROL                    ║"
        );

        System.out.println(
                "║                                              ║"
        );

        System.out.println(
                "║  5. Prestamos vencidos                      ║"
        );

        System.out.println(
                "║  6. Multas y deudas                         ║"
        );

        System.out.println(
                "║                                              ║"
        );

        System.out.println(
                "║              ADMINISTRACION                 ║"
        );

        System.out.println(
                "║                                              ║"
        );

        System.out.println(
                "║  7. Gestion de usuarios                     ║"
        );

        System.out.println(
                "║  8. Gestion de materiales                   ║"
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
    }


    private void procesarOpcion(
            int opcion
    ) {

        switch (
                opcion
        ) {

            /*
             * PRESTAMO
             *
             * Por ahora abrimos directamente la busqueda
             * del usuario.
             *
             * Desde la ficha del usuario se selecciona
             * "Registrar prestamo".
             *
             * En el siguiente paso vamos a hacer que esta
             * opcion entre directamente al flujo de prestamo.
             */
            case 1: {

                MenuUsuarios menuUsuarios =
                        new MenuUsuarios(
                                scanner
                        );

                menuUsuarios.buscarUsuario();

                break;
            }


            /*
             * DEVOLUCION
             *
             * Ya tenemos un flujo propio para buscar usuario,
             * listar sus prestamos y registrar la devolucion.
             */
            case 2: {

                FlujoDevolucionUI flujoDevolucion =
                        new FlujoDevolucionUI(
                                scanner
                        );

                flujoDevolucion.registrarDevolucion();

                break;
            }


            /*
             * CONSULTA RAPIDA DE USUARIO
             */
            case 3: {

                MenuUsuarios menuUsuarios =
                        new MenuUsuarios(
                                scanner
                        );

                menuUsuarios.buscarUsuario();

                break;
            }


            /*
             * CONSULTA RAPIDA DE MATERIAL
             */
            case 4: {

                MenuMateriales menuMateriales =
                        new MenuMateriales(
                                scanner
                        );

                menuMateriales.buscarMaterial();

                break;
            }


            /*
             * CONTROL DE PRESTAMOS VENCIDOS
             */
            case 5: {

                ConsultaVencimientosUI vencimientos =
                        new ConsultaVencimientosUI(
                                scanner
                        );

                vencimientos.mostrarPrestamosVencidos();

                break;
            }


            /*
             * MULTAS Y DEUDAS
             */
            case 6: {

                MenuMultas menuMultas =
                        new MenuMultas(
                                scanner
                        );

                menuMultas.iniciar();

                break;
            }


            /*
             * ADMINISTRACION DE USUARIOS
             */
            case 7: {

                MenuUsuarios menuUsuarios =
                        new MenuUsuarios(
                                scanner
                        );

                menuUsuarios.iniciar();

                break;
            }


            /*
             * ADMINISTRACION DE MATERIALES
             */
            case 8: {

                MenuMateriales menuMateriales =
                        new MenuMateriales(
                                scanner
                        );

                menuMateriales.iniciar();

                break;
            }


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