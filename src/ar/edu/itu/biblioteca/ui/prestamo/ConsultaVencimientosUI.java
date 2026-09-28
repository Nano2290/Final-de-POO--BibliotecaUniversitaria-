package ar.edu.itu.biblioteca.ui.prestamo;

import ar.edu.itu.biblioteca.model.PrestamoVencidoResumen;
import ar.edu.itu.biblioteca.service.VencimientoService;
import ar.edu.itu.biblioteca.ui.ConsolaUI;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ConsultaVencimientosUI {

    private final ConsolaUI ui;
    private final VencimientoService vencimientoService;


    public ConsultaVencimientosUI(
            Scanner scanner
    ) {

        this.ui =
                new ConsolaUI(
                        scanner
                );

        this.vencimientoService =
                new VencimientoService();
    }


    public void mostrarPrestamosVencidos() {

        LocalDate fechaReferencia =
                LocalDate.now();

        try {

            List<PrestamoVencidoResumen> prestamosVencidos =
                    vencimientoService
                            .listarPrestamosVencidos(
                                    fechaReferencia
                            );

            ui.limpiarPantalla();

            System.out.println(
                    "╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║                                           PRESTAMOS VENCIDOS                                               ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝"
            );

            System.out.println();

            System.out.println(
                    "Fecha de control: "
                            + formatearFecha(
                                    fechaReferencia
                            )
            );

            System.out.println();

            if (
                    prestamosVencidos.isEmpty()
            ) {

                ui.mostrarMensaje(
                        "No hay prestamos vencidos."
                );

                return;
            }

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------"
            );

            System.out.printf(
                    "%-4s %-24s %-12s %-28s %-12s %-10s %-14s%n",
                    "Nro",
                    "Usuario",
                    "DNI",
                    "Material",
                    "Vence",
                    "Atraso",
                    "Penalizacion"
            );

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------"
            );

            for (
                    int i = 0;
                    i < prestamosVencidos.size();
                    i++
            ) {

                PrestamoVencidoResumen prestamo =
                        prestamosVencidos.get(
                                i
                        );

                long diasAtraso =
                        vencimientoService
                                .calcularDiasAtraso(
                                        prestamo,
                                        fechaReferencia
                                );

                double penalizacionPendiente =
                        vencimientoService
                                .calcularPenalizacionPendiente(
                                        prestamo,
                                        fechaReferencia
                                );

                System.out.printf(
                        "%-4d %-24s %-12s %-28s %-12s %-10s $%-13.2f%n",
                        i + 1,
                        ui.recortarTexto(
                                prestamo
                                        .getUsuario()
                                        .getNombre()
                                        + " "
                                        + prestamo
                                        .getUsuario()
                                        .getApellido(),
                                24
                        ),
                        prestamo
                                .getUsuario()
                                .getDni(),
                        ui.recortarTexto(
                                prestamo.getTituloMaterial(),
                                28
                        ),
                        formatearFecha(
                                prestamo.getFechaVencimiento()
                        ),
                        diasAtraso + " dias",
                        penalizacionPendiente
                );
            }

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------"
            );

            System.out.println();

            System.out.println(
                    "La penalizacion mostrada es pendiente y se calcula con la fecha de control."
            );

            System.out.println(
                    "La multa se registra en la base de datos al confirmar la devolucion."
            );

            ui.pausar();

        } catch (SQLException e) {

            ui.mostrarMensaje(
                    "Error al consultar los prestamos vencidos: "
                            + e.getMessage()
            );
        }
    }


    private String formatearFecha(
            LocalDate fecha
    ) {

        if (
                fecha == null
        ) {

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
