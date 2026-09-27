package ar.edu.itu.biblioteca.ui;

import ar.edu.itu.biblioteca.dao.MultaDAO;
import ar.edu.itu.biblioteca.dao.UsuarioDAO;
import ar.edu.itu.biblioteca.model.Docente;
import ar.edu.itu.biblioteca.model.Estudiante;
import ar.edu.itu.biblioteca.model.MotivoSuspension;
import ar.edu.itu.biblioteca.model.Usuario;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MenuUsuarios {

    private final Scanner scanner;
    private final ConsolaUI ui;

    public MenuUsuarios(
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
            mostrarMenu();

            System.out.print("\nSeleccione una opcion: ");
            opcion = ui.leerEntero();

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
                    ui.mostrarMensaje(
                            "Opcion invalida."
                    );
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

            ui.limpiarPantalla();

            System.out.println("╔══════════════════════════════════════════════╗");
            System.out.println("║              REGISTRAR USUARIO              ║");
            System.out.println("╚══════════════════════════════════════════════╝");

            System.out.println();
            System.out.println(
                    "Escriba 0 en cualquier momento para cancelar."
            );
            System.out.println();

            System.out.print("DNI: ");

            String dni =
                    scanner.nextLine().trim();

            if (dni.equals("0")) {
                return;
            }

            if (!dni.matches("\\d{7,8}")) {

                ui.mostrarMensaje(
                        "El DNI debe contener solamente 7 u 8 numeros."
                );

                continue;
            }


            System.out.print("Nombre: ");

            String nombre =
                    scanner.nextLine().trim();

            if (nombre.equals("0")) {
                return;
            }

            if (nombre.isEmpty()) {

                ui.mostrarMensaje(
                        "El nombre es obligatorio."
                );

                continue;
            }


            System.out.print("Apellido: ");

            String apellido =
                    scanner.nextLine().trim();

            if (apellido.equals("0")) {
                return;
            }

            if (apellido.isEmpty()) {

                ui.mostrarMensaje(
                        "El apellido es obligatorio."
                );

                continue;
            }


            System.out.print("Email: ");

            String email =
                    scanner.nextLine().trim();

            if (email.equals("0")) {
                return;
            }


            System.out.println();
            System.out.println("Tipo de usuario:");
            System.out.println("1. Estudiante");
            System.out.println("2. Docente");
            System.out.println("0. Cancelar");

            System.out.print(
                    "\nSeleccione tipo: "
            );

            int tipo =
                    ui.leerEntero();

            if (tipo == 0) {
                return;
            }

            if (tipo != 1 && tipo != 2) {

                ui.mostrarMensaje(
                        "Tipo de usuario invalido."
                );

                continue;
            }


            String tipoTexto =
                    tipo == 1
                            ? "ESTUDIANTE"
                            : "DOCENTE";


            ui.limpiarPantalla();

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
                    email.isBlank()
                            ? "-"
                            : email
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
                    ui.leerEntero();

            if (confirmacion == 0) {
                return;
            }

            if (confirmacion == 2) {
                continue;
            }

            if (confirmacion != 1) {

                ui.mostrarMensaje(
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

                ui.mostrarMensaje(
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

                    ui.mostrarMensaje(
                            "Ya existe un usuario registrado con ese DNI."
                    );

                } else {

                    ui.mostrarMensaje(
                            "No se pudo registrar el usuario: "
                                    + e.getMessage()
                    );
                }

                return;
            }
        }
    }


    private void buscarUsuario() {

        ui.limpiarPantalla();

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║                BUSCAR USUARIO               ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        System.out.print(
                "\nIngrese DNI, nombre o apellido: "
        );

        String texto =
                scanner.nextLine().trim();

        if (texto.isEmpty()) {

            ui.mostrarMensaje(
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

            ui.mostrarMensaje(
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

            ui.mostrarMensaje(
                    "Error al listar usuarios: "
                            + e.getMessage()
            );
        }
    }


    private void filtrarPorEstado() {

        ui.limpiarPantalla();

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
                ui.leerEntero();

        if (opcion == 0) {
            return;
        }

        if (
                opcion != 1
                        && opcion != 2
        ) {

            ui.mostrarMensaje(
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

            ui.mostrarMensaje(
                    "Error al filtrar usuarios: "
                            + e.getMessage()
            );
        }
    }


    private void seleccionarUsuario(
            List<Usuario> usuarios
    ) {

        ui.limpiarPantalla();

        if (usuarios.isEmpty()) {

            ui.mostrarMensaje(
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
                ui.leerEntero();

        if (seleccion == 0) {
            return;
        }

        if (
                seleccion < 1
                        || seleccion > usuarios.size()
        ) {

            ui.mostrarMensaje(
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

            ui.limpiarPantalla();

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

            if (
                    !usuario.isActivo()
            ) {

                String motivo =
                        usuario.getMotivoSuspension() == null
                                ? "SIN_ESPECIFICAR"
                                : usuario.getMotivoSuspension()
                                        .name();

                System.out.printf(
                        "║ Motivo:   %-34s║%n",
                        motivo
                );
            }

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
                    "║  6. Registrar prestamo                      ║"
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
                    ui.leerEntero();


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

                    modificarDatosUsuario(
                            usuario
                    );

                    break;


                case 5:

                    cambiarEstadoUsuario(
                            usuario
                    );

                    break;


                case 6: {

                    MenuPrestamos menuPrestamos =
                            new MenuPrestamos(
                                    scanner
                            );

                    menuPrestamos.registrarPrestamo(
                            usuario
                    );

                    break;
                }


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


    private void modificarDatosUsuario(
            Usuario usuario
    ) {

        ui.limpiarPantalla();

        System.out.println(
                "╔══════════════════════════════════════════════╗"
        );

        System.out.println(
                "║              MODIFICAR DATOS                ║"
        );

        System.out.println(
                "╚══════════════════════════════════════════════╝"
        );

        System.out.println();

        System.out.println(
                "DNI: "
                        + usuario.getDni()
                        + " (no modificable)"
        );

        System.out.println();

        System.out.println(
                "Para conservar un dato actual, presione ENTER."
        );

        System.out.println(
                "Escriba 0 para cancelar."
        );

        System.out.println();


        System.out.println(
                "Nombre actual: "
                        + usuario.getNombre()
        );

        System.out.print(
                "Nuevo nombre: "
        );

        String nuevoNombre =
                scanner.nextLine().trim();

        if (nuevoNombre.equals("0")) {
            return;
        }

        if (nuevoNombre.isEmpty()) {

            nuevoNombre =
                    usuario.getNombre();
        }


        System.out.println();

        System.out.println(
                "Apellido actual: "
                        + usuario.getApellido()
        );

        System.out.print(
                "Nuevo apellido: "
        );

        String nuevoApellido =
                scanner.nextLine().trim();

        if (nuevoApellido.equals("0")) {
            return;
        }

        if (nuevoApellido.isEmpty()) {

            nuevoApellido =
                    usuario.getApellido();
        }


        System.out.println();

        String emailActual =
                usuario.getEmail() == null
                        || usuario.getEmail().isBlank()
                        ? "-"
                        : usuario.getEmail();

        System.out.println(
                "Email actual: "
                        + emailActual
        );

        System.out.print(
                "Nuevo email: "
        );

        String nuevoEmail =
                scanner.nextLine().trim();

        if (nuevoEmail.equals("0")) {
            return;
        }

        if (nuevoEmail.isEmpty()) {

            nuevoEmail =
                    usuario.getEmail();
        }


        ui.limpiarPantalla();

        System.out.println(
                "╔══════════════════════════════════════════════╗"
        );

        System.out.println(
                "║           CONFIRMAR MODIFICACION            ║"
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
                nuevoNombre
        );

        System.out.printf(
                "║ Apellido: %-34s║%n",
                nuevoApellido
        );

        System.out.printf(
                "║ Email:    %-34s║%n",
                nuevoEmail == null
                        || nuevoEmail.isBlank()
                        ? "-"
                        : nuevoEmail
        );

        System.out.println(
                "╠══════════════════════════════════════════════╣"
        );

        System.out.println(
                "║  1. Confirmar cambios                       ║"
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


        String nombreAnterior =
                usuario.getNombre();

        String apellidoAnterior =
                usuario.getApellido();

        String emailAnterior =
                usuario.getEmail();


        usuario.setNombre(
                nuevoNombre
        );

        usuario.setApellido(
                nuevoApellido
        );

        usuario.setEmail(
                nuevoEmail
        );


        UsuarioDAO usuarioDAO =
                new UsuarioDAO();

        try {

            boolean actualizado =
                    usuarioDAO.actualizarDatos(
                            usuario
                    );

            if (actualizado) {

                ui.mostrarMensaje(
                        "Datos modificados correctamente."
                );

            } else {

                usuario.setNombre(
                        nombreAnterior
                );

                usuario.setApellido(
                        apellidoAnterior
                );

                usuario.setEmail(
                        emailAnterior
                );

                ui.mostrarMensaje(
                        "No se pudo modificar el usuario."
                );
            }

        } catch (SQLException e) {

            usuario.setNombre(
                    nombreAnterior
            );

            usuario.setApellido(
                    apellidoAnterior
            );

            usuario.setEmail(
                    emailAnterior
            );

            ui.mostrarMensaje(
                    "Error al modificar los datos: "
                            + e.getMessage()
            );
        }
    }

    private void cambiarEstadoUsuario(
            Usuario usuario
    ) {

        ui.limpiarPantalla();


        boolean estadoActual =
                usuario.isActivo();


        if (
                estadoActual
        ) {

            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║             SUSPENDER USUARIO               ║"
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

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println(
                    "║ La suspension sera registrada como MANUAL.  ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println(
                    "║  1. Confirmar suspension                    ║"
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


            usuario.setActivo(
                    false
            );

            usuario.setMotivoSuspension(
                    MotivoSuspension.MANUAL
            );


            UsuarioDAO usuarioDAO =
                    new UsuarioDAO();


            try {

                boolean actualizado =
                        usuarioDAO.actualizarEstado(
                                usuario
                        );


                if (
                        actualizado
                ) {

                    ui.mostrarMensaje(
                            "Usuario suspendido correctamente."
                    );

                } else {

                    usuario.setActivo(
                            true
                    );

                    ui.mostrarMensaje(
                            "No se pudo actualizar el estado."
                    );
                }


            } catch (SQLException e) {

                usuario.setActivo(
                        true
                );

                ui.mostrarMensaje(
                        "Error al actualizar el estado: "
                                + e.getMessage()
                );
            }


            return;
        }


        /*
         * Si queremos reactivar a un usuario suspendido,
         * primero revisamos que no siga superando el umbral
         * de multas pendientes.
         */
        MultaDAO multaDAO =
                new MultaDAO();


        double totalPendiente;


        try {

            totalPendiente =
                    multaDAO
                            .obtenerTotalPendientePorUsuario(
                                    usuario.getId()
                            );


        } catch (SQLException e) {

            ui.mostrarMensaje(
                    "No se pudo verificar la deuda pendiente: "
                            + e.getMessage()
            );

            return;
        }


        final double UMBRAL_SUSPENSION =
                5000.0;


        if (
                totalPendiente
                        > UMBRAL_SUSPENSION
        ) {

            usuario.setMotivoSuspension(
                    MotivoSuspension.DEUDA
            );


            UsuarioDAO usuarioDAO =
                    new UsuarioDAO();


            try {

                usuarioDAO.actualizarEstado(
                        usuario
                );

            } catch (SQLException e) {

                ui.mostrarMensaje(
                        "No se pudo actualizar el motivo de suspension: "
                                + e.getMessage()
                );

                return;
            }


            ui.limpiarPantalla();


            System.out.println(
                    "╔══════════════════════════════════════════════╗"
            );

            System.out.println(
                    "║          REACTIVACION BLOQUEADA             ║"
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.printf(
                    "║ Deuda pendiente: $%-27.2f║%n",
                    totalPendiente
            );

            System.out.printf(
                    "║ Umbral:          $%-27.2f║%n",
                    UMBRAL_SUSPENSION
            );

            System.out.println(
                    "╠══════════════════════════════════════════════╣"
            );

            System.out.println(
                    "║ El monto pendiente supera el umbral.        ║"
            );

            System.out.println(
                    "║ Primero deben regularizarse las multas.     ║"
            );

            System.out.println(
                    "╚══════════════════════════════════════════════╝"
            );

            ui.pausar();

            return;
        }


        String motivoActual =
                usuario.getMotivoSuspension() == null
                        ? "SIN_ESPECIFICAR"
                        : usuario.getMotivoSuspension()
                                .name();


        ui.limpiarPantalla();


        System.out.println(
                "╔══════════════════════════════════════════════╗"
        );

        System.out.println(
                "║              REACTIVAR USUARIO              ║"
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
                "║ Motivo actual: %-27s║%n",
                motivoActual
        );

        System.out.printf(
                "║ Deuda pendiente: $%-27.2f║%n",
                totalPendiente
        );

        System.out.println(
                "╠══════════════════════════════════════════════╣"
        );

        System.out.println(
                "║  1. Confirmar reactivacion                  ║"
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


        MotivoSuspension motivoAnterior =
                usuario.getMotivoSuspension();


        usuario.setActivo(
                true
        );


        UsuarioDAO usuarioDAO =
                new UsuarioDAO();


        try {

            boolean actualizado =
                    usuarioDAO.actualizarEstado(
                            usuario
                    );


            if (
                    actualizado
            ) {

                ui.mostrarMensaje(
                        "Usuario reactivado correctamente."
                );

            } else {

                usuario.setActivo(
                        false
                );

                usuario.setMotivoSuspension(
                        motivoAnterior
                );

                ui.mostrarMensaje(
                        "No se pudo actualizar el estado."
                );
            }


        } catch (SQLException e) {

            usuario.setActivo(
                    false
            );

            usuario.setMotivoSuspension(
                    motivoAnterior
            );

            ui.mostrarMensaje(
                    "Error al actualizar el estado: "
                            + e.getMessage()
            );
        }
    }
}
