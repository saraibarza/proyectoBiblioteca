package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.ConfigMySql;
import excepciones.ExcepcionesBiblioteca;
import modelo.Libro;
import modelo.Prestamo;
import excepciones.BDException;
import modelo.Socio;

public class AccesoPrestamo {
    /**
     * Metodo para validar si el libro ha sido ya prestado o no.
     *
     * @param codigoLibro
     * @return
     * @throws BDException
     */
    public static boolean libroDisponible(int codigoLibro) throws BDException {
        Connection conexion = null;

        try {
            conexion = ConfigMySql.abrirConexion();
            String sql = "SELECT COUNT(*) FROM prestamo WHERE codigo_libro = ? AND fecha_devolucion IS NULL";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ps.setInt(1, codigoLibro);
            ResultSet rs = ps.executeQuery();

            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            ConfigMySql.cerrarConexion(conexion);
        }
    }

    /**
     * Metodo para validar si un socio ya ha pedido un libro prestado.
     *
     * @param codigoSocio
     * @return
     * @throws BDException
     */
    public static boolean puedeSocioPrestar(int codigoSocio) throws BDException {
        Connection conexion = null;

        try {
            conexion = ConfigMySql.abrirConexion();
            String sql = "SELECT COUNT(*) FROM prestamo WHERE codigo_socio = ? AND fecha_devolucion IS NULL";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ps.setInt(1, codigoSocio);
            ResultSet rs = ps.executeQuery();

            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            ConfigMySql.cerrarConexion(conexion);
        }
    }

    /**
     * Insertar un préstamo en la base de datos.
     * 13
     *
     * @param prestamo
     * @return
     * @throws BDException
     */
    public static boolean insertarPrestamo(Prestamo prestamo) throws BDException, ExcepcionesBiblioteca {
        int lineasInsertadas = 0;
        Connection conexion = null;

        if(!libroDisponible(prestamo.getCodigo_libro())){
            throw new ExcepcionesBiblioteca(ExcepcionesBiblioteca.LIBRO_NO_DISPONIBLE);
        } else if (!puedeSocioPrestar(prestamo.getCodigo_socio())) {
            throw new ExcepcionesBiblioteca(ExcepcionesBiblioteca.SOCIO_NO_DISPONIBLE);
        }

        try{
            conexion = ConfigMySql.abrirConexion();
            String sql = "INSERT INTO prestamo (codigo_libro, codigo_socio, fecha_inicio, fecha_fin) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ps.setInt(1, prestamo.getCodigo_libro());
            ps.setInt(2, prestamo.getCodigo_socio());
            ps.setString(3, prestamo.getFecha_inicio());
            ps.setString(4, prestamo.getFecha_fin());

            lineasInsertadas = ps.executeUpdate();
        } catch (SQLException e) {
            String mensaje = e.getMessage().toLowerCase();
            if(mensaje.contains("fk_libro_prestamo")){
                throw new ExcepcionesBiblioteca(ExcepcionesBiblioteca.CODIGO_LIBRO_NO_ENCONTRADO);
            } else if (mensaje.contains("fk_socio_prestamo")) {
                throw new ExcepcionesBiblioteca(ExcepcionesBiblioteca.CODIGO_SOCIO_NO_ENCONTRADO);
            } else {
                throw new BDException(BDException.ERROR_QUERY + e.getMessage());
            }
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return lineasInsertadas > 0;
    }

    /**
     * Actualizar un préstamo, por datos identificativos, de la base de datos.
     * Se devuelve el libro.
     * 14
     *
     * @param codigo_libro
     * @param codigo_socio
     * @param fecha_inicio
     * @param fecha_devolucion
     * @return
     * @throws BDException
     */
    public static boolean actualizarDevolucionDelPrestamo(int codigo_libro, int codigo_socio, String fecha_inicio, String fecha_devolucion) throws BDException{
        int filasActualizadas = 0;
        Connection conexion = null;

        try{
            conexion = ConfigMySql.abrirConexion();
            String sql = "UPDATE prestamo SET fecha_devolucion = ? WHERE codigo_libro = ? AND codigo_socio = ? AND fecha_inicio = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ps.setString(1, fecha_devolucion);
            ps.setInt(2, codigo_libro);
            ps.setInt(3, codigo_socio);
            ps.setString(4, fecha_inicio);

            filasActualizadas = ps.executeUpdate();
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return filasActualizadas > 0;
    }

    /**
     * Eliminar un préstamo, por datos identificativos, de la base de datos.
     * 15
     *
     * @param codigo_libro
     * @param codigo_socio
     * @param fecha_inicio
     * @return
     * @throws BDException
     */
    public static boolean eliminarPrestamo(int codigo_libro, int codigo_socio, String fecha_inicio) throws BDException{
        int filasEliminadas = 0;
        PreparedStatement ps = null;
        Connection conexion = null;

        try{
            conexion = ConfigMySql.abrirConexion();
            String sql = "DELETE FROM prestamo WHERE codigo_libro = ? AND codigo_socio = ? AND fecha_inicio = ?";
            ps = conexion.prepareStatement(sql);

            ps.setInt(1, codigo_libro);
            ps.setInt(2, codigo_socio);
            ps.setString(3, fecha_inicio);

            filasEliminadas = ps.executeUpdate();
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return filasEliminadas > 0;
    }

    /**
     * Consultar todos los préstamos de la base de datos.
     * 16
     *
     * @return
     * @throws BDException
     */
    public static List<Prestamo> consultarTodosLosPrestamos() throws BDException {
        List<Prestamo> prestamos = new ArrayList<>();
        Connection conexion = null;

        try {
            conexion = ConfigMySql.abrirConexion();
            String sql = "SELECT * FROM prestamo";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Prestamo prestamo = new Prestamo(
                        rs.getInt("codigo_libro"),
                        rs.getInt("codigo_socio"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_fin"),
                        rs.getString("fecha_devolucion")
                );
                prestamos.add(prestamo);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            ConfigMySql.cerrarConexion(conexion);
        }
        return prestamos;
    }

    /**
     * Consultar los préstamos no devueltos de la base de datos.
     * 17
     *
     * @return
     * @throws BDException
     */
    public static List<Prestamo> consultarTodosLosPrestamosNoDevueltos() throws BDException{
        List<Prestamo> prestamosNoDevueltos = new ArrayList<>();
        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            conexion = ConfigMySql.abrirConexion();
            String sql = "SELECT * FROM prestamo WHERE fecha_devolucion IS NULL";
            ps = conexion.prepareStatement(sql);

            rs = ps.executeQuery();
            while(rs.next()){
                Prestamo prestamo = new Prestamo(
                        rs.getInt("codigo_libro"),
                        rs.getInt("codigo_socio"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_fin")
                );
                prestamosNoDevueltos.add(prestamo);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return prestamosNoDevueltos;
    }

    /**
     * Consultar DNI y nombre de socio, ISBN y título de libro y fecha de devolución de los
     * préstamos realizados en una fecha de la base de datos.
     * 18
     *
     * @param fecha
     * @return
     * @throws BDException
     */
    public static List<String[]> consultarInfrmacionDePrestamos(String fecha) throws BDException {
        List<String[]> prestamos = new ArrayList<>();
        Connection conexion = null;

        try {
            conexion = ConfigMySql.abrirConexion();
            String sql = "SELECT s.dni, s.nombre, l.isbn, l.titulo, p.fecha_devolucion " +
                    "FROM prestamo p JOIN socio s ON p.codigo_socio = s.codigo " +
                    "JOIN libro l ON p.codigo_libro = l.codigo WHERE p.fecha_inicio = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ps.setString(1, fecha);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String[] prestamo = new String[5];
                prestamo[0] = rs.getString("dni");
                prestamo[1] = rs.getString("nombre");
                prestamo[2] = rs.getString("isbn");
                prestamo[3] = rs.getString("titulo");
                prestamo[4] = rs.getString("fecha_devolucion");
                prestamos.add(prestamo);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            ConfigMySql.cerrarConexion(conexion);
        }
        return prestamos;
    }

    /**
     * Consultar los libros que han sido prestados (incluyendo los libros no devueltos) una cantidad
     * de veces inferior a la media.
     * 21
     *
     * @return
     * @throws BDException
     */
    public static List<Libro> librosPrestadosInferiorMedia() throws BDException {
        List<Libro> libros = new ArrayList<>();
        Connection conexion = null;

        try{
            conexion = ConfigMySql.abrirConexion();
            String sql = "SELECT l.* FROM libro l, prestamo p " +
                    "WHERE l.codigo = p.codigo_libro " +
                    "GROUP BY l.codigo " +
                    "HAVING COUNT(*) < (SELECT COUNT(*) / COUNT(DISTINCT codigo_libro) FROM prestamo);";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Libro libro = new Libro(
                        rs.getInt("codigo"),
                        rs.getString("isbn"),
                        rs.getString("titulo"),
                        rs.getString("escritor"),
                        rs.getInt("año_publicacion"),
                        rs.getDouble("puntuacion")
                );
                libros.add(libro);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            ConfigMySql.cerrarConexion(conexion);
        }
        return libros;
    }

    /**
     * Consultar los socios que han realizado una cantidad de préstamos superior a la media.
     * 22
     *
     * @return
     * @throws BDException
     */
    public static List<Socio> sociosPrestadosSuperiorMedia() throws BDException {
        List<Socio> socios = new ArrayList<>();
        Connection conexion = null;

        try{
            conexion = ConfigMySql.abrirConexion();
            String sql = "SELECT s.* FROM socio s" +
                    " JOIN prestamo p ON s.codigo = p.codigo_socio" +
                    " GROUP BY s.codigo" +
                    " HAVING COUNT(*) > (SELECT COUNT(*) / COUNT(DISTINCT codigo_socio) FROM prestamo)";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Socio socio = new Socio(
                        rs.getInt("codigo"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("domicilio"),
                        rs.getString("telefono"),
                        rs.getString("correo")
                );
                socios.add(socio);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            ConfigMySql.cerrarConexion(conexion);
        }
        return socios;
    }

    /**
     * Consultar el ISBN, el título y el número de veces de los libros que han sido prestados,
     * ordenados por el número de préstamos descendente.
     * 23
     *
     * @return
     * @throws BDException
     */
    public static List<String[]> vecesLibrosPrestados() throws BDException{
        List<String[]> prestamos = new ArrayList<>();
        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            conexion = ConfigMySql.abrirConexion();
            String sql = "SELECT l.isbn, l.titulo, COUNT(*) AS total" +
                    " FROM libro l JOIN prestamo p ON l.codigo = p.codigo_libro" +
                    " GROUP BY l.codigo, l.isbn, l.titulo ORDER BY total DESC";
            ps = conexion.prepareStatement(sql);

            rs = ps.executeQuery();
            while(rs.next()){
                String[] prestamo = new String[3];
                prestamo[0] = rs.getString("isbn");
                prestamo[1] = rs.getString("titulo");
                prestamo[2] = rs.getString("total");
                prestamos.add(prestamo);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return prestamos;
    }

    /**
     * Consultar el DNI, el nombre y el número de veces de los socios que han realizado préstamos,
     * ordenados por el número de préstamos descendente.
     * 24
     *
     * @return
     * @throws BDException
     */
    public static List<String[]> vecesSociosPrestados() throws BDException{
        List<String[]> prestamos = new ArrayList<>();
        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            conexion = ConfigMySql.abrirConexion();
            String sql = "SELECT s.dni, s.nombre, COUNT(*) AS total" +
                    " FROM socio s JOIN prestamo p ON s.codigo = p.codigo_socio" +
                    " GROUP BY s.codigo, s.dni, s.nombre ORDER BY total DESC";
            ps = conexion.prepareStatement(sql);

            rs = ps.executeQuery();
            while(rs.next()){
                String[] prestamo = new String[3];
                prestamo[0] = rs.getString("dni");
                prestamo[1] = rs.getString("nombre");
                prestamo[2] = rs.getString("total");
                prestamos.add(prestamo);
            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return prestamos;
    }
}
