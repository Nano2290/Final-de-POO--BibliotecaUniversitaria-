package ar.edu.itu.biblioteca.service;

import ar.edu.itu.biblioteca.dao.PrestamoDAO;
import ar.edu.itu.biblioteca.model.PrestamoVencidoResumen;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class VencimientoService {

    private final PrestamoDAO prestamoDAO;

    public VencimientoService() {
        this.prestamoDAO = new PrestamoDAO();
    }

    public List<PrestamoVencidoResumen> listarPrestamosVencidos()
            throws SQLException {

        return listarPrestamosVencidos(
                LocalDate.now()
        );
    }

    public List<PrestamoVencidoResumen> listarPrestamosVencidos(
            LocalDate fechaReferencia
    ) throws SQLException {

        validarFechaReferencia(
                fechaReferencia
        );

        return prestamoDAO.listarPrestamosVencidos(
                fechaReferencia
        );
    }

    public long calcularDiasAtraso(
            PrestamoVencidoResumen prestamo
    ) {

        return calcularDiasAtraso(
                prestamo,
                LocalDate.now()
        );
    }

    public long calcularDiasAtraso(
            PrestamoVencidoResumen prestamo,
            LocalDate fechaReferencia
    ) {

        validarPrestamo(
                prestamo
        );

        validarFechaReferencia(
                fechaReferencia
        );

        if (
                !fechaReferencia.isAfter(
                        prestamo.getFechaVencimiento()
                )
        ) {

            return 0;
        }

        return ChronoUnit.DAYS.between(
                prestamo.getFechaVencimiento(),
                fechaReferencia
        );
    }

    public double calcularPenalizacionPendiente(
            PrestamoVencidoResumen prestamo
    ) {

        return calcularPenalizacionPendiente(
                prestamo,
                LocalDate.now()
        );
    }

    public double calcularPenalizacionPendiente(
            PrestamoVencidoResumen prestamo,
            LocalDate fechaReferencia
    ) {

        validarPrestamo(
                prestamo
        );

        long diasAtraso =
                calcularDiasAtraso(
                        prestamo,
                        fechaReferencia
                );

        if (
                diasAtraso <= 0
        ) {

            return 0.0;
        }

        if (
                diasAtraso > Integer.MAX_VALUE
        ) {

            throw new IllegalArgumentException(
                    "La cantidad de dias de atraso excede el limite permitido."
            );
        }

        return prestamo
                .getUsuario()
                .calcularMulta(
                        (int) diasAtraso
                );
    }

    private void validarPrestamo(
            PrestamoVencidoResumen prestamo
    ) {

        if (
                prestamo == null
        ) {

            throw new IllegalArgumentException(
                    "El prestamo no puede ser nulo."
            );
        }

        if (
                prestamo.getUsuario() == null
        ) {

            throw new IllegalArgumentException(
                    "El prestamo debe tener un usuario asociado."
            );
        }

        if (
                prestamo.getFechaVencimiento() == null
        ) {

            throw new IllegalArgumentException(
                    "El prestamo debe tener una fecha de vencimiento."
            );
        }
    }

    private void validarFechaReferencia(
            LocalDate fechaReferencia
    ) {

        if (
                fechaReferencia == null
        ) {

            throw new IllegalArgumentException(
                    "La fecha de referencia no puede ser nula."
            );
        }
    }
}
