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
import java.util.Scanner;

public class AccesoSocio {
    public static void insertarSocio(Socio socioNuevo) throws BDException {
        String sql = "INSERT INTO socio (dni, nombre, domicilio, telefono, correo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConfigMySql.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, socioNuevo.getDni());
            ps.setString(2, socioNuevo.getNombre());
            ps.setString(3, socioNuevo.getDomicilio());
            ps.setString(4, socioNuevo.getTelefono());
            ps.setString(5, socioNuevo.getCorreo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        }
    }

    public static int borrarSocio(int codigo) throws BDException {
        String sqlEliminar = "DELETE FROM socio WHERE codigo = ?";

        try (Connection conexion = ConfigMySql.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(sqlEliminar)) {

            ps.setInt(1, codigo);
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

    private static void mostrarMenu() {
        System.out.println("----- MENU SOCIOS -----");
        System.out.println("0. Salir");
        System.out.println("1. Insertar socio");
        System.out.println("2. Consultar todos los socios");
        System.out.println("3. Consultar socios por localidad");
        System.out.println("4. Consultar socios sin prestamos");
        System.out.println("5. Consultar socios con prestamos en una fecha");
        System.out.println("6. Consultar socio o socios con mas prestamos");
    }

    private static void probarInsertarSocio(Scanner scanner) throws BDException {
        System.out.print("DNI: ");
        String dni = scanner.nextLine();
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Domicilio: ");
        String domicilio = scanner.nextLine();
        System.out.print("Telefono: ");
        String telefono = scanner.nextLine();
        System.out.print("Correo: ");
        String correo = scanner.nextLine();

        Socio socio = new Socio(0, dni, nombre, domicilio, telefono, correo);
        insertarSocio(socio);
        System.out.println("Se ha insertado un socio en la base de datos.");
    }

    private static void probarSociosPorLocalidad(Scanner scanner) throws BDException {
        System.out.print("Localidad: ");
        String localidad = scanner.nextLine();
        mostrarSocios(
                consultarSociosPorLocalidad(localidad),
                "No existe ningun socio con esa localidad en la base de datos."
        );
    }

    private static void probarSociosSinPrestamos() throws BDException {
        mostrarSocios(
                consultarSociosSinPrestamos(),
                "No existe ningun socio sin prestamos en la base de datos."
        );
    }

    private static void probarSociosConPrestamosFecha(Scanner scanner) throws BDException {
        System.out.print("Fecha de inicio (AAAA-MM-DD): ");
        String fechaTexto = scanner.nextLine();
        mostrarSocios(
                consultarSociosConPrestamosFecha(Date.valueOf(fechaTexto)),
                "No existe ningun socio con prestamos en esa fecha en la base de datos."
        );
    }

    private static void probarSociosConMasPrestamos() throws BDException {
        mostrarSocios(
                consultarSociosConMasPrestamos(),
                "No existe ningun socio con prestamos en la base de datos."
        );
    }

    private static void mostrarSocios(ArrayList<Socio> listaSocios, String mensajeVacio) {
        if (listaSocios.isEmpty()) {
            System.out.println(mensajeVacio);
            return;
        }

        for (Socio socio : listaSocios) {
            System.out.println(socio);
        }
        System.out.println("Se han consultado " + listaSocios.size() + " socios de la base de datos.");
    }

    private static int leerEntero(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine();
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                System.out.println("Introduce un numero entero.");
            }
        }
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