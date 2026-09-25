package ar.edu.itu.biblioteca.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static final String ARCHIVO_CONFIGURACION =
            "database.properties";

    public static Connection obtenerConexion()
            throws SQLException {

        Properties propiedades =
                new Properties();

        try (
                FileInputStream archivo =
                        new FileInputStream(
                                ARCHIVO_CONFIGURACION
                        )
        ) {

            propiedades.load(
                    archivo
            );

        } catch (IOException e) {

            throw new SQLException(
                    "No se pudo leer el archivo "
                            + ARCHIVO_CONFIGURACION,
                    e
            );
        }

        String url =
                propiedades.getProperty(
                        "db.url"
                );

        String usuario =
                propiedades.getProperty(
                        "db.usuario"
                );

        String password =
                propiedades.getProperty(
                        "db.password"
                );

        return DriverManager.getConnection(
                url,
                usuario,
                password
        );
    }
}