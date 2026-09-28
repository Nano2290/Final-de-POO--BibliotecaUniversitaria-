package ar.edu.itu.biblioteca.ui.prestamo;

import ar.edu.itu.biblioteca.ui.ConsolaUI;

import ar.edu.itu.biblioteca.dao.PrestamoDAO;
import ar.edu.itu.biblioteca.model.MaterialBibliografico;
import ar.edu.itu.biblioteca.model.PrestamoMaterialResumen;
import ar.edu.itu.biblioteca.model.PrestamoResumen;
import ar.edu.itu.biblioteca.model.Usuario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ConsultaPrestamosUI {

    private final Scanner scanner;
    private final ConsolaUI ui;

    public ConsultaPrestamosUI(Scanner scanner) {
        this.scanner = scanner;
        this.ui = new ConsolaUI(scanner);
    }

    public void mostrarPrestamosActivos(Usuario usuario) {

        while (true) {

            ui.limpiarPantalla();

            System.out.println("╔════════════════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                                   PRESTAMOS ACTIVOS                                       ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════════════════════════════╝");

            System.out.println();
            System.out.println("Usuario: " + usuario.getNombre() + " " + usuario.getApellido());
            System.out.println("DNI: " + usuario.getDni());

            PrestamoDAO prestamoDAO = new PrestamoDAO();

            try {

                List<PrestamoResumen> prestamos =
                        prestamoDAO.listarPrestamosActivosPorUsuario(
                                usuario.getId()
                        );

                if (prestamos.isEmpty()) {
                    ui.mostrarMensaje("El usuario no posee prestamos activos.");
                    return;
                }

                System.out.println();
                System.out.println("--------------------------------------------------------------------------------------------");

                System.out.printf(
                        "%-4s %-12s %-30s %-12s %-12s %-12s%n",
                        "Nro",
                        "Codigo",
                        "Material",
                        "Inicio",
                        "Vence",
                        "Estado"
                );

                System.out.println("--------------------------------------------------------------------------------------------");

                for (int i = 0; i < prestamos.size(); i++) {

                    PrestamoResumen prestamo = prestamos.get(i);

                    System.out.printf(
                            "%-4d %-12s %-30s %-12s %-12s %-12s%n",
                            i + 1,
                            prestamo.getCodigoMaterial(),
                            ui.recortarTexto(
                                    prestamo.getTituloMaterial(),
                                    30
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

                System.out.println("--------------------------------------------------------------------------------------------");
                System.out.println();
                System.out.println("0. Volver");

                System.out.print("\nSeleccione un prestamo: ");

                int opcion = ui.leerEntero();

                if (opcion == 0) {
                    return;
                }

                if (opcion < 1 || opcion > prestamos.size()) {
                    ui.mostrarMensaje("Seleccion invalida.");
                    continue;
                }

                PrestamoResumen prestamoSeleccionado =
                        prestamos.get(opcion - 1);

                mostrarFichaPrestamoActivo(
                        usuario,
                        prestamoSeleccionado
                );

            } catch (SQLException e) {

                ui.mostrarMensaje(
                        "Error al consultar los prestamos activos: "
                                + e.getMessage()
                );

                return;
            }
        }
    }

    private void mostrarFichaPrestamoActivo(
            Usuario usuario,
            PrestamoResumen prestamo
    ) {

        while (true) {

            ui.limpiarPantalla();

            System.out.println("╔══════════════════════════════════════════════╗");
            System.out.println("║              PRESTAMO ACTIVO                ║");
            System.out.println("╠══════════════════════════════════════════════╣");

            System.out.printf(
                    "║ Material: %-32s║%n",
                    ui.recortarTexto(
                            prestamo.getTituloMaterial(),
                            32
                    )
            );

            System.out.printf(
                    "║ Codigo:   %-32s║%n",
                    prestamo.getCodigoMaterial()
            );

            System.out.printf(
                    "║ Prestado: %-32s║%n",
                    formatearFecha(
                            prestamo.getFechaInicio()
                    )
            );

            System.out.printf(
                    "║ Vence:    %-32s║%n",
                    formatearFecha(
                            prestamo.getFechaVencimiento()
                    )
            );

            System.out.printf(
                    "║ Estado:   %-32s║%n",
                    prestamo.obtenerEstado()
            );

            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  1. Registrar devolucion                    ║");
            System.out.println("║  0. Volver                                  ║");
            System.out.println("╚══════════════════════════════════════════════╝");

            System.out.print("\nSeleccione una opcion: ");

            int opcion = ui.leerEntero();

            switch (opcion) {

                case 1: {

                    FlujoDevolucionUI flujoDevolucion =
                            new FlujoDevolucionUI(
                                    scanner
                            );

                    flujoDevolucion.registrarDevolucion(
                            usuario,
                            prestamo
                    );

                    return;
                }

                case 0:
                    return;

                default:
                    ui.mostrarMensaje("Opcion invalida.");
                    break;
            }
        }
    }

    public void mostrarHistorialPrestamos(Usuario usuario) {

        ui.limpiarPantalla();

        System.out.println("╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                        HISTORIAL DE PRESTAMOS                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");

        System.out.println();
        System.out.println("Usuario: " + usuario.getNombre() + " " + usuario.getApellido());
        System.out.println("DNI: " + usuario.getDni());

        PrestamoDAO prestamoDAO = new PrestamoDAO();

        try {

            List<PrestamoResumen> historial =
                    prestamoDAO.listarHistorialPorUsuario(
                            usuario.getId()
                    );

            if (historial.isEmpty()) {
                ui.mostrarMensaje("El usuario no posee prestamos registrados.");
                return;
            }

            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------");

            System.out.printf(
                    "%-12s %-30s %-12s %-12s %-12s %-12s%n",
                    "Codigo",
                    "Material",
                    "Inicio",
                    "Vence",
                    "Devolucion",
                    "Estado"
            );

            System.out.println("----------------------------------------------------------------------------------------------------");

            for (PrestamoResumen prestamo : historial) {

                String devolucion =
                        formatearFecha(
                                prestamo.getFechaDevolucion()
                        );

                System.out.printf(
                        "%-12s %-30s %-12s %-12s %-12s %-12s%n",
                        prestamo.getCodigoMaterial(),
                        ui.recortarTexto(
                                prestamo.getTituloMaterial(),
                                30
                        ),
                        formatearFecha(
                                prestamo.getFechaInicio()
                        ),
                        formatearFecha(
                                prestamo.getFechaVencimiento()
                        ),
                        devolucion,
                        prestamo.obtenerEstado()
                );
            }

            System.out.println("----------------------------------------------------------------------------------------------------");
            ui.pausar();

        } catch (SQLException e) {

            ui.mostrarMensaje(
                    "Error al consultar el historial: "
                            + e.getMessage()
            );
        }
    }

    public void mostrarPrestamosActivosPorMaterial(
            MaterialBibliografico material
    ) {

        PrestamoDAO prestamoDAO =
                new PrestamoDAO();

        try {

            List<PrestamoMaterialResumen> prestamos =
                    prestamoDAO.listarPrestamosActivosPorMaterial(
                            material.getId()
                    );

            ui.limpiarPantalla();

            System.out.println("╔══════════════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                               PRESTAMOS ACTIVOS                                         ║");
            System.out.println("╚══════════════════════════════════════════════════════════════════════════════════════════╝");

            System.out.println();
            System.out.println("Material: " + material.getTitulo());
            System.out.println("Codigo:   " + material.getCodigo());

            if (prestamos.isEmpty()) {
                ui.mostrarMensaje("Actualmente nadie tiene prestado este material.");
                return;
            }

            System.out.println();
            System.out.println("------------------------------------------------------------------------------------------------");

            System.out.printf(
                    "%-4s %-24s %-12s %-14s %-14s %-12s%n",
                    "Nro",
                    "Usuario",
                    "DNI",
                    "Prestamo",
                    "Vencimiento",
                    "Estado"
            );

            System.out.println("------------------------------------------------------------------------------------------------");

            for (int i = 0; i < prestamos.size(); i++) {

                PrestamoMaterialResumen prestamo =
                        prestamos.get(i);

                System.out.printf(
                        "%-4d %-24s %-12s %-14s %-14s %-12s%n",
                        i + 1,
                        ui.recortarTexto(
                                prestamo.getNombreCompletoUsuario(),
                                24
                        ),
                        prestamo.getDniUsuario(),
                        formatearFecha(
                                prestamo.getFechaInicio()
                        ),
                        formatearFecha(
                                prestamo.getFechaVencimiento()
                        ),
                        prestamo.obtenerEstado()
                );
            }

            System.out.println("------------------------------------------------------------------------------------------------");
            ui.pausar();

        } catch (SQLException e) {

            ui.mostrarMensaje(
                    "Error al consultar prestamos activos: "
                            + e.getMessage()
            );
        }
    }

    public void mostrarHistorialPorMaterial(
            MaterialBibliografico material
    ) {

        PrestamoDAO prestamoDAO =
                new PrestamoDAO();

        try {

            List<PrestamoMaterialResumen> historial =
                    prestamoDAO.listarHistorialPorMaterial(
                            material.getId()
                    );

            ui.limpiarPantalla();

            System.out.println("╔══════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                                      HISTORIAL DE PRESTAMOS                                             ║");
            System.out.println("╚══════════════════════════════════════════════════════════════════════════════════════════════════════════╝");

            System.out.println();
            System.out.println("Material: " + material.getTitulo());
            System.out.println("Codigo:   " + material.getCodigo());

            if (historial.isEmpty()) {
                ui.mostrarMensaje("Este material todavia no tiene prestamos registrados.");
                return;
            }

            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------");

            System.out.printf(
                    "%-4s %-22s %-12s %-12s %-12s %-12s %-12s%n",
                    "Nro",
                    "Usuario",
                    "DNI",
                    "Inicio",
                    "Vence",
                    "Devolucion",
                    "Estado"
            );

            System.out.println("----------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < historial.size(); i++) {

                PrestamoMaterialResumen prestamo =
                        historial.get(i);

                System.out.printf(
                        "%-4d %-22s %-12s %-12s %-12s %-12s %-12s%n",
                        i + 1,
                        ui.recortarTexto(
                                prestamo.getNombreCompletoUsuario(),
                                22
                        ),
                        prestamo.getDniUsuario(),
                        formatearFecha(
                                prestamo.getFechaInicio()
                        ),
                        formatearFecha(
                                prestamo.getFechaVencimiento()
                        ),
                        formatearFecha(
                                prestamo.getFechaDevolucion()
                        ),
                        prestamo.obtenerEstado()
                );
            }

            System.out.println("----------------------------------------------------------------------------------------------------------");
            ui.pausar();

        } catch (SQLException e) {

            ui.mostrarMensaje(
                    "Error al consultar historial del material: "
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
