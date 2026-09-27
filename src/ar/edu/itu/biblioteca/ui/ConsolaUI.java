package ar.edu.itu.biblioteca.ui;

import java.util.Scanner;

public class ConsolaUI {

    private final Scanner scanner;

    public ConsolaUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void mostrarTitulo(String titulo) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.printf("║ %-44s ║%n", recortarTexto(titulo, 44));
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println();
        System.out.println("────────────────────────────────────────────────");
        System.out.println(mensaje);
        System.out.println("────────────────────────────────────────────────");
        pausar();
    }

    public void pausar() {
        System.out.println();
        System.out.print("Presione ENTER para continuar...");
        scanner.nextLine();
    }

    public int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un numero valido: ");
            }
        }
    }

    public String recortarTexto(String texto, int longitudMaxima) {
        if (texto == null) {
            return "-";
        }

        if (texto.length() <= longitudMaxima) {
            return texto;
        }

        return texto.substring(0, longitudMaxima - 3) + "...";
    }

    public void limpiarPantalla() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls")
                        .inheritIO()
                        .start()
                        .waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 30; i++) {
                System.out.println();
            }
        }
    }
}
