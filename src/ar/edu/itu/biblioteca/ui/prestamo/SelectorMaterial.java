package ar.edu.itu.biblioteca.ui.prestamo;

import ar.edu.itu.biblioteca.ui.ConsolaUI;

import ar.edu.itu.biblioteca.dao.MaterialDAO;
import ar.edu.itu.biblioteca.model.MaterialBibliografico;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class SelectorMaterial {

    private final Scanner scanner;
    private final ConsolaUI ui;
    private final MaterialDAO materialDAO;


    public SelectorMaterial(
            Scanner scanner
    ) {

        this.scanner = scanner;
        this.ui = new ConsolaUI(scanner);
        this.materialDAO = new MaterialDAO();
    }


    public MaterialBibliografico seleccionarParaPrestamo()
            throws SQLException {

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

            List<MaterialBibliografico> materiales =
                    materialDAO.buscarCoincidencias(
                            texto
                    );

            if (materiales.isEmpty()) {

                ui.mostrarMensaje(
                        "No se encontraron materiales coincidentes."
                );

                continue;
            }

            if (materiales.size() == 1) {
                return materiales.get(0);
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
                            materiales.get(i);

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

                if (opcion == 0) {
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
}
