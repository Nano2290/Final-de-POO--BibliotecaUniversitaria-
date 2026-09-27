package ar.edu.itu.biblioteca.ui;

import ar.edu.itu.biblioteca.dao.MaterialDAO;
import ar.edu.itu.biblioteca.dao.PrestamoDAO;
import ar.edu.itu.biblioteca.dao.UsuarioDAO;
import ar.edu.itu.biblioteca.exception.BibliotecaException;
import ar.edu.itu.biblioteca.model.MaterialBibliografico;
import ar.edu.itu.biblioteca.model.Prestamo;
import ar.edu.itu.biblioteca.model.PrestamoMaterialResumen;
import ar.edu.itu.biblioteca.model.PrestamoResumen;
import ar.edu.itu.biblioteca.model.Usuario;
import ar.edu.itu.biblioteca.service.PrestamoService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class MenuPrestamos {

    private final Scanner scanner;

    public MenuPrestamos(Scanner scanner) {
        this.scanner = scanner;
    }

    public void registrarPrestamo() {

        limpiarPantalla();

        System.out.println(
                "╔══════════════════════════════════════════════╗"
        );

        System.out.println(
                "║              REGISTRAR PRESTAMO             ║"
        );

        System.out.println(
                "╚══════════════════════════════════════════════╝"
        );

        System.out.println();

        System.out.println(
                "Ingrese 0 para cancelar."
        );

        System.out.print(
                "\nDNI del usuario: "
        );


        String dni =
                scanner.nextLine()
                        .trim();


        if (
                dni.equals("0")
        ) {

            return;
        }


        UsuarioDAO usuarioDAO =
                new UsuarioDAO();

        MaterialDAO materialDAO =
                new MaterialDAO();

        PrestamoDAO prestamoDAO =
                new PrestamoDAO();

        PrestamoService prestamoService =
                new PrestamoService();


        try {

            Usuario usuario =
                    usuarioDAO.buscarPorDni(
                            dni
                    );


            if (
                    usuario == null
            ) {

                mostrarMensaje(
                        "No existe un usuario con ese DNI."
                );

                return;
            }


            System.out.println();

            System.out.println(
                    "Usuario: "
                            + usuario.getNombre()
                            + " "
                            + usuario.getApellido()
            );

            System.out.println(
                    "Estado: "
                            + (
                                    usuario.isActivo()
                                            ? "ACTIVO"
                                            : "SUSPENDIDO"
                            )
            );


            System.out.print(
                    "\nCodigo del material: "
            );


            String codigo =
                    scanner.nextLine()
                            .trim()
                            .toUpperCase();


            if (
                    codigo.equals("0")
            ) {

                return;
            }


            MaterialBibliografico material =
                    materialDAO.buscarPorCodigo(
                            codigo
                    );


            if (
                    material == null
            ) {

                mostrarMensaje(
                        "No existe un material con ese codigo."
                );

                return;
            }


            System.out.println();

            System.out.println(
                    "Material: "
                            + material.getTitulo()
            );

            System.out.println(
                    "Tipo: "
                            + material.obtenerTipoMaterial()
            );

            System.out.println(
                    "Disponibles: "
                            + material.getCantidadDisponible()
            );


            int prestamosActivos =
                    prestamoDAO
                            .listarPrestamosActivosPorUsuario(
                                    usuario.getId()
                            )
                            .size();


            /*
             * El Service aplica las reglas de negocio:
             * - usuario existente
             * - usuario activo
             * - limite de prestamos
             * - material existente
             * - disponibilidad
             * - compatibilidad usuario/material
             */
            Prestamo prestamo =
                    prestamoService
                            .registrarPrestamo(
                                    0,
                                    usuario,
                                    material,
                                    prestamosActivos
                            );


            System.out.println();

            System.out.println(
                    "Fecha de inicio: "
                            + formatearFecha(
                                    prestamo.getFechaInicio()
                            )
            );

            System.out.println(
                    "Fecha de vencimiento: "
                            + formatearFecha(
                                    prestamo.getFechaVencimiento()
                            )
            );

            System.out.println();

            System.out.println(
                    "1. Confirmar prestamo"
            );

            System.out.println(
                    "0. Cancelar"
            );

            System.out.print(
                    "\nSeleccione una opcion: "
            );


            int confirmacion =
                    leerEntero();


            if (
                    confirmacion == 0
            ) {

                return;
            }


            if (
                    confirmacion != 1
            ) {

                mostrarMensaje(
                        "Opcion invalida."
                );

                return;
            }


            /*
             * El DAO persiste el prestamo y actualiza
             * la disponibilidad dentro de una transaccion.
             */
            prestamoDAO.registrarPrestamo(
                    prestamo
            );


            mostrarMensaje(
                    "Prestamo registrado correctamente."
            );


        } catch (BibliotecaException e) {

            mostrarMensaje(
                    e.getMessage()
            );

        } catch (SQLException e) {

            mostrarMensaje(
                    "No se pudo registrar el prestamo: "
                            + e.getMessage()
            );
        }
    }


    public void mostrarPrestamosActivos(
            Usuario usuario
    ) {

        limpiarPantalla();

        System.out.println(
                "╔══════════════════════════════════════════════════════════════════════════════╗"
        );

        System.out.println(
                "║                         PRESTAMOS ACTIVOS                                  ║"
        );

        System.out.println(
                "╚══════════════════════════════════════════════════════════════════════════════╝"
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

        PrestamoDAO prestamoDAO =
                new PrestamoDAO();

        try {

            List<PrestamoResumen> prestamos =
                    prestamoDAO.listarPrestamosActivosPorUsuario(
                            usuario.getId()
                    );

            if (prestamos.isEmpty()) {

                mostrarMensaje(
                        "El usuario no posee prestamos activos."
                );

                return;
            }

            System.out.println();

            System.out.println(
                    "--------------------------------------------------------------------------------------"
            );

            System.out.printf(
                    "%-12s %-30s %-12s %-12s %-12s%n",
                    "Codigo",
                    "Material",
                    "Inicio",
                    "Vence",
                    "Estado"
            );

            System.out.println(
                    "--------------------------------------------------------------------------------------"
            );

            for (PrestamoResumen prestamo : prestamos) {

                System.out.printf(
                        "%-12s %-30s %-12s %-12s %-12s%n",
                        prestamo.getCodigoMaterial(),
                        recortarTexto(
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

            System.out.println(
                    "--------------------------------------------------------------------------------------"
            );

            pausar();

        } catch (SQLException e) {

            mostrarMensaje(
                    "Error al consultar los prestamos activos: "
                            + e.getMessage()
            );
        }
    }

    public void mostrarHistorialPrestamos(
            Usuario usuario
    ) {

        limpiarPantalla();

        System.out.println(
                "╔══════════════════════════════════════════════════════════════════════════════╗"
        );

        System.out.println(
                "║                        HISTORIAL DE PRESTAMOS                              ║"
        );

        System.out.println(
                "╚══════════════════════════════════════════════════════════════════════════════╝"
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

        PrestamoDAO prestamoDAO =
                new PrestamoDAO();

        try {

            List<PrestamoResumen> historial =
                    prestamoDAO.listarHistorialPorUsuario(
                            usuario.getId()
                    );

            if (historial.isEmpty()) {

                mostrarMensaje(
                        "El usuario no posee prestamos registrados."
                );

                return;
            }

            System.out.println();

            System.out.println(
                    "----------------------------------------------------------------------------------------------------"
            );

            System.out.printf(
                    "%-12s %-30s %-12s %-12s %-12s %-12s%n",
                    "Codigo",
                    "Material",
                    "Inicio",
                    "Vence",
                    "Devolucion",
                    "Estado"
            );

            System.out.println(
                    "----------------------------------------------------------------------------------------------------"
            );

            for (PrestamoResumen prestamo : historial) {

                String devolucion =
                        formatearFecha(
                                prestamo.getFechaDevolucion()
                        );

                System.out.printf(
                        "%-12s %-30s %-12s %-12s %-12s %-12s%n",
                        prestamo.getCodigoMaterial(),
                        recortarTexto(
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

            System.out.println(
                    "----------------------------------------------------------------------------------------------------"
            );

            pausar();

        } catch (SQLException e) {

            mostrarMensaje(
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

            limpiarPantalla();

            System.out.println(
                    "╔══════════════════════════════════════════════════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║                               PRESTAMOS ACTIVOS                                         ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════════════════════════════════════════════════╝"
            );

            System.out.println();

            System.out.println(
                    "Material: "
                            + material.getTitulo()
            );

            System.out.println(
                    "Codigo:   "
                            + material.getCodigo()
            );

            if (prestamos.isEmpty()) {

                mostrarMensaje(
                        "Actualmente nadie tiene prestado este material."
                );

                return;
            }

            System.out.println();

            System.out.println(
                    "------------------------------------------------------------------------------------------------"
            );

            System.out.printf(
                    "%-4s %-24s %-12s %-14s %-14s %-12s%n",
                    "Nro",
                    "Usuario",
                    "DNI",
                    "Prestamo",
                    "Vencimiento",
                    "Estado"
            );

            System.out.println(
                    "------------------------------------------------------------------------------------------------"
            );

            for (
                    int i = 0;
                    i < prestamos.size();
                    i++
            ) {

                PrestamoMaterialResumen prestamo =
                        prestamos.get(i);

                System.out.printf(
                        "%-4d %-24s %-12s %-14s %-14s %-12s%n",
                        i + 1,
                        recortarTexto(
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

            System.out.println(
                    "------------------------------------------------------------------------------------------------"
            );

            pausar();

        } catch (SQLException e) {

            mostrarMensaje(
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

            limpiarPantalla();

            System.out.println(
                    "╔══════════════════════════════════════════════════════════════════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║                                      HISTORIAL DE PRESTAMOS                                             ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════════════════════════════════════════════════════════════════╝"
            );

            System.out.println();

            System.out.println(
                    "Material: "
                            + material.getTitulo()
            );

            System.out.println(
                    "Codigo:   "
                            + material.getCodigo()
            );

            if (historial.isEmpty()) {

                mostrarMensaje(
                        "Este material todavia no tiene prestamos registrados."
                );

                return;
            }

            System.out.println();

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------"
            );

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

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------"
            );

            for (
                    int i = 0;
                    i < historial.size();
                    i++
            ) {

                PrestamoMaterialResumen prestamo =
                        historial.get(i);

                System.out.printf(
                        "%-4d %-22s %-12s %-12s %-12s %-12s %-12s%n",
                        i + 1,
                        recortarTexto(
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

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------"
            );

            pausar();

        } catch (SQLException e) {

            mostrarMensaje(
                    "Error al consultar historial del material: "
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
                        scanner.nextLine()
                                .trim()
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
