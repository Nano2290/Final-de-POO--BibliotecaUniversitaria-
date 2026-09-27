package ar.edu.itu.biblioteca.dao;

import ar.edu.itu.biblioteca.database.ConexionBD;
import ar.edu.itu.biblioteca.model.Docente;
import ar.edu.itu.biblioteca.model.Estudiante;
import ar.edu.itu.biblioteca.model.Prestamo;
import ar.edu.itu.biblioteca.model.Usuario;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import ar.edu.itu.biblioteca.model.PrestamoResumen;
import ar.edu.itu.biblioteca.model.PrestamoMaterialResumen;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    public void registrarPrestamo(Prestamo prestamo) throws SQLException {

        String sqlPrestamo = """
                INSERT INTO prestamos
                (
                    usuario_id,
                    material_id,
                    fecha_inicio,
                    fecha_vencimiento,
                    fecha_devolucion
                )
                VALUES (?, ?, ?, ?, NULL)
                """;

        String sqlActualizarStock = """
                UPDATE materiales
                SET cantidad_disponible = cantidad_disponible - 1
                WHERE id = ?
                AND cantidad_disponible > 0
                """;

        Connection conexion = null;

        try {

            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            try (
                    PreparedStatement statementPrestamo =
                            conexion.prepareStatement(
                                    sqlPrestamo,
                                    Statement.RETURN_GENERATED_KEYS
                            );

                    PreparedStatement statementStock =
                            conexion.prepareStatement(
                                    sqlActualizarStock
                            )
            ) {

                /*
                 * 1. REGISTRAMOS EL PRESTAMO
                 */

                statementPrestamo.setInt(
                        1,
                        prestamo.getUsuario().getId()
                );

                statementPrestamo.setInt(
                        2,
                        prestamo.getMaterial().getId()
                );

                statementPrestamo.setDate(
                        3,
                        Date.valueOf(
                                prestamo.getFechaInicio()
                        )
                );

                statementPrestamo.setDate(
                        4,
                        Date.valueOf(
                                prestamo.getFechaVencimiento()
                        )
                );

                statementPrestamo.executeUpdate();

                /*
                 * 2. RECUPERAMOS EL ID GENERADO POR MYSQL
                 */

                try (
                        ResultSet clavesGeneradas =
                                statementPrestamo.getGeneratedKeys()
                ) {

                    if (clavesGeneradas.next()) {

                        prestamo.setId(
                                clavesGeneradas.getInt(1)
                        );

                    } else {

                        throw new SQLException(
                                "No se pudo obtener el ID generado del prestamo."
                        );
                    }
                }

                /*
                 * 3. ACTUALIZAMOS LA DISPONIBILIDAD
                 */

                statementStock.setInt(
                        1,
                        prestamo.getMaterial().getId()
                );

                int filasActualizadas =
                        statementStock.executeUpdate();

                if (filasActualizadas == 0) {

                    throw new SQLException(
                            "No hay disponibilidad para el material."
                    );
                }

                /*
                 * 4. CONFIRMAMOS LA TRANSACCION
                 */

                conexion.commit();
            }

        } catch (SQLException e) {

            if (conexion != null) {

                try {

                    conexion.rollback();

                } catch (SQLException rollbackException) {

                    e.addSuppressed(
                            rollbackException
                    );
                }
            }

            throw e;

        } finally {

            cerrarConexion(conexion);
        }
    }


    public void registrarDevolucion(
            int prestamoId,
            LocalDate fechaDevolucion
    ) throws SQLException {

        String sqlBuscarPrestamo = """
                SELECT
                    p.material_id,
                    p.fecha_vencimiento,
                    p.fecha_devolucion,
                    u.id AS usuario_id,
                    u.dni,
                    u.email,
                    u.nombre,
                    u.apellido,
                    u.tipo_usuario,
                    u.activo
                FROM prestamos p
                INNER JOIN usuarios u
                    ON p.usuario_id = u.id
                WHERE p.id = ?
                """;

        String sqlActualizarPrestamo = """
                UPDATE prestamos
                SET fecha_devolucion = ?
                WHERE id = ?
                """;

        String sqlActualizarStock = """
                UPDATE materiales
                SET cantidad_disponible = cantidad_disponible + 1
                WHERE id = ?
                AND cantidad_disponible < cantidad_total
                """;

        String sqlInsertarMulta = """
                INSERT INTO multas
                (
                    prestamo_id,
                    dias_atraso,
                    monto,
                    fecha_generacion,
                    pagada
                )
                VALUES (?, ?, ?, ?, FALSE)
                """;

        String sqlTotalMultas = """
                SELECT
                    COALESCE(SUM(m.monto), 0) AS total_multas
                FROM multas m
                INNER JOIN prestamos p
                    ON m.prestamo_id = p.id
                WHERE p.usuario_id = ?
                AND m.pagada = FALSE
                """;

        String sqlSuspenderUsuario = """
                UPDATE usuarios
                SET
                    activo = FALSE,
                    motivo_suspension = 'DEUDA'
                WHERE id = ?
                """;

        final double UMBRAL_SUSPENSION = 5000.0;

        Connection conexion = null;

        try {

            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            int materialId;
            int usuarioId;
            LocalDate fechaVencimiento;
            Usuario usuario;

            /*
             * 1. BUSCAMOS EL PRESTAMO
             */

            try (
                    PreparedStatement buscarPrestamo =
                            conexion.prepareStatement(
                                    sqlBuscarPrestamo
                            )
            ) {

                buscarPrestamo.setInt(
                        1,
                        prestamoId
                );

                try (
                        ResultSet resultado =
                                buscarPrestamo.executeQuery()
                ) {

                    if (!resultado.next()) {

                        throw new SQLException(
                                "El prestamo no existe."
                        );
                    }

                    if (resultado.getDate(
                            "fecha_devolucion"
                    ) != null) {

                        throw new SQLException(
                                "El prestamo ya fue devuelto."
                        );
                    }

                    materialId =
                            resultado.getInt(
                                    "material_id"
                            );

                    fechaVencimiento =
                            resultado.getDate(
                                    "fecha_vencimiento"
                            ).toLocalDate();

                    usuarioId =
                            resultado.getInt(
                                    "usuario_id"
                            );

                    String dni =
                             resultado.getString(
                                     "dni"
                            );

                   String email =
                            resultado.getString(
                                    "email"
                            );

                    String nombre =
                            resultado.getString(
                                    "nombre"
                            );

                    String apellido =
                            resultado.getString(
                                    "apellido"
                            );

                    String tipoUsuario =
                            resultado.getString(
                                    "tipo_usuario"
                            );

                    boolean activo =
                            resultado.getBoolean(
                                    "activo"
                            );

                    /*
                     * Reconstruimos el tipo concreto de usuario
                     * para aprovechar el polimorfismo.
                     */

                    if (
                            "ESTUDIANTE"
                                    .equalsIgnoreCase(
                                            tipoUsuario
                                    )
                    ) {

                        usuario = new Estudiante(
                                            usuarioId,
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

                       usuario = new Docente(
                                             usuarioId,
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
                }
            }

            /*
             * 2. CALCULAMOS DIAS DE ATRASO
             */

            long diasAtraso = 0;

            if (
                    fechaDevolucion.isAfter(
                            fechaVencimiento
                    )
            ) {

                diasAtraso =
                        ChronoUnit.DAYS.between(
                                fechaVencimiento,
                                fechaDevolucion
                        );
            }

            /*
             * 3. GENERAMOS MULTA SI CORRESPONDE
             */

            if (diasAtraso > 0) {

                double montoMulta =
                        usuario.calcularMulta(
                                (int) diasAtraso
                        );

                try (
                        PreparedStatement insertarMulta =
                                conexion.prepareStatement(
                                        sqlInsertarMulta
                                )
                ) {

                    insertarMulta.setInt(
                            1,
                            prestamoId
                    );

                    insertarMulta.setInt(
                            2,
                            (int) diasAtraso
                    );

                    insertarMulta.setDouble(
                            3,
                            montoMulta
                    );

                    insertarMulta.setDate(
                            4,
                            Date.valueOf(
                                    LocalDate.now()
                            )
                    );

                    insertarMulta.executeUpdate();
                }

                /*
                 * 4. CALCULAMOS TOTAL DE MULTAS IMPAGAS
                 */

                double totalMultas = 0.0;

                try (
                        PreparedStatement consultarMultas =
                                conexion.prepareStatement(
                                        sqlTotalMultas
                                )
                ) {

                    consultarMultas.setInt(
                            1,
                            usuarioId
                    );

                    try (
                            ResultSet resultadoMultas =
                                    consultarMultas
                                            .executeQuery()
                    ) {

                        if (
                                resultadoMultas.next()
                        ) {

                            totalMultas =
                                    resultadoMultas
                                            .getDouble(
                                                    "total_multas"
                                            );
                        }
                    }
                }

                /*
                 * 5. SUSPENSION AUTOMATICA
                 */

                if (
                        totalMultas
                                > UMBRAL_SUSPENSION
                ) {

                    try (
                            PreparedStatement suspenderUsuario =
                                    conexion.prepareStatement(
                                            sqlSuspenderUsuario
                                    )
                    ) {

                        suspenderUsuario.setInt(
                                1,
                                usuarioId
                        );

                        suspenderUsuario.executeUpdate();
                    }
                }
            }

            /*
             * 6. REGISTRAMOS FECHA DE DEVOLUCION
             */

            try (
                    PreparedStatement actualizarPrestamo =
                            conexion.prepareStatement(
                                    sqlActualizarPrestamo
                            )
            ) {

                actualizarPrestamo.setDate(
                        1,
                        Date.valueOf(
                                fechaDevolucion
                        )
                );

                actualizarPrestamo.setInt(
                        2,
                        prestamoId
                );

                int filasPrestamo =
                        actualizarPrestamo.executeUpdate();

                if (filasPrestamo == 0) {

                    throw new SQLException(
                            "No se pudo registrar la devolucion."
                    );
                }
            }

            /*
             * 7. DEVOLVEMOS EL EJEMPLAR AL STOCK
             */

            try (
                    PreparedStatement actualizarStock =
                            conexion.prepareStatement(
                                    sqlActualizarStock
                            )
            ) {

                actualizarStock.setInt(
                        1,
                        materialId
                );

                int filasActualizadas =
                        actualizarStock.executeUpdate();

                if (filasActualizadas == 0) {

                    throw new SQLException(
                            "No se pudo actualizar la disponibilidad del material."
                    );
                }
            }

            /*
             * 8. CONFIRMAMOS 
             */

            conexion.commit();

        } catch (SQLException e) {

            if (conexion != null) {

                try {

                    conexion.rollback();

                } catch (SQLException rollbackException) {

                    e.addSuppressed(
                            rollbackException
                    );
                }
            }

            throw e;

        } finally {

            cerrarConexion(conexion);
        }
    }
    public List<PrestamoResumen> listarHistorialPorUsuario(
        int usuarioId
) throws SQLException {

    List<PrestamoResumen> historial =
            new ArrayList<>();

    String sql = """
            SELECT
                p.id AS prestamo_id,
                m.codigo AS codigo_material,
                m.titulo AS titulo_material,
                p.fecha_inicio,
                p.fecha_vencimiento,
                p.fecha_devolucion
            FROM prestamos p
            INNER JOIN materiales m
                ON p.material_id = m.id
            WHERE p.usuario_id = ?
            ORDER BY p.fecha_inicio DESC
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

                LocalDate fechaInicio =
                        resultado
                                .getDate(
                                        "fecha_inicio"
                                )
                                .toLocalDate();

                LocalDate fechaVencimiento =
                        resultado
                                .getDate(
                                        "fecha_vencimiento"
                                )
                                .toLocalDate();

                Date fechaDevolucionSql =
                        resultado.getDate(
                                "fecha_devolucion"
                        );

                LocalDate fechaDevolucion =
                        fechaDevolucionSql == null
                                ? null
                                : fechaDevolucionSql
                                        .toLocalDate();

                PrestamoResumen resumen =
                        new PrestamoResumen(
                                resultado.getInt(
                                        "prestamo_id"
                                ),
                                resultado.getString(
                                        "codigo_material"
                                ),
                                resultado.getString(
                                        "titulo_material"
                                ),
                                fechaInicio,
                                fechaVencimiento,
                                fechaDevolucion
                        );

                historial.add(
                        resumen
                );
            }
        }
    }

    return historial;
}
    public List<PrestamoResumen> listarPrestamosActivosPorUsuario(
        int usuarioId
) throws SQLException {

    List<PrestamoResumen> prestamosActivos =
            new ArrayList<>();

    String sql = """
            SELECT
                p.id AS prestamo_id,
                m.codigo AS codigo_material,
                m.titulo AS titulo_material,
                p.fecha_inicio,
                p.fecha_vencimiento,
                p.fecha_devolucion
            FROM prestamos p
            INNER JOIN materiales m
                ON p.material_id = m.id
            WHERE p.usuario_id = ?
            AND p.fecha_devolucion IS NULL
            ORDER BY p.fecha_vencimiento ASC
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

                LocalDate fechaInicio =
                        resultado
                                .getDate("fecha_inicio")
                                .toLocalDate();

                LocalDate fechaVencimiento =
                        resultado
                                .getDate("fecha_vencimiento")
                                .toLocalDate();

                PrestamoResumen prestamo =
                        new PrestamoResumen(
                                resultado.getInt(
                                        "prestamo_id"
                                ),
                                resultado.getString(
                                        "codigo_material"
                                ),
                                resultado.getString(
                                        "titulo_material"
                                ),
                                fechaInicio,
                                fechaVencimiento,
                                null
                        );

                prestamosActivos.add(
                        prestamo
                );
            }
        }
    }

    return prestamosActivos;
}
public List<PrestamoMaterialResumen>
listarPrestamosActivosPorMaterial(
        int materialId
) throws SQLException {

    List<PrestamoMaterialResumen> prestamos =
            new ArrayList<>();


    String sql = """
            SELECT
                p.id AS prestamo_id,
                u.dni,
                u.nombre,
                u.apellido,
                p.fecha_inicio,
                p.fecha_vencimiento,
                p.fecha_devolucion
            FROM prestamos p
            INNER JOIN usuarios u
                ON p.usuario_id = u.id
            WHERE p.material_id = ?
            AND p.fecha_devolucion IS NULL
            ORDER BY p.fecha_vencimiento ASC
            """;


    try (
            Connection conexion =
                    ConexionBD.obtenerConexion();

            PreparedStatement statement =
                    conexion.prepareStatement(
                            sql
                    )
    ) {

        statement.setInt(
                1,
                materialId
        );


        try (
                ResultSet resultado =
                        statement.executeQuery()
        ) {

            while (
                    resultado.next()
            ) {

                PrestamoMaterialResumen resumen =
                        new PrestamoMaterialResumen(
                                resultado.getInt(
                                        "prestamo_id"
                                ),
                                resultado.getString(
                                        "dni"
                                ),
                                resultado.getString(
                                        "nombre"
                                ),
                                resultado.getString(
                                        "apellido"
                                ),
                                resultado.getDate(
                                        "fecha_inicio"
                                ).toLocalDate(),
                                resultado.getDate(
                                        "fecha_vencimiento"
                                ).toLocalDate(),
                                null
                        );


                prestamos.add(
                        resumen
                );
            }
        }
    }


    return prestamos;
}
public List<PrestamoMaterialResumen>
listarHistorialPorMaterial(
        int materialId
) throws SQLException {

    List<PrestamoMaterialResumen> historial =
            new ArrayList<>();


    String sql = """
            SELECT
                p.id AS prestamo_id,
                u.dni,
                u.nombre,
                u.apellido,
                p.fecha_inicio,
                p.fecha_vencimiento,
                p.fecha_devolucion
            FROM prestamos p
            INNER JOIN usuarios u
                ON p.usuario_id = u.id
            WHERE p.material_id = ?
            ORDER BY p.fecha_inicio DESC
            """;


    try (
            Connection conexion =
                    ConexionBD.obtenerConexion();

            PreparedStatement statement =
                    conexion.prepareStatement(
                            sql
                    )
    ) {

        statement.setInt(
                1,
                materialId
        );


        try (
                ResultSet resultado =
                        statement.executeQuery()
        ) {

            while (
                    resultado.next()
            ) {

                Date fechaDevolucionSql =
                        resultado.getDate(
                                "fecha_devolucion"
                        );


                LocalDate fechaDevolucion =
                        fechaDevolucionSql == null
                                ? null
                                : fechaDevolucionSql
                                        .toLocalDate();


                PrestamoMaterialResumen resumen =
                        new PrestamoMaterialResumen(
                                resultado.getInt(
                                        "prestamo_id"
                                ),
                                resultado.getString(
                                        "dni"
                                ),
                                resultado.getString(
                                        "nombre"
                                ),
                                resultado.getString(
                                        "apellido"
                                ),
                                resultado.getDate(
                                        "fecha_inicio"
                                ).toLocalDate(),
                                resultado.getDate(
                                        "fecha_vencimiento"
                                ).toLocalDate(),
                                fechaDevolucion
                        );


                historial.add(
                        resumen
                );
            }
        }
    }


    return historial;
}

    private void cerrarConexion(
            Connection conexion
    ) {

        if (conexion != null) {

            try {

                conexion.setAutoCommit(true);
                conexion.close();

            } catch (SQLException e) {

                System.out.println(
                        "Error al cerrar la conexion: "
                                + e.getMessage()
                );
            }
        }
    }
}