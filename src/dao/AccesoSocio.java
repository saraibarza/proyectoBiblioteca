package dao;

import config.ConfigMySql;
import excepciones.BDException;
import modelo.Socio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AccesoSocio {
    /**
     * Inserta un socio a la base de datos.
     *
     * @param socioNuevo
     * @throws BDException
     */
    public static void insertarSocio(Socio socioNuevo) throws BDException {
        String sqlInsertar = "INSERT INTO socio (dni, nombre, domicilio, telefono, correo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConfigMySql.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(sqlInsertar)) {

            ps.setString(1, socioNuevo.getDni().trim().replace("-", "").replace(" ", "").toUpperCase());
            ps.setString(2, socioNuevo.getNombre());
            ps.setString(3, socioNuevo.getDomicilio());
            ps.setString(4, socioNuevo.getTelefono().trim());
            ps.setString(5, socioNuevo.getCorreo().trim());

            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                throw new BDException("Ya existe un socio con ese DNI.");
            }
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }
    }

    /**
     * Borra un socio de la base de datos.
     *
     * @param dni
     * @return
     * @throws BDException
     */
    public static int borrarSocio(String dni) throws BDException {
        String sqlEliminar = "DELETE FROM socio WHERE dni = ?";

        try (Connection conexion = ConfigMySql.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(sqlEliminar)) {

            ps.setString(1, dni.trim().replace("-", "").replace(" ", "").toUpperCase());
            return ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                return -1;
            }
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }
    }

    /**
     * Consulta todos los socios de la base de datos.
     *
     * @return
     * @throws BDException
     */
    public static ArrayList<Socio> consultarSocios() throws BDException {
        String sqlSeleccion = "SELECT codigo, dni, nombre, domicilio, telefono, correo FROM socio";
        ArrayList<Socio> listaSocios = new ArrayList<>();

        try (Connection conexion = ConfigMySql.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(sqlSeleccion);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                listaSocios.add(mapearSocio(rs));
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }

        return listaSocios;
    }

    /**
     * Consulta un socio por su dni.
     *
     * @param dni
     * @return
     * @throws BDException
     */
    public static Socio consultarSocioPorDni(String dni) throws BDException {
        String sql = "SELECT codigo, dni, nombre, domicilio, telefono, correo FROM socio WHERE dni = ?";

        try (Connection conexion = ConfigMySql.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, dni.trim().replace("-", "").replace(" ", "").toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearSocio(rs);
                }
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }

        return null;
    }

    /**
     * Consulta un socio por su dirección.
     *
     * @param localidad
     * @return
     * @throws BDException
     */
    public static ArrayList<Socio> consultarSociosPorLocalidad(String localidad) throws BDException {
        String sql = "SELECT codigo, dni, nombre, domicilio, telefono, correo " +
                "FROM socio WHERE domicilio LIKE ? ORDER BY nombre ASC";
        ArrayList<Socio> listaSocios = new ArrayList<>();

        try (Connection conexion = ConfigMySql.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, "%" + localidad + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listaSocios.add(mapearSocio(rs));
                }
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }

        return listaSocios;
    }

    /**
     * Consultar los socios que no hayan hecho ningún prestamo.
     *
     * @return
     * @throws BDException
     */
    public static ArrayList<Socio> consultarSociosSinPrestamos() throws BDException {
        String sql = "SELECT s.codigo, s.dni, s.nombre, s.domicilio, s.telefono, s.correo\n" +
                "FROM socio s WHERE s.codigo NOT IN (SELECT p.codigo_socio FROM prestamo p WHERE p.fecha_devolucion IS NULL);";
        ArrayList<Socio> listaSocios = new ArrayList<>();

        try (Connection conexion = ConfigMySql.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                listaSocios.add(mapearSocio(rs));
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }

        return listaSocios;
    }

    /**
     * Consultar un socio que haya hecho un prestamo en una fecha.
     *
     * @param fechaInicio
     * @return
     * @throws BDException
     */
    public static ArrayList<Socio> consultarSociosConPrestamosFecha(String fechaInicio) throws BDException {
        String sql = "SELECT DISTINCT s.codigo, s.dni, s.nombre, s.domicilio, s.telefono, s.correo " +
                "FROM socio s INNER JOIN prestamo p ON s.codigo = p.codigo_socio " +
                "WHERE p.fecha_inicio = ?";
        ArrayList<Socio> listaSocios = new ArrayList<>();

        try (Connection conexion = ConfigMySql.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, fechaInicio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listaSocios.add(mapearSocio(rs));
                }
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }

        return listaSocios;
    }

    /**
     * Consultar el socio o los socios con más préstamos.
     *
     * @return
     * @throws BDException
     */
    public static ArrayList<Socio> consultarSociosConMasPrestamos() throws BDException {
        String sql = "SELECT s.codigo, s.dni, s.nombre, s.domicilio, s.telefono, s.correo " +
                "FROM socio s INNER JOIN prestamo p ON s.codigo = p.codigo_socio " +
                "GROUP BY s.codigo, s.dni, s.nombre, s.domicilio, s.telefono, s.correo " +
                "HAVING COUNT(*) = (" +
                "SELECT MAX(total_prestamos) FROM (" +
                "SELECT COUNT(*) AS total_prestamos " +
                "FROM prestamo GROUP BY codigo_socio" +
                ") AS prestamos_por_socio" +
                ")";
        ArrayList<Socio> listaSocios = new ArrayList<>();

        try (Connection conexion = ConfigMySql.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                listaSocios.add(mapearSocio(rs));
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }

        return listaSocios;
    }

    /**
     * Devuelve todos los datos de un socio.
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private static Socio mapearSocio(ResultSet rs) throws SQLException {
        return new Socio(
                rs.getInt("codigo"),
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("domicilio"),
                rs.getString("telefono"),
                rs.getString("correo")
        );
    }
}
