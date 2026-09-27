package ar.edu.itu.biblioteca.dao;

import ar.edu.itu.biblioteca.database.ConexionBD;
import ar.edu.itu.biblioteca.model.Docente;
import ar.edu.itu.biblioteca.model.Estudiante;
import ar.edu.itu.biblioteca.model.MotivoSuspension;
import ar.edu.itu.biblioteca.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void guardar(
            Usuario usuario
    ) throws SQLException {

        String sql = """
                INSERT INTO usuarios
                (
                    dni,
                    nombre,
                    apellido,
                    email,
                    tipo_usuario,
                    activo,
                    motivo_suspension
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    usuario.getDni()
            );

            statement.setString(
                    2,
                    usuario.getNombre()
            );

            statement.setString(
                    3,
                    usuario.getApellido()
            );

            statement.setString(
                    4,
                    usuario.getEmail()
            );

            statement.setString(
                    5,
                    usuario
                            .getClass()
                            .getSimpleName()
                            .toUpperCase()
            );

            statement.setBoolean(
                    6,
                    usuario.isActivo()
            );

            if (
                    usuario.getMotivoSuspension() == null
            ) {

                statement.setNull(
                        7,
                        java.sql.Types.VARCHAR
                );

            } else {

                statement.setString(
                        7,
                        usuario.getMotivoSuspension()
                                .name()
                );
            }

            statement.executeUpdate();
        }
    }


    public boolean actualizarDatos(
            Usuario usuario
    ) throws SQLException {

        String sql = """
                UPDATE usuarios
                SET
                    nombre = ?,
                    apellido = ?,
                    email = ?
                WHERE id = ?
                """;

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    usuario.getNombre()
            );

            statement.setString(
                    2,
                    usuario.getApellido()
            );

            statement.setString(
                    3,
                    usuario.getEmail()
            );

            statement.setInt(
                    4,
                    usuario.getId()
            );

            int filasModificadas =
                    statement.executeUpdate();

            return filasModificadas > 0;
        }
    }


    public boolean actualizarEstado(
            Usuario usuario
    ) throws SQLException {

        String sql = """
                UPDATE usuarios
                SET
                    activo = ?,
                    motivo_suspension = ?
                WHERE id = ?
                """;

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setBoolean(
                    1,
                    usuario.isActivo()
            );

            if (
                    usuario.getMotivoSuspension() == null
            ) {

                statement.setNull(
                        2,
                        java.sql.Types.VARCHAR
                );

            } else {

                statement.setString(
                        2,
                        usuario.getMotivoSuspension()
                                .name()
                );
            }

            statement.setInt(
                    3,
                    usuario.getId()
            );

            int filasModificadas =
                    statement.executeUpdate();

            return filasModificadas > 0;
        }
    }


    public Usuario buscarPorDni(
            String dni
    ) throws SQLException {

        String sql = """
                SELECT
                    id,
                    dni,
                    nombre,
                    apellido,
                    email,
                    tipo_usuario,
                    activo,
                    motivo_suspension
                FROM usuarios
                WHERE dni = ?
                """;

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    dni
            );

            try (
                    ResultSet resultado =
                            statement.executeQuery()
            ) {

                if (resultado.next()) {

                    return crearUsuarioDesdeResultSet(
                            resultado
                    );
                }
            }
        }

        return null;
    }


    public List<Usuario> listarTodos()
            throws SQLException {

        List<Usuario> usuarios =
                new ArrayList<>();

        String sql = """
                SELECT
                    id,
                    dni,
                    nombre,
                    apellido,
                    email,
                    tipo_usuario,
                    activo,
                    motivo_suspension
                FROM usuarios
                ORDER BY apellido, nombre
                """;

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql);

                ResultSet resultado =
                        statement.executeQuery()
        ) {

            while (resultado.next()) {

                usuarios.add(
                        crearUsuarioDesdeResultSet(
                                resultado
                        )
                );
            }
        }

        return usuarios;
    }


    public List<Usuario> buscarCoincidencias(
            String texto
    ) throws SQLException {

        List<Usuario> usuarios =
                new ArrayList<>();

        String sql = """
                SELECT
                    id,
                    dni,
                    nombre,
                    apellido,
                    email,
                    tipo_usuario,
                    activo,
                    motivo_suspension
                FROM usuarios
                WHERE UPPER(dni) LIKE ?
                   OR UPPER(nombre) LIKE ?
                   OR UPPER(apellido) LIKE ?
                ORDER BY apellido, nombre
                """;

        String criterio =
                "%"
                        + texto
                        .trim()
                        .toUpperCase()
                        + "%";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    criterio
            );

            statement.setString(
                    2,
                    criterio
            );

            statement.setString(
                    3,
                    criterio
            );

            try (
                    ResultSet resultado =
                            statement.executeQuery()
            ) {

                while (resultado.next()) {

                    usuarios.add(
                            crearUsuarioDesdeResultSet(
                                    resultado
                            )
                    );
                }
            }
        }

        return usuarios;
    }


    private Usuario crearUsuarioDesdeResultSet(
            ResultSet resultado
    ) throws SQLException {

        int id =
                resultado.getInt(
                        "id"
                );

        String dni =
                resultado.getString(
                        "dni"
                );

        String nombre =
                resultado.getString(
                        "nombre"
                );

        String apellido =
                resultado.getString(
                        "apellido"
                );

        String email =
                resultado.getString(
                        "email"
                );

        String tipoUsuario =
                resultado.getString(
                        "tipo_usuario"
                );

        boolean activo =
                resultado.getBoolean(
                        "activo"
                );

        String motivoTexto =
                resultado.getString(
                        "motivo_suspension"
                );

        Usuario usuario;

        if (
                "ESTUDIANTE"
                        .equalsIgnoreCase(
                                tipoUsuario
                        )
        ) {

            usuario =
                    new Estudiante(
                            id,
                            dni,
                            nombre,
                            apellido,
                            email
                    );

        } else if (
                "DOCENTE"
                        .equalsIgnoreCase(
                                tipoUsuario
                        )
        ) {

            usuario =
                    new Docente(
                            id,
                            dni,
                            nombre,
                            apellido,
                            email
                    );

        } else {

            throw new SQLException(
                    "Tipo de usuario desconocido: "
                            + tipoUsuario
            );
        }

        usuario.setActivo(
                activo
        );

        if (
                !activo
        ) {

            MotivoSuspension motivo =
                    MotivoSuspension.SIN_ESPECIFICAR;

            if (
                    motivoTexto != null
                            && !motivoTexto.isBlank()
            ) {

                try {

                    motivo =
                            MotivoSuspension.valueOf(
                                    motivoTexto
                                            .trim()
                                            .toUpperCase()
                            );

                } catch (
                        IllegalArgumentException e
                ) {

                    motivo =
                            MotivoSuspension.SIN_ESPECIFICAR;
                }
            }

            usuario.setMotivoSuspension(
                    motivo
            );
        }

        return usuario;
    }
}
