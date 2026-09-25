package ar.edu.itu.biblioteca.ui;

import ar.edu.itu.biblioteca.dao.UsuarioDAO;
import ar.edu.itu.biblioteca.model.Docente;
import ar.edu.itu.biblioteca.model.Estudiante;
import ar.edu.itu.biblioteca.model.Usuario;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MenuUsuarios {

    private final Scanner scanner;

    public MenuUsuarios(Scanner scanner) {
        this.scanner = scanner;
    }

    public void iniciar() {

        int opcion;

        do {

            limpiarPantalla();
            mostrarMenu();

            System.out.print("\nSeleccione una opcion: ");
            opcion = leerEntero();

            switch (opcion) {

                case 1:
                    registrarUsuario();
                    break;

                case 2:
                    buscarUsuario();
                    break;

                case 3:
                    listarUsuarios();
                    break;

                case 4:
                    filtrarPorEstado();
                    break;

                case 0:
                    break;

                default:
                    mostrarMensaje("Opcion invalida.");
                    break;
            }

        } while (opcion != 0);
    }

    private void mostrarMenu() {

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║             GESTION DE USUARIOS             ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  1. Registrar usuario                       ║");
        System.out.println("║  2. Buscar usuario                          ║");
        System.out.println("║  3. Listar usuarios                         ║");
        System.out.println("║  4. Filtrar por estado                      ║");
        System.out.println("║                                              ║");
        System.out.println("║  0. Volver                                  ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    private void registrarUsuario() {

        while (true) {

            limpiarPantalla();

            System.out.println("╔══════════════════════════════════════════════╗");
            System.out.println("║              REGISTRAR USUARIO              ║");
            System.out.println("╚══════════════════════════════════════════════╝");

            System.out.println();
            System.out.println("Escriba 0 en cualquier momento para cancelar.");
            System.out.println();

            System.out.print("DNI: ");
            String dni = scanner.nextLine().trim();

            if (dni.equals("0")) {
                return;
            }

            if (!dni.matches("\\d{7,8}")) {

                mostrarMensaje(
                        "El DNI debe contener solamente 7 u 8 numeros."
                );

                continue;
            }

            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();

            if (nombre.equals("0")) {
                return;
            }

            if (nombre.isEmpty()) {

                mostrarMensaje(
                        "El nombre es obligatorio."
                );

                continue;
            }

            System.out.print("Apellido: ");
            String apellido = scanner.nextLine().trim();

            if (apellido.equals("0")) {
                return;
            }

            if (apellido.isEmpty()) {

                mostrarMensaje(
                        "El apellido es obligatorio."
                );

                continue;
            }

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            if (email.equals("0")) {
                return;
            }

            System.out.println();
            System.out.println("Tipo de usuario:");
            System.out.println("1. Estudiante");
            System.out.println("2. Docente");
            System.out.println("0. Cancelar");

            System.out.print("\nSeleccione tipo: ");

            int tipo = leerEntero();

            if (tipo == 0) {
                return;
            }

            if (tipo != 1 && tipo != 2) {

                mostrarMensaje(
                        "Tipo de usuario invalido."
                );

                continue;
            }

            String tipoTexto =
                    tipo == 1
                            ? "ESTUDIANTE"
                            : "DOCENTE";

            limpiarPantalla();

            System.out.println("╔══════════════════════════════════════════════╗");
            System.out.println("║           CONFIRMAR NUEVO USUARIO           ║");
            System.out.println("╠══════════════════════════════════════════════╣");

            System.out.printf(
                    "║ DNI:      %-34s║%n",
                    dni
            );

            System.out.printf(
                    "║ Nombre:   %-34s║%n",
                    nombre
            );

            System.out.printf(
                    "║ Apellido: %-34s║%n",
                    apellido
            );

            System.out.printf(
                    "║ Email:    %-34s║%n",
                    email.isBlank() ? "-" : email
            );

            System.out.printf(
                    "║ Tipo:     %-34s║%n",
                    tipoTexto
            );

            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  1. Confirmar                               ║");
            System.out.println("║  2. Volver a cargar los datos               ║");
            System.out.println("║  0. Cancelar                                ║");
            System.out.println("╚══════════════════════════════════════════════╝");

            System.out.print(
                    "\nSeleccione opcion: "
            );

            int confirmacion =
                    leerEntero();

            if (confirmacion == 0) {
                return;
            }

            if (confirmacion == 2) {
                continue;
            }

            if (confirmacion != 1) {

                mostrarMensaje(
                        "Opcion invalida."
                );

                continue;
            }

            Usuario usuario;

            if (tipo == 1) {

                usuario = new Estudiante(
                        0,
                        dni,
                        nombre,
                        apellido,
                        email
                );

            } else {

                usuario = new Docente(
                        0,
                        dni,
                        nombre,
                        apellido,
                        email
                );
            }

            UsuarioDAO usuarioDAO =
                    new UsuarioDAO();

            try {

                usuarioDAO.guardar(
                        usuario
                );

                mostrarMensaje(
                        "Usuario registrado correctamente."
                );

                return;

            } catch (SQLException e) {

                if (
                        e.getMessage() != null
                        && e.getMessage().contains(
                                "Duplicate entry"
                        )
                ) {

                    mostrarMensaje(
                            "Ya existe un usuario registrado con ese DNI."
                    );

                } else {

                    mostrarMensaje(
                            "No se pudo registrar el usuario: "
                                    + e.getMessage()
                    );
                }

                return;
            }
        }
    }

    private void buscarUsuario() {

        limpiarPantalla();

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║                BUSCAR USUARIO               ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        System.out.print(
                "\nIngrese DNI, nombre o apellido: "
        );

        String texto =
                scanner.nextLine().trim();

        if (texto.isEmpty()) {

            mostrarMensaje(
                    "Debe ingresar un criterio de busqueda."
            );

            return;
        }

        UsuarioDAO usuarioDAO =
                new UsuarioDAO();

        try {

            List<Usuario> usuarios =
                    usuarioDAO.buscarCoincidencias(
                            texto
                    );

            seleccionarUsuario(
                    usuarios
            );

        } catch (SQLException e) {

            mostrarMensaje(
                    "Error al buscar usuarios: "
                            + e.getMessage()
            );
        }
    }

    private void listarUsuarios() {

        UsuarioDAO usuarioDAO =
                new UsuarioDAO();

        try {

            List<Usuario> usuarios =
                    usuarioDAO.listarTodos();

            seleccionarUsuario(
                    usuarios
            );

        } catch (SQLException e) {

            mostrarMensaje(
                    "Error al listar usuarios: "
                            + e.getMessage()
            );
        }
    }

    private void filtrarPorEstado() {

        limpiarPantalla();

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║             FILTRAR POR ESTADO              ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  1. Usuarios activos                        ║");
        System.out.println("║  2. Usuarios suspendidos                    ║");
        System.out.println("║  0. Volver                                  ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        System.out.print(
                "\nSeleccione opcion: "
        );

        int opcion =
                leerEntero();

        if (opcion == 0) {
            return;
        }

        if (
                opcion != 1
                && opcion != 2
        ) {

            mostrarMensaje(
                    "Opcion invalida."
            );

            return;
        }

        boolean estadoBuscado =
                opcion == 1;

        UsuarioDAO usuarioDAO =
                new UsuarioDAO();

        try {

            List<Usuario> todos =
                    usuarioDAO.listarTodos();

            List<Usuario> filtrados =
                    todos.stream()
                            .filter(
                                    usuario ->
                                            usuario.isActivo()
                                                    == estadoBuscado
                            )
                            .toList();

            seleccionarUsuario(
                    filtrados
            );

        } catch (SQLException e) {

            mostrarMensaje(
                    "Error al filtrar usuarios: "
                            + e.getMessage()
            );
        }
    }

    private void seleccionarUsuario(
            List<Usuario> usuarios
    ) {

        limpiarPantalla();

        if (usuarios.isEmpty()) {

            mostrarMensaje(
                    "No se encontraron usuarios."
            );

            return;
        }

        System.out.println(
                "╔════════════════════════════════════════════════════════════════════════╗"
        );

        System.out.println(
                "║                         LISTADO DE USUARIOS                           ║"
        );

        System.out.println(
                "╚════════════════════════════════════════════════════════════════════════╝"
        );

        System.out.println();

        System.out.println(
                "----------------------------------------------------------------------------"
        );

        System.out.printf(
                "%-4s %-10s %-18s %-18s %-12s %-12s%n",
                "N°",
                "DNI",
                "Nombre",
                "Apellido",
                "Tipo",
                "Estado"
        );

        System.out.println(
                "----------------------------------------------------------------------------"
        );

        for (int i = 0; i < usuarios.size(); i++) {

            Usuario usuario =
                    usuarios.get(i);

            String tipo =
                    usuario.getClass()
                            .getSimpleName()
                            .toUpperCase();

            String estado =
                    usuario.isActivo()
                            ? "ACTIVO"
                            : "SUSPENDIDO";

            System.out.printf(
                    "%-4d %-10s %-18s %-18s %-12s %-12s%n",
                    i + 1,
                    usuario.getDni(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    tipo,
                    estado
            );
        }

        System.out.println(
                "----------------------------------------------------------------------------"
        );

        System.out.println();
        System.out.println("0. Volver");

        System.out.print(
                "\nSeleccione usuario: "
        );

        int seleccion =
                leerEntero();

        if (seleccion == 0) {
            return;
        }

        if (
                seleccion < 1
                || seleccion > usuarios.size()
        ) {

            mostrarMensaje(
                    "Seleccion invalida."
            );

            return;
        }

        Usuario usuarioSeleccionado =
                usuarios.get(
                        seleccion - 1
                );

        mostrarFichaUsuario(
                usuarioSeleccionado
        );
    }

    private void mostrarFichaUsuario(
            Usuario usuario
    ) {

        int opcion;

        do {

            limpiarPantalla();

            String tipo =
                    usuario.getClass()
                            .getSimpleName()
                            .toUpperCase();

            String estado =
                    usuario.isActivo()
                            ? "ACTIVO"
                            : "SUSPENDIDO";

            String email =
                    usuario.getEmail() == null
                    || usuario.getEmail().isBlank()
                            ? "-"
                            : usuario.getEmail();

            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║               FICHA DE USUARIO              ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.printf(
                    "║ DNI:      %-34s║%n",
                    usuario.getDni()
            );

            System.out.printf(
                    "║ Nombre:   %-34s║%n",
                    usuario.getNombre()
                            + " "
                            + usuario.getApellido()
            );

            System.out.printf(
                    "║ Email:    %-34s║%n",
                    email
            );

            System.out.printf(
                    "║ Tipo:     %-34s║%n",
                    tipo
            );

            System.out.printf(
                    "║ Estado:   %-34s║%n",
                    estado
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
                    "║  3. Ver multas                              ║"
            );

            System.out.println(
                    "║  4. Modificar datos                         ║"
            );

            System.out.println(
                    "║  5. Cambiar estado                          ║"
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
                    "\nSeleccione opcion: "
            );

            opcion =
                    leerEntero();

            switch (opcion) {

                case 1: {

                    MenuPrestamos menuPrestamos =
                            new MenuPrestamos(
                                    scanner
                            );

                    menuPrestamos.mostrarPrestamosActivos(
                            usuario
                    );

                    break;
                }

                case 2: {

                    MenuPrestamos menuPrestamos =
                            new MenuPrestamos(
                                    scanner
                            );

                    menuPrestamos.mostrarHistorialPrestamos(
                            usuario
                    );

                    break;
                }

                case 3: {

                    MenuMultas menuMultas =
                            new MenuMultas(
                                    scanner
                            );

                    menuMultas.mostrarMultasDeUsuario(
                            usuario
                    );

                    break;
                }

                case 4:

                    mostrarMensaje(
                            "Modificar datos: funcion en preparacion."
                    );

                    break;

                case 5:

                    mostrarMensaje(
                            "Cambiar estado: funcion en preparacion."
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