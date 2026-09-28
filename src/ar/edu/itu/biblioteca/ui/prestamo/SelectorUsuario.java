package ar.edu.itu.biblioteca.ui.prestamo;

import ar.edu.itu.biblioteca.ui.ConsolaUI;

import ar.edu.itu.biblioteca.dao.UsuarioDAO;
import ar.edu.itu.biblioteca.model.Usuario;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class SelectorUsuario {

    private final Scanner scanner;
    private final ConsolaUI ui;
    private final UsuarioDAO usuarioDAO;


    public SelectorUsuario(
            Scanner scanner
    ) {

        this.scanner = scanner;
        this.ui = new ConsolaUI(scanner);
        this.usuarioDAO = new UsuarioDAO();
    }


    public Usuario seleccionar() {

        while (true) {

            ui.limpiarPantalla();

            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║               BUSCAR USUARIO                ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════╝"
            );

            System.out.println();
            System.out.println(
                    "Puede buscar por DNI, nombre o apellido."
            );
            System.out.println(
                    "Escriba 0 para volver."
            );
            System.out.print(
                    "\nBuscar: "
            );

            String texto =
                    scanner.nextLine().trim();

            if (texto.equals("0")) {
                return null;
            }

            if (texto.isEmpty()) {

                ui.mostrarMensaje(
                        "Debe ingresar un criterio de busqueda."
                );

                continue;
            }

            List<Usuario> coincidencias;

            try {

                coincidencias =
                        usuarioDAO.buscarCoincidencias(
                                texto
                        );

            } catch (SQLException e) {

                ui.mostrarMensaje(
                        "Error al buscar usuarios: "
                                + e.getMessage()
                );

                return null;
            }

            if (coincidencias.isEmpty()) {

                ui.mostrarMensaje(
                        "No se encontraron usuarios."
                );

                continue;
            }

            if (coincidencias.size() == 1) {
                return coincidencias.get(0);
            }

            ui.limpiarPantalla();

            System.out.println(
                    "╔════════════════════════════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║                    USUARIOS ENCONTRADOS                          ║"
            );

            System.out.println(
                    "╚════════════════════════════════════════════════════════════════════╝"
            );

            System.out.println();

            System.out.printf(
                    "%-4s %-12s %-24s %-12s%n",
                    "Nro",
                    "DNI",
                    "Usuario",
                    "Estado"
            );

            System.out.println(
                    "--------------------------------------------------------------------"
            );

            for (
                    int i = 0;
                    i < coincidencias.size();
                    i++
            ) {

                Usuario usuario =
                        coincidencias.get(i);

                System.out.printf(
                        "%-4d %-12s %-24s %-12s%n",
                        i + 1,
                        usuario.getDni(),
                        ui.recortarTexto(
                                usuario.getNombre()
                                        + " "
                                        + usuario.getApellido(),
                                24
                        ),
                        usuario.isActivo()
                                ? "ACTIVO"
                                : "SUSPENDIDO"
                );
            }

            System.out.println(
                    "--------------------------------------------------------------------"
            );

            System.out.println();
            System.out.println(
                    "0. Volver"
            );
            System.out.print(
                    "\nSeleccione un usuario: "
            );

            int opcion =
                    ui.leerEntero();

            if (opcion == 0) {
                return null;
            }

            if (
                    opcion < 1
                            || opcion > coincidencias.size()
            ) {

                ui.mostrarMensaje(
                        "Seleccion invalida."
                );

                continue;
            }

            return coincidencias.get(
                    opcion - 1
            );
        }
    }
}
