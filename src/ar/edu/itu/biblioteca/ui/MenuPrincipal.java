package ar.edu.itu.biblioteca.ui;

import java.util.Scanner;

public class MenuPrincipal {

    private final Scanner scanner;

    public MenuPrincipal() {
        scanner = new Scanner(System.in);
    }

    public void iniciar() {

        int opcion;

        do {

            limpiarPantalla();
            mostrarEncabezado();

            System.out.println("║  1. Gestion de usuarios                     ║");
            System.out.println("║  2. Gestion de materiales                   ║");
            System.out.println("║  3. Registrar prestamo                      ║");
            System.out.println("║  4. Registrar devolucion                    ║");
            System.out.println("║  5. Prestamos vencidos                      ║");
            System.out.println("║  6. Gestion de multas                       ║");
            System.out.println("║                                              ║");
            System.out.println("║  0. Salir                                   ║");
            System.out.println("╚══════════════════════════════════════════════╝");

            System.out.print("\nSeleccione una opcion: ");

            opcion = leerEntero();

            procesarOpcion(opcion);

        } while (opcion != 0);

        scanner.close();
    }

    private void mostrarEncabezado() {

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║       BIBLIOTECA UNIVERSITARIA - ITU        ║");
        System.out.println("╠══════════════════════════════════════════════╣");
    }

    private void procesarOpcion(int opcion) {

        switch (opcion) {

            case 1:

                 MenuUsuarios menuUsuarios =
                        new MenuUsuarios(scanner);

                 menuUsuarios.iniciar();

                break;

            case 2:

                 MenuMateriales menuMateriales =
                         new MenuMateriales(scanner);

                         menuMateriales.iniciar();

                 break;

            case 3:
                mostrarMensaje(
                        "Registro de prestamos."
                );
                break;

            case 4:
                mostrarMensaje(
                        "Registro de devoluciones."
                );
                break;

            case 5:
                mostrarMensaje(
                        "Consulta de prestamos vencidos."
                );
                break;

            case 6:
                mostrarMensaje(
                        "Gestion de multas."
                );
                break;

            case 0:

                System.out.println();
                System.out.println(
                        "Gracias por utilizar Biblioteca Universitaria."
                );

                break;

            default:

                mostrarMensaje(
                        "Opcion invalida. Intente nuevamente."
                );

                break;
        }
    }

    private int leerEntero() {

        while (true) {

            try {

                return Integer.parseInt(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                System.out.print(
                        "Ingrese un numero valido: "
                );
            }
        }
    }

    private void mostrarMensaje(String mensaje) {

        System.out.println();
        System.out.println("────────────────────────────────────────────────");
        System.out.println(mensaje);
        System.out.println("────────────────────────────────────────────────");

        pausar();
    }

    private void pausar() {

        System.out.println();
        System.out.print(
                "Presione ENTER para continuar..."
        );

        scanner.nextLine();
    }

    private void limpiarPantalla() {

        for (int i = 0; i < 30; i++) {
            System.out.println();
        }
    }
}