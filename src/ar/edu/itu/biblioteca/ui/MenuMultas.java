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

    private final ConsolaUI ui;

    public MenuMultas(
            Scanner scanner
    ) {

        this.ui =
                new ConsolaUI(
                        scanner
                );
    }

    public void mostrarMultasDeUsuario(
            Usuario usuario
    ) {

        int opcion;

        do {

            ui.limpiarPantalla();

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
                    ui.leerEntero();

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

                    ui.mostrarMensaje(
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

            ui.mostrarMensaje(
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

            ui.mostrarMensaje(
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

        ui.limpiarPantalla();

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

            ui.mostrarMensaje(
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
                    ui.recortarTexto(
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

        ui.pausar();
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

                ui.mostrarMensaje(
                        "El usuario no posee multas pendientes."
                );

                return;
            }

            ui.limpiarPantalla();

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
                        ui.recortarTexto(
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
                    ui.leerEntero();

            if (seleccion == 0) {
                return;
            }

            if (
                    seleccion < 1
                    || seleccion > pendientes.size()
            ) {

                ui.mostrarMensaje(
                        "Seleccion invalida."
                );

                return;
            }

            MultaResumen multaSeleccionada =
                    pendientes.get(
                            seleccion - 1
                    );

            ui.limpiarPantalla();

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
                    ui.recortarTexto(
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
                    ui.leerEntero();

            if (confirmacion == 0) {
                return;
            }

            if (confirmacion != 1) {

                ui.mostrarMensaje(
                        "Opcion invalida."
                );

                return;
            }

            boolean actualizado =
                    multaDAO.marcarComoPagada(
                            multaSeleccionada.getIdMulta()
                    );

            if (actualizado) {

                ui.mostrarMensaje(
                        "Pago registrado correctamente."
                );

            } else {

                ui.mostrarMensaje(
                        "No se pudo registrar el pago."
                );
            }

        } catch (SQLException e) {

            ui.mostrarMensaje(
                    "Error al registrar el pago: "
                            + e.getMessage()
            );
        }
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
