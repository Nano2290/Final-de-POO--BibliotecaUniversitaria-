package ar.edu.itu.biblioteca.dao;

import ar.edu.itu.biblioteca.database.ConexionBD;
import ar.edu.itu.biblioteca.model.Libro;
import ar.edu.itu.biblioteca.model.MaterialBibliografico;
import ar.edu.itu.biblioteca.model.Revista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {

    public void guardar(
            MaterialBibliografico material
    ) throws SQLException {

        String sql = """
                INSERT INTO materiales
                (
                    codigo,
                    titulo,
                    tipo_material,
                    cantidad_total,
                    cantidad_disponible
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    material.getCodigo()
            );

            statement.setString(
                    2,
                    material.getTitulo()
            );

            statement.setString(
                    3,
                    material.obtenerTipoMaterial()
            );

            statement.setInt(
                    4,
                    material.getCantidadTotal()
            );

            statement.setInt(
                    5,
                    material.getCantidadDisponible()
            );

            statement.executeUpdate();
        }
    }


    public String generarCodigoSugerido(
            String tipoMaterial
    ) throws SQLException {

        String prefijo;

        if (
                "LIBRO".equalsIgnoreCase(
                        tipoMaterial
                )
        ) {

            prefijo = "LIB";

        } else if (
                "REVISTA".equalsIgnoreCase(
                        tipoMaterial
                )
        ) {

            prefijo = "REV";

        } else {

            throw new SQLException(
                    "Tipo de material invalido."
            );
        }


        String sql = """
                SELECT codigo
                FROM materiales
                WHERE tipo_material = ?
                AND codigo LIKE ?
                """;


        int mayorNumero =
                0;


        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    tipoMaterial.toUpperCase()
            );

            statement.setString(
                    2,
                    prefijo + "%"
            );


            try (
                    ResultSet resultado =
                            statement.executeQuery()
            ) {

                while (
                        resultado.next()
                ) {

                    String codigo =
                            resultado.getString(
                                    "codigo"
                            );


                    if (
                            codigo != null
                            && codigo.matches(
                                    prefijo + "\\d{4}"
                            )
                    ) {

                        String parteNumerica =
                                codigo.substring(
                                        prefijo.length()
                                );

                        int numero =
                                Integer.parseInt(
                                        parteNumerica
                                );


                        if (
                                numero > mayorNumero
                        ) {

                            mayorNumero =
                                    numero;
                        }
                    }
                }
            }
        }


        int siguienteNumero =
                mayorNumero + 1;


        return prefijo
                + String.format(
                        "%04d",
                        siguienteNumero
                );
    }


    public MaterialBibliografico buscarPorCodigo(
            String codigo
    ) throws SQLException {

        String sql = """
                SELECT
                    id,
                    codigo,
                    titulo,
                    tipo_material,
                    cantidad_total,
                    cantidad_disponible
                FROM materiales
                WHERE codigo = ?
                """;

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    codigo
            );

            try (
                    ResultSet resultado =
                            statement.executeQuery()
            ) {

                if (
                        resultado.next()
                ) {

                    return crearMaterialDesdeResultSet(
                            resultado
                    );
                }
            }
        }

        return null;
    }


    public List<MaterialBibliografico> buscarCoincidencias(
            String texto
    ) throws SQLException {

        List<MaterialBibliografico> materiales =
                new ArrayList<>();


        String sql = """
                SELECT
                    id,
                    codigo,
                    titulo,
                    tipo_material,
                    cantidad_total,
                    cantidad_disponible
                FROM materiales
                WHERE UPPER(codigo) LIKE ?
                   OR UPPER(titulo) LIKE ?
                ORDER BY titulo
                """;


        String patron =
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
                    patron
            );

            statement.setString(
                    2,
                    patron
            );


            try (
                    ResultSet resultado =
                            statement.executeQuery()
            ) {

                while (
                        resultado.next()
                ) {

                    materiales.add(
                            crearMaterialDesdeResultSet(
                                    resultado
                            )
                    );
                }
            }
        }


        return materiales;
    }


    public List<MaterialBibliografico> listarTodos()
            throws SQLException {

        List<MaterialBibliografico> materiales =
                new ArrayList<>();


        String sql = """
                SELECT
                    id,
                    codigo,
                    titulo,
                    tipo_material,
                    cantidad_total,
                    cantidad_disponible
                FROM materiales
                ORDER BY id
                """;


        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql);

                ResultSet resultado =
                        statement.executeQuery()
        ) {

            while (
                    resultado.next()
            ) {

                materiales.add(
                        crearMaterialDesdeResultSet(
                                resultado
                        )
                );
            }
        }


        return materiales;
    }


    private MaterialBibliografico crearMaterialDesdeResultSet(
            ResultSet resultado
    ) throws SQLException {

        int id =
                resultado.getInt(
                        "id"
                );

        String codigo =
                resultado.getString(
                        "codigo"
                );

        String titulo =
                resultado.getString(
                        "titulo"
                );

        String tipoMaterial =
                resultado.getString(
                        "tipo_material"
                );

        int cantidadTotal =
                resultado.getInt(
                        "cantidad_total"
                );

        int cantidadDisponible =
                resultado.getInt(
                        "cantidad_disponible"
                );


        MaterialBibliografico material;


        if (
                "LIBRO".equalsIgnoreCase(
                        tipoMaterial
                )
        ) {

            material =
                    new Libro(
                            id,
                            codigo,
                            titulo,
                            cantidadTotal
                    );

        } else if (
                "REVISTA".equalsIgnoreCase(
                        tipoMaterial
                )
        ) {

            material =
                    new Revista(
                            id,
                            codigo,
                            titulo,
                            cantidadTotal
                    );

        } else {

            throw new SQLException(
                    "Tipo de material desconocido: "
                            + tipoMaterial
            );
        }


        while (
                material.getCantidadDisponible()
                        > cantidadDisponible
        ) {

            material.prestar();
        }


        return material;
    }
}