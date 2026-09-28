package ar.edu.itu.biblioteca.ui.prestamo;

import ar.edu.itu.biblioteca.ui.ConsolaUI;

import ar.edu.itu.biblioteca.dao.PrestamoDAO;
import ar.edu.itu.biblioteca.model.PrestamoResumen;
import ar.edu.itu.biblioteca.model.Usuario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class FlujoDevolucionUI {

    private final Scanner scanner;
    private final ConsolaUI ui;
    private final SelectorUsuario selectorUsuario;


    public FlujoDevolucionUI(
            Scanner scanner
    ) {

        this.scanner =
                scanner;

        this.ui =
                new ConsolaUI(
                        scanner
                );

        this.selectorUsuario =
                new SelectorUsuario(
                        scanner
                );
    }


    public void registrarDevolucion() {

        Usuario usuario =
                selectorUsuario.seleccionar();


        if (
                usuario == null
        ) {

            return;
        }


        PrestamoDAO prestamoDAO =
                new PrestamoDAO();


        List<PrestamoResumen> prestamosActivos;


        try {

            prestamosActivos =
                    prestamoDAO
                            .listarPrestamosActivosPorUsuario(
                                    usuario.getId()
                            );


        } catch (SQLException e) {

            ui.mostrarMensaje(
                    "No se pudieron consultar los prestamos activos: "
                            + e.getMessage()
            );

            return;
        }


        if (
                prestamosActivos.isEmpty()
        ) {

            ui.mostrarMensaje(
                    "El usuario no tiene prestamos activos para devolver."
            );

            return;
        }


        while (
                true
        ) {

            ui.limpiarPantalla();


            System.out.println(
                    "╔════════════════════════════════════════════════════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║                                   REGISTRAR DEVOLUCION                                    ║"
            );

            System.out.println(
                    "╚════════════════════════════════════════════════════════════════════════════════════════════╝"
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
                    "%-4s %-12s %-32s %-14s %-14s %-12s%n",
                    "Nro",
                    "Codigo",
                    "Material",
                    "Prestamo",
                    "Vencimiento",
                    "Estado"
            );

            System.out.println(
                    "--------------------------------------------------------------------------------------------"
            );


            for (
                    int i = 0;
                    i < prestamosActivos.size();
                    i++
            ) {

                PrestamoResumen prestamo =
                        prestamosActivos.get(
                                i
                        );


                System.out.printf(
                        "%-4d %-12s %-32s %-14s %-14s %-12s%n",
                        i + 1,
                        prestamo.getCodigoMaterial(),
                        ui.recortarTexto(
                                prestamo.getTituloMaterial(),
                                32
                        ),
                        formatearFecha(
                                prestamo.getFechaInicio()
                        ),
                        formatearFecha(
                                prestamo.getFechaVencimiento()
                        ),
                        prestamo.obtenerEstado()
                );
            }


            System.out.println(
                    "--------------------------------------------------------------------------------------------"
            );

            System.out.println();

            System.out.println(
                    "0. Volver"
            );

            System.out.print(
                    "\nSeleccione el prestamo a devolver: "
            );


            int opcion =
                    ui.leerEntero();


            if (
                    opcion == 0
            ) {

                return;
            }


            if (
                    opcion < 1
                            || opcion > prestamosActivos.size()
            ) {

                ui.mostrarMensaje(
                        "Seleccion invalida."
                );

                continue;
            }


            PrestamoResumen prestamoSeleccionado =
                    prestamosActivos.get(
                            opcion - 1
                    );


            LocalDate fechaDevolucion =
                    LocalDate.now();


            while (
                    true
            ) {

                long diasAtraso =
                        0;


                if (
                        fechaDevolucion.isAfter(
                                prestamoSeleccionado
                                        .getFechaVencimiento()
                        )
                ) {

                    diasAtraso =
                            java.time.temporal.ChronoUnit.DAYS
                                    .between(
                                            prestamoSeleccionado
                                                    .getFechaVencimiento(),
                                            fechaDevolucion
                                    );
                }


                double multaEstimada =
                        diasAtraso > 0
                                ? usuario.calcularMulta(
                                        (int) diasAtraso
                                )
                                : 0.0;


                ui.limpiarPantalla();


                System.out.println(
                        "╔══════════════════════════════════════════════╗"
                );

                System.out.println(
                        "║            CONFIRMAR DEVOLUCION             ║"
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
                        "║ Material: %-32s║%n",
                        ui.recortarTexto(
                                prestamoSeleccionado
                                        .getTituloMaterial(),
                                32
                        )
                );

                System.out.printf(
                        "║ Codigo:   %-32s║%n",
                        prestamoSeleccionado
                                .getCodigoMaterial()
                );

                System.out.printf(
                        "║ Inicio:   %-32s║%n",
                        formatearFecha(
                                prestamoSeleccionado
                                        .getFechaInicio()
                        )
                );

                System.out.printf(
                        "║ Vence:    %-32s║%n",
                        formatearFecha(
                                prestamoSeleccionado
                                        .getFechaVencimiento()
                        )
                );

                System.out.printf(
                        "║ Devuelve: %-32s║%n",
                        formatearFecha(
                                fechaDevolucion
                        )
                );


                if (
                        diasAtraso > 0
                ) {

                    System.out.printf(
                            "║ Atraso:   %-24s dias║%n",
                            diasAtraso
                    );

                    System.out.printf(
                            "║ Multa:    $%-31.2f║%n",
                            multaEstimada
                    );

                } else {

                    System.out.printf(
                            "║ Atraso:   %-32s║%n",
                            "SIN ATRASO"
                    );
                }


                System.out.println(
                        "╠══════════════════════════════════════════════╣"
                );

                System.out.println(
                        "║  1. Confirmar devolucion                    ║"
                );

                System.out.println(
                        "║  2. Cambiar fecha de devolucion             ║"
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
                        confirmacion == 2
                ) {

                    LocalDate nuevaFecha =
                            leerFechaDevolucion(
                                    prestamoSeleccionado
                                            .getFechaInicio()
                            );


                    if (
                            nuevaFecha != null
                    ) {

                        fechaDevolucion =
                                nuevaFecha;
                    }


                    continue;
                }


                if (
                        confirmacion != 1
                ) {

                    ui.mostrarMensaje(
                            "Opcion invalida."
                    );

                    continue;
                }


                try {

                    prestamoDAO.registrarDevolucion(
                            prestamoSeleccionado
                                    .getIdPrestamo(),
                            fechaDevolucion
                    );


                    ui.limpiarPantalla();


                    System.out.println(
                            "╔══════════════════════════════════════════════╗"
                    );

                    System.out.println(
                            "║          DEVOLUCION REGISTRADA              ║"
                    );

                    System.out.println(
                            "╠══════════════════════════════════════════════╣"
                    );

                    System.out.printf(
                            "║ Material: %-32s║%n",
                            ui.recortarTexto(
                                    prestamoSeleccionado
                                            .getTituloMaterial(),
                                    32
                            )
                    );

                    System.out.printf(
                            "║ Fecha:    %-32s║%n",
                            formatearFecha(
                                    fechaDevolucion
                            )
                    );


                    if (
                            diasAtraso > 0
                    ) {

                        System.out.printf(
                                "║ Atraso:   %-24s dias║%n",
                                diasAtraso
                        );

                        System.out.printf(
                                "║ Multa generada: $%-23.2f║%n",
                                multaEstimada
                        );

                    } else {

                        System.out.printf(
                                "║ Multa:    %-32s║%n",
                                "NO CORRESPONDE"
                        );
                    }


                    System.out.println(
                            "╚══════════════════════════════════════════════╝"
                    );

                    ui.pausar();

                    return;


                } catch (SQLException e) {

                    ui.mostrarMensaje(
                            "No se pudo registrar la devolucion: "
                                    + e.getMessage()
                    );

                    return;
                }
            }
        }
    }

    private LocalDate leerFechaDevolucion(
            LocalDate fechaInicio
    ) {

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                );


        while (
                true
        ) {

            ui.limpiarPantalla();


            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║       CAMBIAR FECHA DE DEVOLUCION           ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════╝"
            );

            System.out.println();

            System.out.println(
                    "Fecha de inicio del prestamo: "
                            + formatearFecha(
                                    fechaInicio
                            )
            );

            System.out.println(
                    "Fecha actual: "
                            + formatearFecha(
                                    LocalDate.now()
                            )
            );

            System.out.println();

            System.out.println(
                    "Ingrese la fecha en formato dd/MM/yyyy."
            );

            System.out.println(
                    "Ingrese 0 para cancelar el cambio."
            );

            System.out.print(
                    "\nNueva fecha: "
            );


            String texto =
                    scanner.nextLine()
                            .trim();


            if (
                    texto.equals(
                            "0"
                    )
            ) {

                return null;
            }


            try {

                LocalDate fecha =
                        LocalDate.parse(
                                texto,
                                formato
                        );


                if (
                        fecha.isBefore(
                                fechaInicio
                        )
                ) {

                    ui.mostrarMensaje(
                            "La fecha de devolucion no puede ser anterior "
                                    + "a la fecha de inicio del prestamo."
                    );

                    continue;
                }


                if (
                        fecha.isAfter(
                                LocalDate.now()
                        )
                ) {

                    ui.mostrarMensaje(
                            "La fecha de devolucion no puede ser futura."
                    );

                    continue;
                }


                return fecha;


            } catch (DateTimeParseException e) {

                ui.mostrarMensaje(
                        "Fecha invalida. Use el formato dd/MM/yyyy."
                );
            }
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
