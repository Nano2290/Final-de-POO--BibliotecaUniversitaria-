package ar.edu.itu.biblioteca.ui;

import ar.edu.itu.biblioteca.dao.MultaDAO;
import ar.edu.itu.biblioteca.model.MultaResumen;
import ar.edu.itu.biblioteca.model.Usuario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class MenuMultas {

    private final Scanner scanner;

    public MenuMultas(Scanner scanner) {
        this.scanner = scanner;
    }

    public void mostrarMultasDeUsuario(
            Usuario usuario
    ) {

        int opcion;

        do {

            limpiarPantalla();

            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║              GESTION DE MULTAS              ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println(
                    "║  1. Ver todas las multas                    ║"
            );

            System.out.println(
                    "║  2. Ver multas pendientes                   ║"
            );

            System.out.println(
                    "║  3. Ver multas pagadas                      ║"
            );

            System.out.println(
                    "║  4. Registrar pago de multa                 ║"
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

            System.out.print(
                    "\nSeleccione opcion: "
            );

            opcion =
                    leerEntero();

            switch (opcion) {

                case 1:

                    mostrarTodasLasMultas(
                            usuario
                    );

                    break;

                case 2:

                    mostrarMultasPorEstado(
                            usuario,
                            false
                    );

                    break;

                case 3:

                    mostrarMultasPorEstado(
                            usuario,
                            true
                    );

                    break;

                case 4:

                    registrarPago(
                            usuario
                    );

                    break;

                case 0:
                    break;

                default:

                    mostrarMensaje(
                            "Opcion invalida."
                    );

                    break;
            }

        } while (opcion != 0);
    }


    private void mostrarTodasLasMultas(
            Usuario usuario
    ) {

        MultaDAO multaDAO =
                new MultaDAO();

        try {

            List<MultaResumen> multas =
                    multaDAO.listarPorUsuario(
                            usuario.getId()
                    );

            mostrarListado(
                    usuario,
                    multas,
                    "TODAS LAS MULTAS"
            );

        } catch (SQLException e) {

            mostrarMensaje(
                    "Error al consultar las multas: "
                            + e.getMessage()
            );
        }
    }


    private void mostrarMultasPorEstado(
            Usuario usuario,
            boolean pagada
    ) {

        MultaDAO multaDAO =
                new MultaDAO();

        try {

            List<MultaResumen> multas =
                    multaDAO.listarPorUsuarioYEstado(
                            usuario.getId(),
                            pagada
                    );

            String titulo =
                    pagada
                            ? "MULTAS PAGADAS"
                            : "MULTAS PENDIENTES";

            mostrarListado(
                    usuario,
                    multas,
                    titulo
            );

        } catch (SQLException e) {

            mostrarMensaje(
                    "Error al consultar las multas: "
                            + e.getMessage()
            );
        }
    }


    private void mostrarListado(
            Usuario usuario,
            List<MultaResumen> multas,
            String titulo
    ) {

        limpiarPantalla();

        System.out.println(
                "╔════════════════════════════════════════════════════════════════════════════════════╗"
        );

        System.out.printf(
                "║ %-82s║%n",
                titulo
        );

        System.out.println(
                "╚════════════════════════════════════════════════════════════════════════════════════╝"
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

        if (multas.isEmpty()) {

            mostrarMensaje(
                    "No hay multas para mostrar."
            );

            return;
        }

        System.out.println();

        System.out.println(
                "----------------------------------------------------------------------------------------------------"
        );

        System.out.printf(
                "%-4s %-12s %-28s %-12s %-12s %-12s %-12s%n",
                "N°",
                "Codigo",
                "Material",
                "Dias atraso",
                "Monto",
                "Fecha",
                "Estado"
        );

        System.out.println(
                "----------------------------------------------------------------------------------------------------"
        );

        double totalPendiente =
                0.0;

        for (int i = 0; i < multas.size(); i++) {

            MultaResumen multa =
                    multas.get(i);

            if (!multa.isPagada()) {

                totalPendiente +=
                        multa.getMonto();
            }

            System.out.printf(
                    "%-4d %-12s %-28s %-12d $%-11.2f %-12s %-12s%n",
                    i + 1,
                    multa.getCodigoMaterial(),
                    recortarTexto(
                            multa.getTituloMaterial(),
                            28
                    ),
                    multa.getDiasAtraso(),
                    multa.getMonto(),
                    formatearFecha(
                            multa.getFechaGeneracion()
                    ),
                    multa.obtenerEstado()
            );
        }

        System.out.println(
                "----------------------------------------------------------------------------------------------------"
        );

        System.out.printf(
                "Total pendiente: $%.2f%n",
                totalPendiente
        );

        pausar();
    }


    private void registrarPago(
            Usuario usuario
    ) {

        MultaDAO multaDAO =
                new MultaDAO();

        try {

            List<MultaResumen> pendientes =
                    multaDAO.listarPorUsuarioYEstado(
                            usuario.getId(),
                            false
                    );

            if (pendientes.isEmpty()) {

                mostrarMensaje(
                        "El usuario no posee multas pendientes."
                );

                return;
            }

            limpiarPantalla();

            System.out.println(
                    "╔════════════════════════════════════════════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║                         REGISTRAR PAGO DE MULTA                                  ║"
            );

            System.out.println(
                    "╚════════════════════════════════════════════════════════════════════════════════════╝"
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

            System.out.println();

            System.out.println(
                    "--------------------------------------------------------------------------------------------"
            );

            System.out.printf(
                    "%-4s %-12s %-28s %-12s %-12s%n",
                    "N°",
                    "Codigo",
                    "Material",
                    "Atraso",
                    "Monto"
            );

            System.out.println(
                    "--------------------------------------------------------------------------------------------"
            );

            for (int i = 0; i < pendientes.size(); i++) {

                MultaResumen multa =
                        pendientes.get(i);

                System.out.printf(
                        "%-4d %-12s %-28s %-12d $%-11.2f%n",
                        i + 1,
                        multa.getCodigoMaterial(),
                        recortarTexto(
                                multa.getTituloMaterial(),
                                28
                        ),
                        multa.getDiasAtraso(),
                        multa.getMonto()
                );
            }

            System.out.println(
                    "--------------------------------------------------------------------------------------------"
            );

            System.out.println();
            System.out.println(
                    "0. Cancelar"
            );

            System.out.print(
                    "\nSeleccione multa a pagar: "
            );

            int seleccion =
                    leerEntero();

            if (seleccion == 0) {
                return;
            }

            if (
                    seleccion < 1
                    || seleccion > pendientes.size()
            ) {

                mostrarMensaje(
                        "Seleccion invalida."
                );

                return;
            }

            MultaResumen multaSeleccionada =
                    pendientes.get(
                            seleccion - 1
                    );

            limpiarPantalla();

            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║             CONFIRMAR PAGO                  ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.printf(
                    "║ Material: %-32s║%n",
                    recortarTexto(
                            multaSeleccionada.getTituloMaterial(),
                            32
                    )
            );

            System.out.printf(
                    "║ Dias atraso: %-29d║%n",
                    multaSeleccionada.getDiasAtraso()
            );

            System.out.printf(
                    "║ Monto: $%-34.2f║%n",
                    multaSeleccionada.getMonto()
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println(
                    "║  1. Confirmar pago                          ║"
            );

            System.out.println(
                    "║  0. Cancelar                                ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════╝"
            );

            System.out.print(
                    "\nSeleccione opcion: "
            );

            int confirmacion =
                    leerEntero();

            if (confirmacion == 0) {
                return;
            }

            if (confirmacion != 1) {

                mostrarMensaje(
                        "Opcion invalida."
                );

                return;
            }

            boolean actualizado =
                    multaDAO.marcarComoPagada(
                            multaSeleccionada.getIdMulta()
                    );

            if (actualizado) {

                mostrarMensaje(
                        "Pago registrado correctamente."
                );

            } else {

                mostrarMensaje(
                        "No se pudo registrar el pago."
                );
            }

        } catch (SQLException e) {

            mostrarMensaje(
                    "Error al registrar el pago: "
                            + e.getMessage()
            );
        }
    }


    private String recortarTexto(
            String texto,
            int longitudMaxima
    ) {

        if (texto == null) {
            return "-";
        }

        if (texto.length() <= longitudMaxima) {
            return texto;
        }

        return texto.substring(
                0,
                longitudMaxima - 3
        ) + "...";
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


    private void mostrarMensaje(
            String mensaje
    ) {

        System.out.println();

        System.out.println(
                "────────────────────────────────────────────────"
        );

        System.out.println(
                mensaje
        );

        System.out.println(
                "────────────────────────────────────────────────"
        );

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

        try {

            if (System.getProperty("os.name")
                    .toLowerCase()
                    .contains("windows")) {

                new ProcessBuilder(
                        "cmd",
                        "/c",
                        "cls"
                )
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