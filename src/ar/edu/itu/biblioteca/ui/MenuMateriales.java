package ar.edu.itu.biblioteca.ui;

import ar.edu.itu.biblioteca.dao.MaterialDAO;

import ar.edu.itu.biblioteca.model.Libro;
import ar.edu.itu.biblioteca.model.MaterialBibliografico;
import ar.edu.itu.biblioteca.model.Revista;
import ar.edu.itu.biblioteca.model.Tesis;

import java.sql.SQLException;

import java.util.List;
import java.util.Scanner;

public class MenuMateriales {

    private final Scanner scanner;

    /*
     * Nos permite salir directamente desde una pantalla interna
     * hasta el menu principal.
     */
    private boolean volverMenuPrincipal = false;


    public MenuMateriales(
            Scanner scanner
    ) {

        this.scanner =
                scanner;
    }


    public void iniciar() {

        volverMenuPrincipal =
                false;

        int opcion;

        do {

            limpiarPantalla();

            mostrarMenu();

            System.out.print(
                    "\nSeleccione una opcion: "
            );

            opcion =
                    leerEntero();


            switch (opcion) {

                case 1:

                    registrarMaterial();

                    break;


                case 2:

                    buscarMaterial();

                    break;


                case 3:

                    listarTodos();

                    break;


                case 4:

                    listarPorDisponibilidad(
                            true
                    );

                    break;


                case 5:

                    listarPorDisponibilidad(
                            false
                    );

                    break;


                case 9:

                    volverMenuPrincipal =
                            true;

                    break;


                case 0:

                    break;


                default:

                    mostrarMensaje(
                            "Opcion invalida."
                    );

                    break;
            }

        } while (
                opcion != 0
                        && !volverMenuPrincipal
        );
    }


    private void mostrarMenu() {

        System.out.println(
                "╔══════════════════════════════════════════════╗"
        );

        System.out.println(
                "║            GESTION DE MATERIALES            ║"
        );

        System.out.println(
                "╠══════════════════════════════════════════════╣"
        );

        System.out.println(
                "║  1. Registrar material                      ║"
        );

        System.out.println(
                "║  2. Buscar por codigo o titulo              ║"
        );

        System.out.println(
                "║  3. Listar todos                            ║"
        );

        System.out.println(
                "║  4. Ver materiales disponibles              ║"
        );

        System.out.println(
                "║  5. Ver materiales no disponibles           ║"
        );

        System.out.println(
                "║                                              ║"
        );

        System.out.println(
                "║  0. Volver                                  ║"
        );

        System.out.println(
                "║  9. Menu principal                          ║"
        );

        System.out.println(
                "╚══════════════════════════════════════════════╝"
        );
    }


    private void registrarMaterial() {

        while (true) {

            limpiarPantalla();


            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║             REGISTRAR MATERIAL              ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════╝"
            );

            System.out.println();

            System.out.println(
                    "Escriba 0 en cualquier momento para cancelar."
            );

            System.out.println();


            /*
             * Primero elegimos el tipo para saber
             * que prefijo corresponde al codigo.
             */

            System.out.println(
                    "Tipo de material:"
            );

            System.out.println(
                    "1. Libro"
            );

            System.out.println(
                    "2. Revista"
            );

            System.out.println(
                    "3. Tesis"
            );

            System.out.println(
                    "0. Cancelar"
            );

            System.out.print(
                    "\nSeleccione tipo: "
            );


            int tipo =
                    leerEntero();


            if (tipo == 0) {

                return;
            }


            if (
                    tipo != 1
                            && tipo != 2
                            && tipo != 3
            ) {

                mostrarMensaje(
                        "Tipo de material invalido."
                );

                continue;
            }


            String tipoTexto;

            if (
                    tipo == 1
            ) {

                tipoTexto =
                        "LIBRO";

            } else if (
                    tipo == 2
            ) {

                tipoTexto =
                        "REVISTA";

            } else {

                tipoTexto =
                        "TESIS";
            }


            MaterialDAO materialDAO =
                    new MaterialDAO();


            String codigoSugerido;


            try {

                codigoSugerido =
                        materialDAO
                                .generarCodigoSugerido(
                                        tipoTexto
                                );

            } catch (SQLException e) {

                mostrarMensaje(
                        "No se pudo generar el codigo sugerido: "
                                + e.getMessage()
                );

                return;
            }


            System.out.println();

            System.out.println(
                    "Codigo sugerido: "
                            + codigoSugerido
            );

            System.out.println(
                    "Presione ENTER para usar el codigo sugerido."
            );

            System.out.println(
                    "O escriba otro codigo manualmente."
            );

            System.out.print(
                    "\nCodigo: "
            );


            String codigo =
                    scanner.nextLine()
                            .trim();


            if (
                    codigo.equals(
                            "0"
                    )
            ) {

                return;
            }


            /*
             * ENTER acepta el codigo sugerido.
             */

            if (
                    codigo.isEmpty()
            ) {

                codigo =
                        codigoSugerido;

            } else {

                codigo =
                        codigo.toUpperCase();
            }


            /*
             * Validamos que el codigo no exista.
             */

            try {

                MaterialBibliografico existente =
                        materialDAO
                                .buscarPorCodigo(
                                        codigo
                                );


                if (
                        existente != null
                ) {

                    mostrarMensaje(
                            "Ya existe un material registrado con ese codigo."
                    );

                    continue;
                }

            } catch (SQLException e) {

                mostrarMensaje(
                        "Error al verificar el codigo: "
                                + e.getMessage()
                );

                return;
            }


            System.out.println();

            System.out.print(
                    "Titulo: "
            );


            String titulo =
                    scanner.nextLine()
                            .trim();


            if (
                    titulo.equals(
                            "0"
                    )
            ) {

                return;
            }


            if (
                    titulo.isEmpty()
            ) {

                mostrarMensaje(
                        "El titulo es obligatorio."
                );

                continue;
            }


            System.out.println();

            System.out.print(
                    "Cantidad de ejemplares: "
            );


            int cantidad =
                    leerEntero();


            if (
                    cantidad == 0
            ) {

                return;
            }


            if (
                    cantidad < 1
            ) {

                mostrarMensaje(
                        "La cantidad debe ser mayor que cero."
                );

                continue;
            }


            limpiarPantalla();


            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║           CONFIRMAR NUEVO MATERIAL          ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.printf(
                    "║ Codigo:   %-34s║%n",
                    codigo
            );

            System.out.printf(
                    "║ Titulo:   %-34s║%n",
                    recortarTexto(
                            titulo,
                            34
                    )
            );

            System.out.printf(
                    "║ Tipo:     %-34s║%n",
                    tipoTexto
            );

            System.out.printf(
                    "║ Cantidad: %-34d║%n",
                    cantidad
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println(
                    "║  1. Confirmar                               ║"
            );

            System.out.println(
                    "║  2. Volver a cargar los datos               ║"
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


            if (
                    confirmacion == 0
            ) {

                return;
            }


            if (
                    confirmacion == 2
            ) {

                continue;
            }


            if (
                    confirmacion != 1
            ) {

                mostrarMensaje(
                        "Opcion invalida."
                );

                continue;
            }


            /*
             * Polimorfismo:
             * la variable es MaterialBibliografico,
             * pero el objeto concreto puede ser
             * Libro, Revista o Tesis.
             */

            MaterialBibliografico material;


            if (
                    tipo == 1
            ) {

                material =
                        new Libro(
                                0,
                                codigo,
                                titulo,
                                cantidad
                        );

            } else if (
                    tipo == 2
            ) {

                material =
                        new Revista(
                                0,
                                codigo,
                                titulo,
                                cantidad
                        );

            } else {

                material =
                        new Tesis(
                                0,
                                codigo,
                                titulo,
                                cantidad
                        );
            }


            try {

                materialDAO.guardar(
                        material
                );


                mostrarMensaje(
                        "Material registrado correctamente."
                );


                return;


            } catch (SQLException e) {

                if (
                        e.getMessage() != null
                                && e.getMessage()
                                .contains(
                                        "Duplicate entry"
                                )
                ) {

                    mostrarMensaje(
                            "Ya existe un material registrado con ese codigo."
                    );

                } else {

                    mostrarMensaje(
                            "No se pudo registrar el material: "
                                    + e.getMessage()
                    );
                }


                return;
            }
        }
    }


    private void buscarMaterial() {

        limpiarPantalla();


        System.out.println(
                "╔══════════════════════════════════════════════╗"
        );

        System.out.println(
                "║              BUSCAR MATERIAL                ║"
        );

        System.out.println(
                "╚══════════════════════════════════════════════╝"
        );


        System.out.println();

        System.out.println(
                "Puede buscar por:"
        );

        System.out.println(
                "- Codigo"
        );

        System.out.println(
                "- Titulo completo"
        );

        System.out.println(
                "- Parte del titulo"
        );

        System.out.println();

        System.out.print(
                "Ingrese busqueda: "
        );


        String texto =
                scanner.nextLine()
                        .trim();


        if (
                texto.isEmpty()
        ) {

            mostrarMensaje(
                    "Debe ingresar un codigo o titulo."
            );

            return;
        }


        MaterialDAO materialDAO =
                new MaterialDAO();


        try {

            List<MaterialBibliografico> materiales =
                    materialDAO
                            .buscarCoincidencias(
                                    texto
                            );


            if (
                    materiales.isEmpty()
            ) {

                mostrarMensaje(
                        "No se encontraron materiales coincidentes."
                );

                return;
            }


            /*
             * Reutilizamos el mismo listado seleccionable
             * que usamos en Listar todos.
             */
            mostrarListado(
                    materiales,
                    "RESULTADOS DE BUSQUEDA"
            );


        } catch (SQLException e) {

            mostrarMensaje(
                    "Error al buscar materiales: "
                            + e.getMessage()
            );
        }
    }


    private void listarTodos() {

        MaterialDAO materialDAO =
                new MaterialDAO();


        try {

            List<MaterialBibliografico> materiales =
                    materialDAO
                            .listarTodos();


            mostrarListado(
                    materiales,
                    "LISTADO DE MATERIALES"
            );


        } catch (SQLException e) {

            mostrarMensaje(
                    "Error al listar materiales: "
                            + e.getMessage()
            );
        }
    }


    private void listarPorDisponibilidad(
            boolean disponibles
    ) {

        MaterialDAO materialDAO =
                new MaterialDAO();


        try {

            List<MaterialBibliografico> todos =
                    materialDAO
                            .listarTodos();


            List<MaterialBibliografico> filtrados =
                    todos.stream()
                            .filter(
                                    material ->
                                            material
                                                    .estaDisponible()
                                                    == disponibles
                            )
                            .toList();


            String titulo =
                    disponibles
                            ? "MATERIALES DISPONIBLES"
                            : "MATERIALES NO DISPONIBLES";


            mostrarListado(
                    filtrados,
                    titulo
            );


        } catch (SQLException e) {

            mostrarMensaje(
                    "Error al consultar materiales: "
                            + e.getMessage()
            );
        }
    }


    private void mostrarListado(
            List<MaterialBibliografico> materiales,
            String titulo
    ) {

        while (
                !volverMenuPrincipal
        ) {

            limpiarPantalla();


            System.out.println(
                    "╔══════════════════════════════════════════════════════════════════════════════╗"
            );

            System.out.printf(
                    "║ %-76s║%n",
                    titulo
            );

            System.out.println(
                    "╚══════════════════════════════════════════════════════════════════════════════╝"
            );


            if (
                    materiales.isEmpty()
            ) {

                mostrarMensaje(
                        "No hay materiales para mostrar."
                );

                return;
            }


            System.out.println();

            System.out.println(
                    "--------------------------------------------------------------------------------"
            );

            System.out.printf(
                    "%-4s %-12s %-30s %-12s %-8s %-12s%n",
                    "Nro",
                    "Codigo",
                    "Titulo",
                    "Tipo",
                    "Total",
                    "Disponibles"
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
                        "%-4d %-12s %-30s %-12s %-8d %-12d%n",
                        i + 1,
                        material.getCodigo(),
                        recortarTexto(
                                material.getTitulo(),
                                30
                        ),
                        material.obtenerTipoMaterial(),
                        material.getCantidadTotal(),
                        material.getCantidadDisponible()
                );
            }


            System.out.println(
                    "--------------------------------------------------------------------------------"
            );

            System.out.println();

            System.out.println(
                    "0. Volver"
            );

            System.out.print(
                    "\nSeleccione un material: "
            );


            int opcion =
                    leerEntero();


            if (
                    opcion == 0
            ) {

                return;
            }


            if (
                    opcion < 1
                            || opcion > materiales.size()
            ) {

                mostrarMensaje(
                        "Seleccion invalida."
                );

                continue;
            }


            MaterialBibliografico materialSeleccionado =
                    materiales.get(
                            opcion - 1
                    );


            mostrarFichaMaterial(
                    materialSeleccionado
            );
        }
    }


    private void mostrarFichaMaterial(
            MaterialBibliografico material
    ) {

        while (
                !volverMenuPrincipal
        ) {

            limpiarPantalla();


            String disponibilidad =
                    material.estaDisponible()
                            ? "DISPONIBLE"
                            : "NO DISPONIBLE";


            int cantidadPrestada =
                    material.getCantidadTotal()
                            - material.getCantidadDisponible();


            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║              FICHA MATERIAL                 ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );


            System.out.printf(
                    "║ Codigo:      %-31s║%n",
                    material.getCodigo()
            );


            /*
             * En la ficha mostramos el titulo completo.
             * Si es largo se reparte en varias lineas.
             */

            mostrarTextoLargoEnFicha(
                    "Titulo:",
                    material.getTitulo()
            );


            System.out.printf(
                    "║ Tipo:        %-31s║%n",
                    material.obtenerTipoMaterial()
            );

            System.out.printf(
                    "║ Total:       %-31d║%n",
                    material.getCantidadTotal()
            );

            System.out.printf(
                    "║ Disponibles: %-31d║%n",
                    material.getCantidadDisponible()
            );

            System.out.printf(
                    "║ Prestados:   %-31d║%n",
                    cantidadPrestada
            );

            System.out.printf(
                    "║ Estado:      %-31s║%n",
                    disponibilidad
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
                    "║  3. Agregar ejemplares                      ║"
            );

            System.out.println(
                    "║  4. Quitar ejemplares                       ║"
            );

            System.out.println(
                    "║                                              ║"
            );

            System.out.println(
                    "║  0. Volver                                  ║"
            );

            System.out.println(
                    "║  9. Menu principal                          ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════╝"
            );


            System.out.print(
                    "\nSeleccione una opcion: "
            );


            int opcion =
                    leerEntero();


            switch (opcion) {

                case 1: {

                    MenuPrestamos menuPrestamos =
                            new MenuPrestamos(
                                    scanner
                            );

                    menuPrestamos
                            .mostrarPrestamosActivosPorMaterial(
                                    material
                            );

                    break;
                }


                case 2: {

                    MenuPrestamos menuPrestamos =
                            new MenuPrestamos(
                                    scanner
                            );

                    menuPrestamos
                            .mostrarHistorialPorMaterial(
                                    material
                            );

                    break;
                }


                case 3:

                    agregarEjemplares(
                            material
                    );

                    break;


                case 4:

                    quitarEjemplares(
                            material
                    );

                    break;


                case 0:

                    return;


                case 9:

                    volverMenuPrincipal =
                            true;

                    return;


                default:

                    mostrarMensaje(
                            "Opcion invalida."
                    );

                    break;
            }
        }
    }


    private void agregarEjemplares(
            MaterialBibliografico material
    ) {

        limpiarPantalla();


        System.out.println(
                "╔══════════════════════════════════════════════╗"
        );

        System.out.println(
                "║             AGREGAR EJEMPLARES              ║"
        );

        System.out.println(
                "╠══════════════════════════════════════════════╣"
        );

        System.out.printf(
                "║ Codigo:      %-31s║%n",
                material.getCodigo()
        );

        mostrarTextoLargoEnFicha(
                "Titulo:",
                material.getTitulo()
        );

        System.out.printf(
                "║ Total actual:%-31d║%n",
                material.getCantidadTotal()
        );

        System.out.printf(
                "║ Disponibles: %-31d║%n",
                material.getCantidadDisponible()
        );

        System.out.println(
                "╚══════════════════════════════════════════════╝"
        );


        System.out.println();

        System.out.println(
                "Ingrese 0 para cancelar."
        );

        System.out.print(
                "\nCantidad de ejemplares a agregar: "
        );


        int cantidad =
                leerEntero();


        if (
                cantidad == 0
        ) {

            return;
        }


        if (
                cantidad < 1
        ) {

            mostrarMensaje(
                    "La cantidad debe ser mayor que cero."
            );

            return;
        }


        System.out.println();

        System.out.println(
                "Se agregaran "
                        + cantidad
                        + " ejemplares."
        );

        System.out.println(
                "Nuevo total: "
                        + (
                                material.getCantidadTotal()
                                        + cantidad
                        )
        );

        System.out.println(
                "Nuevos disponibles: "
                        + (
                                material.getCantidadDisponible()
                                        + cantidad
                        )
        );

        System.out.println();

        System.out.println(
                "1. Confirmar"
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


        MaterialDAO materialDAO =
                new MaterialDAO();


        try {

            materialDAO
                    .agregarEjemplares(
                            material.getId(),
                            cantidad
                    );


            /*
             * Actualizamos tambien el objeto que ya esta
             * cargado en memoria. De esta manera la ficha
             * y el listado muestran los nuevos valores sin
             * tener que reiniciar el programa.
             */
            material.agregarEjemplares(
                    cantidad
            );


            mostrarMensaje(
                    "Ejemplares agregados correctamente."
            );


        } catch (SQLException e) {

            mostrarMensaje(
                    "No se pudieron agregar los ejemplares: "
                            + e.getMessage()
            );
        }
    }


    private void quitarEjemplares(
            MaterialBibliografico material
    ) {

        limpiarPantalla();


        int prestados =
                material.getCantidadTotal()
                        - material.getCantidadDisponible();


        System.out.println(
                "╔══════════════════════════════════════════════╗"
        );

        System.out.println(
                "║              QUITAR EJEMPLARES              ║"
        );

        System.out.println(
                "╠══════════════════════════════════════════════╣"
        );

        System.out.printf(
                "║ Codigo:      %-31s║%n",
                material.getCodigo()
        );

        mostrarTextoLargoEnFicha(
                "Titulo:",
                material.getTitulo()
        );

        System.out.printf(
                "║ Total actual:%-31d║%n",
                material.getCantidadTotal()
        );

        System.out.printf(
                "║ Disponibles: %-31d║%n",
                material.getCantidadDisponible()
        );

        System.out.printf(
                "║ Prestados:   %-31d║%n",
                prestados
        );

        System.out.println(
                "╚══════════════════════════════════════════════╝"
        );


        if (
                material.getCantidadDisponible() == 0
        ) {

            mostrarMensaje(
                    "No hay ejemplares disponibles para quitar."
            );

            return;
        }


        System.out.println();

        System.out.println(
                "Solo se pueden quitar ejemplares disponibles."
        );

        System.out.println(
                "Maximo que puede quitar: "
                        + material.getCantidadDisponible()
        );

        System.out.println(
                "Ingrese 0 para cancelar."
        );

        System.out.print(
                "\nCantidad de ejemplares a quitar: "
        );


        int cantidad =
                leerEntero();


        if (
                cantidad == 0
        ) {

            return;
        }


        if (
                cantidad < 1
        ) {

            mostrarMensaje(
                    "La cantidad debe ser mayor que cero."
            );

            return;
        }


        if (
                cantidad > material.getCantidadDisponible()
        ) {

            mostrarMensaje(
                    "No se pueden quitar "
                            + cantidad
                            + " ejemplares. Solo hay "
                            + material.getCantidadDisponible()
                            + " disponibles."
            );

            return;
        }


        System.out.println();

        System.out.println(
                "Se quitaran "
                        + cantidad
                        + " ejemplares."
        );

        System.out.println(
                "Nuevo total: "
                        + (
                                material.getCantidadTotal()
                                        - cantidad
                        )
        );

        System.out.println(
                "Nuevos disponibles: "
                        + (
                                material.getCantidadDisponible()
                                        - cantidad
                        )
        );

        System.out.println(
                "Prestados: "
                        + prestados
        );

        System.out.println();

        System.out.println(
                "1. Confirmar"
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


        MaterialDAO materialDAO =
                new MaterialDAO();


        try {

            materialDAO
                    .quitarEjemplares(
                            material.getId(),
                            cantidad
                    );


            /*
             * Mantenemos sincronizado el objeto que ya esta
             * cargado en memoria con lo que se guardo en BD.
             */
            material.quitarEjemplares(
                    cantidad
            );


            mostrarMensaje(
                    "Ejemplares quitados correctamente."
            );


        } catch (
                SQLException
                        | IllegalArgumentException e
        ) {

            mostrarMensaje(
                    "No se pudieron quitar los ejemplares: "
                            + e.getMessage()
            );
        }
    }


    private void mostrarTextoLargoEnFicha(
            String etiqueta,
            String texto
    ) {

        int anchoTexto =
                31;


        if (
                texto == null
                        || texto.isBlank()
        ) {

            System.out.printf(
                    "║ %-12s%-31s║%n",
                    etiqueta,
                    "-"
            );

            return;
        }


        String restante =
                texto.trim();


        boolean primeraLinea =
                true;


        while (
                !restante.isEmpty()
        ) {

            String linea;


            if (
                    restante.length()
                            <= anchoTexto
            ) {

                linea =
                        restante;

                restante =
                        "";

            } else {

                int corte =
                        restante.lastIndexOf(
                                " ",
                                anchoTexto
                        );


                if (
                        corte <= 0
                ) {

                    corte =
                            anchoTexto;
                }


                linea =
                        restante.substring(
                                0,
                                corte
                        ).trim();


                restante =
                        restante.substring(
                                corte
                        ).trim();
            }


            if (
                    primeraLinea
            ) {

                System.out.printf(
                        "║ %-12s%-31s║%n",
                        etiqueta,
                        linea
                );

                primeraLinea =
                        false;

            } else {

                System.out.printf(
                        "║ %-12s%-31s║%n",
                        "",
                        linea
                );
            }
        }
    }


    private String recortarTexto(
            String texto,
            int longitudMaxima
    ) {

        if (
                texto == null
        ) {

            return "-";
        }


        if (
                texto.length()
                        <= longitudMaxima
        ) {

            return texto;
        }


        return texto.substring(
                0,
                longitudMaxima - 3
        ) + "...";
    }


    private int leerEntero() {

        while (true) {

            try {

                return Integer.parseInt(
                        scanner.nextLine()
                                .trim()
                );

            } catch (
                    NumberFormatException e
            ) {

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

            if (
                    System.getProperty(
                                    "os.name"
                            )
                            .toLowerCase()
                            .contains(
                                    "windows"
                            )
            ) {

                new ProcessBuilder(
                        "cmd",
                        "/c",
                        "cls"
                )
                        .inheritIO()
                        .start()
                        .waitFor();

            } else {

                System.out.print(
                        "\033[H\033[2J"
                );

                System.out.flush();
            }

        } catch (
                Exception e
        ) {

            for (
                    int i = 0;
                    i < 30;
                    i++
            ) {

                System.out.println();
            }
        }
    }
}
