package dao;

import config.ConfigMySql;
import excepciones.BDException;
import modelo.Socio;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AccesoSocio {

    public static void insertarSocio(Socio socioNuevo) throws BDException {
        String dni = normalizarDni(socioNuevo.getDni());
        if (!validarDni(dni)) {
            throw new BDException("DNI no valido.");
        }

        String sql = "INSERT INTO socio (dni, nombre, domicilio, telefono, correo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConfigMySql.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, dni);
            ps.setString(2, socioNuevo.getNombre());
            ps.setString(3, socioNuevo.getDomicilio());
            ps.setString(4, socioNuevo.getTelefono());
            ps.setString(5, socioNuevo.getCorreo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }
    }

    public static int borrarSocio(String dni) throws BDException {
        String dniNormalizado = normalizarDni(dni);
        if (!validarDni(dniNormalizado)) {
            throw new BDException("DNI no valido.");
        }

        String sqlEliminar = "DELETE FROM socio WHERE dni = ?";

        try (Connection conexion = ConfigMySql.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(sqlEliminar)) {

            ps.setString(1, dniNormalizado);
            return ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                return -1;
            }
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }
    }

    public static ArrayList<Socio> consultarSocios() throws BDException {
        String sql = "SELECT codigo, dni, nombre, domicilio, telefono, correo FROM socio";
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

    public static Socio consultarSocioPorDni(String dni) throws BDException {
        String dniNormalizado = normalizarDni(dni);
        if (!validarDni(dniNormalizado)) {
            throw new BDException("DNI no valido.");
        }

        String sql = "SELECT codigo, dni, nombre, domicilio, telefono, correo FROM socio WHERE dni = ?";

        try (Connection conexion = ConfigMySql.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, dniNormalizado);

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

    public static ArrayList<Socio> consultarSociosSinPrestamos() throws BDException {
        String sql = "SELECT s.codigo, s.dni, s.nombre, s.domicilio, s.telefono, s.correo " +
                "FROM socio s LEFT JOIN prestamo p ON s.codigo = p.codigo_socio " +
                "WHERE p.codigo_socio IS NULL";
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

    public static ArrayList<Socio> consultarSociosConPrestamosFecha(Date fechaInicio) throws BDException {
        String sql = "SELECT DISTINCT s.codigo, s.dni, s.nombre, s.domicilio, s.telefono, s.correo " +
                "FROM socio s INNER JOIN prestamo p ON s.codigo = p.codigo_socio " +
                "WHERE p.fecha_inicio = ?";
        ArrayList<Socio> listaSocios = new ArrayList<>();

        try (Connection conexion = ConfigMySql.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setDate(1, fechaInicio);

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

    public static boolean validarDni(String dni) {
        if (dni == null) {
            return false;
        }

        String dniNormalizado = normalizarDni(dni);
        if (!dniNormalizado.matches("\\d{8}[A-Z]")) {
            return false;
        }

        int numero = Integer.parseInt(dniNormalizado.substring(0, 8));
        char letra = dniNormalizado.charAt(8);
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";

        return letras.charAt(numero % 23) == letra;
    }

    private static String normalizarDni(String dni) {
        return dni == null ? null : dni.trim().toUpperCase();
    }

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