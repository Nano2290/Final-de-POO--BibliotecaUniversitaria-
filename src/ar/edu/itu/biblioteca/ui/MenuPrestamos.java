package ar.edu.itu.biblioteca.ui;

import ar.edu.itu.biblioteca.dao.MaterialDAO;
import ar.edu.itu.biblioteca.dao.PrestamoDAO;
import ar.edu.itu.biblioteca.exception.BibliotecaException;
import ar.edu.itu.biblioteca.model.MaterialBibliografico;
import ar.edu.itu.biblioteca.model.MotivoSuspension;
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
    private final ConsolaUI ui;

    public MenuPrestamos(
            Scanner scanner
    ) {

        this.scanner =
                scanner;

        this.ui =
                new ConsolaUI(
                        scanner
                );
    }

    public void iniciar() {

        int opcion;

        do {

            ui.limpiarPantalla();

            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║             GESTION DE PRESTAMOS            ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println(
                    "║  1. Registrar devolucion                    ║"
            );

            System.out.println(
                    "║  2. Prestamos vencidos                      ║"
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

            System.out.print(
                    "\nSeleccione una opcion: "
            );


            opcion =
                    ui.leerEntero();


            switch (opcion) {

                case 1:

                    ui.mostrarMensaje(
                            "Registro de devoluciones: pendiente de implementar."
                    );

                    break;


                case 2:

                    ui.mostrarMensaje(
                            "Prestamos vencidos: pendiente de implementar."
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

        } while (
                opcion != 0
        );
    }


    public void registrarPrestamo(
            Usuario usuario
    ) {

        ui.limpiarPantalla();


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
                "Usuario: "
                        + usuario.getNombre()
                        + " "
                        + usuario.getApellido()
        );

        System.out.println(
                "DNI: "
                        + usuario.getDni()
        );

        System.out.println(
                "Estado: "
                        + (
                                usuario.isActivo()
                                        ? "ACTIVO"
                                        : "SUSPENDIDO"
                        )
        );


        PrestamoDAO prestamoDAO =
                new PrestamoDAO();


        int prestamosActivos;


        try {

            prestamosActivos =
                    prestamoDAO
                            .listarPrestamosActivosPorUsuario(
                                    usuario.getId()
                            )
                            .size();


        } catch (SQLException e) {

            ui.mostrarMensaje(
                    "No se pudo consultar el estado de prestamos del usuario: "
                            + e.getMessage()
            );

            return;
        }


        /*
         * Antes de pedir un material verificamos si el usuario
         * ya tiene alguna condicion que impida el prestamo.
         *
         * El Service volvera a validar estas reglas al confirmar.
         * Esta validacion previa existe para mejorar la experiencia
         * de navegacion y no hacer buscar un material innecesariamente.
         */
        if (
                !usuario.isActivo()
        ) {

            mostrarBloqueoUsuarioSuspendido(
                    usuario,
                    prestamosActivos
            );

            return;
        }


        if (
                prestamosActivos
                        >= usuario.obtenerLimitePrestamos()
        ) {

            mostrarBloqueoLimitePrestamos(
                    usuario,
                    prestamosActivos
            );

            return;
        }


        System.out.println();

        System.out.println(
                "Usuario habilitado para solicitar prestamos."
        );

        System.out.println(
                "Prestamos activos: "
                        + prestamosActivos
                        + " de "
                        + usuario.obtenerLimitePrestamos()
        );


        MaterialDAO materialDAO =
                new MaterialDAO();

        PrestamoService prestamoService =
                new PrestamoService();


        try {

            MaterialBibliografico material =
                    seleccionarMaterialParaPrestamo(
                            materialDAO
                    );


            if (
                    material == null
            ) {

                return;
            }


            Prestamo prestamo =
                    prestamoService
                            .registrarPrestamo(
                                    0,
                                    usuario,
                                    material,
                                    prestamosActivos
                            );


            ui.limpiarPantalla();


            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║            CONFIRMAR PRESTAMO               ║"
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
                    "║ DNI:     %-33s║%n",
                    usuario.getDni()
            );

            System.out.printf(
                    "║ Material:%-33s║%n",
                    ui.recortarTexto(
                            material.getTitulo(),
                            33
                    )
            );

            System.out.printf(
                    "║ Codigo:  %-33s║%n",
                    material.getCodigo()
            );

            System.out.printf(
                    "║ Tipo:    %-33s║%n",
                    material.obtenerTipoMaterial()
            );

            System.out.printf(
                    "║ Inicio:  %-33s║%n",
                    formatearFecha(
                            prestamo.getFechaInicio()
                    )
            );

            System.out.printf(
                    "║ Vence:   %-33s║%n",
                    formatearFecha(
                            prestamo.getFechaVencimiento()
                    )
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println(
                    "║  1. Confirmar                               ║"
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
                    confirmacion != 1
            ) {

                ui.mostrarMensaje(
                        "Opcion invalida."
                );

                return;
            }


            prestamoDAO.registrarPrestamo(
                    prestamo
            );


            ui.mostrarMensaje(
                    "Prestamo registrado correctamente."
            );


        } catch (BibliotecaException e) {

            mostrarErrorPrestamoContextual(
                    usuario,
                    e.getMessage()
            );


        } catch (SQLException e) {

            ui.mostrarMensaje(
                    "No se pudo registrar el prestamo: "
                            + e.getMessage()
            );
        }
    }


    private void mostrarBloqueoUsuarioSuspendido(
            Usuario usuario,
            int prestamosActivos
    ) {

        MotivoSuspension motivo =
                usuario.getMotivoSuspension() == null
                        ? MotivoSuspension.SIN_ESPECIFICAR
                        : usuario.getMotivoSuspension();


        int opcion;

        do {

            ui.limpiarPantalla();


            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║          PRESTAMO NO DISPONIBLE             ║"
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
                    "║ Estado:  %-33s║%n",
                    "SUSPENDIDO"
            );

            System.out.printf(
                    "║ Motivo:  %-33s║%n",
                    motivo.name()
            );

            System.out.printf(
                    "║ Prestamos activos: %-24d║%n",
                    prestamosActivos
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );


            if (
                    motivo == MotivoSuspension.DEUDA
            ) {

                System.out.println(
                        "║ La suspension corresponde a deuda pendiente.║"
                );

                System.out.println(
                        "║ No se puede registrar un nuevo prestamo.    ║"
                );

                System.out.println(
                        "╠══════════════════════════════════════════════╣"
                );

                System.out.println(
                        "║  1. Ver multas                              ║"
                );

                System.out.println(
                        "║  2. Ver prestamos activos                   ║"
                );

                System.out.println(
                        "║  3. Ver historial de prestamos              ║"
                );

            } else {

                System.out.println(
                        "║ La suspension no corresponde a deuda.       ║"
                );

                System.out.println(
                        "║ Revise el estado desde la ficha del usuario.║"
                );

                System.out.println(
                        "╠══════════════════════════════════════════════╣"
                );

                System.out.println(
                        "║  1. Ver prestamos activos                   ║"
                );

                System.out.println(
                        "║  2. Ver historial de prestamos              ║"
                );
            }


            System.out.println(
                    "║                                              ║"
            );

            System.out.println(
                    "║  0. Volver a la ficha                       ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════╝"
            );

            System.out.print(
                    "\nSeleccione una opcion: "
            );


            opcion =
                    ui.leerEntero();


            if (
                    motivo == MotivoSuspension.DEUDA
            ) {

                switch (opcion) {

                    case 1: {

                        MenuMultas menuMultas =
                                new MenuMultas(
                                        scanner
                                );

                        menuMultas.mostrarMultasDeUsuario(
                                usuario
                        );

                        break;
                    }


                    case 2:

                        mostrarPrestamosActivos(
                                usuario
                        );

                        break;


                    case 3:

                        mostrarHistorialPrestamos(
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

            } else {

                switch (opcion) {

                    case 1:

                        mostrarPrestamosActivos(
                                usuario
                        );

                        break;


                    case 2:

                        mostrarHistorialPrestamos(
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
            }

        } while (
                opcion != 0
        );
    }


    private void mostrarBloqueoLimitePrestamos(
            Usuario usuario,
            int prestamosActivos
    ) {

        int opcion;

        do {

            ui.limpiarPantalla();


            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║          LIMITE DE PRESTAMOS                ║"
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
                    "║ Prestamos activos: %-24d║%n",
                    prestamosActivos
            );

            System.out.printf(
                    "║ Limite permitido: %-24d║%n",
                    usuario.obtenerLimitePrestamos()
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println(
                    "║ El usuario alcanzo su limite de prestamos.  ║"
            );

            System.out.println(
                    "║ Debe devolver material antes de continuar.  ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println(
                    "║  1. Ver prestamos activos                   ║"
            );

            System.out.println(
                    "║  2. Ver historial de prestamos              ║"
            );

            System.out.println(
                    "║                                              ║"
            );

            System.out.println(
                    "║  0. Volver a la ficha                       ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════╝"
            );

            System.out.print(
                    "\nSeleccione una opcion: "
            );


            opcion =
                    ui.leerEntero();


            switch (opcion) {

                case 1:

                    mostrarPrestamosActivos(
                            usuario
                    );

                    break;


                case 2:

                    mostrarHistorialPrestamos(
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

        } while (
                opcion != 0
        );
    }


    private void mostrarErrorPrestamoContextual(
            Usuario usuario,
            String mensaje
    ) {

        int opcion;

        do {

            ui.limpiarPantalla();


            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║            PRESTAMO RECHAZADO               ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println();

            System.out.println(
                    mensaje
            );

            System.out.println();

            System.out.println(
                    "1. Intentar con otro material"
            );

            System.out.println(
                    "2. Ver prestamos activos del usuario"
            );

            System.out.println(
                    "0. Volver a la ficha"
            );

            System.out.print(
                    "\nSeleccione una opcion: "
            );


            opcion =
                    ui.leerEntero();


            switch (opcion) {

                case 1:

                    registrarPrestamo(
                            usuario
                    );

                    return;


                case 2:

                    mostrarPrestamosActivos(
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

        } while (
                opcion != 0
        );
    }


    private MaterialBibliografico seleccionarMaterialParaPrestamo(
            MaterialDAO materialDAO
    ) throws SQLException {

        while (true) {

            System.out.println();

            System.out.println(
                    "Buscar material por codigo o titulo."
            );

            System.out.println(
                    "Puede escribir solo una parte del titulo."
            );

            System.out.println(
                    "Ingrese 0 para cancelar."
            );

            System.out.print(
                    "\nBuscar material: "
            );


            String texto =
                    scanner.nextLine()
                            .trim();


            if (
                    texto.equals("0")
            ) {

                return null;
            }


            if (
                    texto.isEmpty()
            ) {

                ui.mostrarMensaje(
                        "Debe ingresar un criterio de busqueda."
                );

                continue;
            }


            List<MaterialBibliografico> materiales =
                    materialDAO
                            .buscarCoincidencias(
                                    texto
                            );


            if (
                    materiales.isEmpty()
            ) {

                ui.mostrarMensaje(
                        "No se encontraron materiales coincidentes."
                );

                continue;
            }


            if (
                    materiales.size() == 1
            ) {

                return materiales.get(
                        0
                );
            }


            while (true) {

                ui.limpiarPantalla();


                System.out.println(
                        "╔══════════════════════════════════════════════════════════════════════════════╗"
                );

                System.out.println(
                        "║                    SELECCIONAR MATERIAL PARA PRESTAMO                     ║"
                );

                System.out.println(
                        "╚══════════════════════════════════════════════════════════════════════════════╝"
                );

                System.out.println();

                System.out.println(
                        "--------------------------------------------------------------------------------"
                );

                System.out.printf(
                        "%-4s %-12s %-32s %-12s %-10s%n",
                        "Nro",
                        "Codigo",
                        "Titulo",
                        "Tipo",
                        "Dispon."
                );

                System.out.println(
                        "--------------------------------------------------------------------------------"
                );


                for (
                        int i = 0;
                        i < materiales.size();
                        i++
                ) {

                    MaterialBibliografico material =
                            materiales.get(
                                    i
                            );


                    System.out.printf(
                            "%-4d %-12s %-32s %-12s %-10d%n",
                            i + 1,
                            material.getCodigo(),
                            ui.recortarTexto(
                                    material.getTitulo(),
                                    32
                            ),
                            material.obtenerTipoMaterial(),
                            material.getCantidadDisponible()
                    );
                }


                System.out.println(
                        "--------------------------------------------------------------------------------"
                );

                System.out.println();

                System.out.println(
                        "0. Cancelar"
                );

                System.out.print(
                        "\nSeleccione un material: "
                );


                int opcion =
                        ui.leerEntero();


                if (
                        opcion == 0
                ) {

                    return null;
                }


                if (
                        opcion < 1
                                || opcion > materiales.size()
                ) {

                    ui.mostrarMensaje(
                            "Seleccion invalida."
                    );

                    continue;
                }


                return materiales.get(
                        opcion - 1
                );
            }
        }
    }


    public void mostrarPrestamosActivos(
            Usuario usuario
    ) {

        ui.limpiarPantalla();

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

                ui.mostrarMensaje(
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

            System.out.println(
                    "--------------------------------------------------------------------------------------"
            );

            ui.pausar();

        } catch (SQLException e) {

            ui.mostrarMensaje(
                    "Error al consultar los prestamos activos: "
                            + e.getMessage()
            );
        }
    }

    public void mostrarHistorialPrestamos(
            Usuario usuario
    ) {

        ui.limpiarPantalla();

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

                ui.mostrarMensaje(
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

            System.out.println(
                    "----------------------------------------------------------------------------------------------------"
            );

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

                ui.mostrarMensaje(
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

            System.out.println(
                    "------------------------------------------------------------------------------------------------"
            );

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

                ui.mostrarMensaje(
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

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------"
            );

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
