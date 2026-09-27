package ar.edu.itu.biblioteca.dao;

import ar.edu.itu.biblioteca.database.ConexionBD;
import ar.edu.itu.biblioteca.model.Multa;
import ar.edu.itu.biblioteca.model.MultaResumen;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class MultaDAO {

    public void guardar(
            Multa multa
    ) throws SQLException {

        String sql = """
                INSERT INTO multas
                (
                    prestamo_id,
                    dias_atraso,
                    monto,
                    fecha_generacion,
                    pagada
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    multa.getPrestamo().getId()
            );

            statement.setInt(
                    2,
                    multa.getDiasAtraso()
            );

            statement.setDouble(
                    3,
                    multa.getMonto()
            );

            statement.setDate(
                    4,
                    Date.valueOf(
                            multa.getFechaGeneracion()
                    )
            );

            statement.setBoolean(
                    5,
                    multa.isPagada()
            );

            statement.executeUpdate();
        }
    }


    public List<MultaResumen> listarPorUsuario(
            int usuarioId
    ) throws SQLException {

        List<MultaResumen> multas =
                new ArrayList<>();

        String sql = """
                SELECT
                    m.id AS multa_id,
                    mat.codigo AS codigo_material,
                    mat.titulo AS titulo_material,
                    m.dias_atraso,
                    m.monto,
                    m.fecha_generacion,
                    m.pagada
                FROM multas m
                INNER JOIN prestamos p
                    ON m.prestamo_id = p.id
                INNER JOIN materiales mat
                    ON p.material_id = mat.id
                WHERE p.usuario_id = ?
                ORDER BY m.fecha_generacion DESC
                """;

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    usuarioId
            );

            try (
                    ResultSet resultado =
                            statement.executeQuery()
            ) {

                while (resultado.next()) {

                    multas.add(
                            construirMultaResumen(
                                    resultado
                            )
                    );
                }
            }
        }

        return multas;
    }


    public List<MultaResumen> listarPorUsuarioYEstado(
            int usuarioId,
            boolean pagada
    ) throws SQLException {

        List<MultaResumen> multas =
                new ArrayList<>();

        String sql = """
                SELECT
                    m.id AS multa_id,
                    mat.codigo AS codigo_material,
                    mat.titulo AS titulo_material,
                    m.dias_atraso,
                    m.monto,
                    m.fecha_generacion,
                    m.pagada
                FROM multas m
                INNER JOIN prestamos p
                    ON m.prestamo_id = p.id
                INNER JOIN materiales mat
                    ON p.material_id = mat.id
                WHERE p.usuario_id = ?
                AND m.pagada = ?
                ORDER BY m.fecha_generacion DESC
                """;

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    usuarioId
            );

            statement.setBoolean(
                    2,
                    pagada
            );

            try (
                    ResultSet resultado =
                            statement.executeQuery()
            ) {

                while (resultado.next()) {

                    multas.add(
                            construirMultaResumen(
                                    resultado
                            )
                    );
                }
            }
        }

        return multas;
    }


    public double obtenerTotalPendientePorUsuario(
            int usuarioId
    ) throws SQLException {

        String sql = """
                SELECT
                    COALESCE(SUM(m.monto), 0) AS total_pendiente
                FROM multas m
                INNER JOIN prestamos p
                    ON m.prestamo_id = p.id
                WHERE p.usuario_id = ?
                AND m.pagada = FALSE
                """;

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    usuarioId
            );

            try (
                    ResultSet resultado =
                            statement.executeQuery()
            ) {

                if (
                        resultado.next()
                ) {

                    return resultado.getDouble(
                            "total_pendiente"
                    );
                }
            }
        }

        return 0.0;
    }


    public boolean marcarComoPagada(
            int multaId
    ) throws SQLException {

        String sql = """
                UPDATE multas
                SET pagada = TRUE
                WHERE id = ?
                AND pagada = FALSE
                """;

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    multaId
            );

            int filasModificadas =
                    statement.executeUpdate();

            return filasModificadas > 0;
        }
    }


    private MultaResumen construirMultaResumen(
            ResultSet resultado
    ) throws SQLException {

        return new MultaResumen(
                resultado.getInt(
                        "multa_id"
                ),
                resultado.getString(
                        "codigo_material"
                ),
                resultado.getString(
                        "titulo_material"
                ),
                resultado.getInt(
                        "dias_atraso"
                ),
                resultado.getDouble(
                        "monto"
                ),
                resultado
                        .getDate(
                                "fecha_generacion"
                        )
                        .toLocalDate(),
                resultado.getBoolean(
                        "pagada"
                )
        );
    }
}
