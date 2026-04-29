package dao;

import config.ConfigMySql;
import excepciones.BDException;
import modelo.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccesoLibro {

    /**
     * metodo insertar libro en la base de datos
     */

    public boolean insertar(Libro libro) throws BDException, SQLException {
        int lineasInsertadas = 0;
        Connection conexion = null;
        try {

            conexion = ConfigMySql.abrirConexion();
            String sql = "INSERT INTO libro (isbn, titulo, escritor,año_publicacion, puntuacion) " +
                    "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = conexion.prepareStatement(sql);

            ps.setString(1, libro.getIsbn());
            ps.setString(2, libro.getTitulo());
            ps.setString(3, libro.getEscritor());
            ps.setInt(4, libro.getAño_publicacion());
            ps.setDouble(5, libro.getPuntuacion());

            lineasInsertadas = ps.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return lineasInsertadas > 0;
    }

    /**
     * metodo eliminar un libro, por isbn, de la base de datos
     */

    public static boolean eliminarLibro(String isbn) throws BDException, SQLException {
        int lineasEliminadas = 0;
        PreparedStatement ps = null;
        Connection conexion = null;

        try {
            conexion = ConfigMySql.abrirConexion();
            String sql = "DELETE FROM libro WHERE isbn = ?";

            ps = conexion.prepareStatement(sql);
            ps.setString(1, isbn);

            lineasEliminadas = ps.executeUpdate();
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return lineasEliminadas > 0;
    }


    /**
     * consultar todos los libros de la base de datos
     */

    public static List<Libro> consultarLibros() throws BDException, SQLException {
        List<Libro> libros = new ArrayList<>();
        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = ConfigMySql.abrirConexion();

            String sql = "SELECT * FROM libro";

            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

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
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return libros;
    }

    /**
     * consultar varios libros, por escritor, de la base de datos, ordenados por puntuacion descendiente
     *
     */

    public static List<Libro> consultarPorEscritor(String escritor) throws BDException {
        List<Libro> libros = new ArrayList<>();
        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = ConfigMySql.abrirConexion();

            String sql = "SELECT * FROM libro WHERE escritor = ? ORDER BY puntuacion DESC";

            ps = conexion.prepareStatement(sql);
            ps.setString(1, escritor);
            rs = ps.executeQuery();

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
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return libros;
    }


    /**
     * consultar los libros no prestados  de la base de datos
     */

    public static List<Libro> consultarNoPrestados() throws BDException {
        List<Libro> libros = new ArrayList<>();
        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = ConfigMySql.abrirConexion();

            String sql = "SELECT codigo, isbn, titulo, escritor, año_publicacion, puntuacion\n" +
                    "FROM libro WHERE codigo NOT IN (SELECT codigo_libro FROM prestamo WHERE fecha_devolucion IS NULL);";

            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

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
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return libros;
    }


    /**
     * consultar los libros devueltos, en una fecha, de la base de datos
     */

    public static List<Libro> consultarDevueltosPorFecha(String fecha) throws BDException {
        List<Libro> libros = new ArrayList<>();
        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = ConfigMySql.abrirConexion();

            String sql = "SELECT libro.* FROM libro " + "JOIN prestamo ON libro.codigo = prestamo.codigo_libro " + "WHERE prestamo.fecha_devolucion = ?";

            ps = conexion.prepareStatement(sql);
            ps.setString(1, fecha);
            rs = ps.executeQuery();

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
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return libros;
    }

    /**
     * consultar el libro o libros que han sido prestados menos veces con un minimo de un libro prestado
     */

    public static List<Libro> consultarMenosPrestados() throws BDException {
        List<Libro> libros = new ArrayList<>();
        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conexion = ConfigMySql.abrirConexion();

            String sql = "SELECT libro.* FROM libro " +
                    "JOIN prestamo ON libro.codigo = prestamo.codigo_libro " +
                    "GROUP BY libro.codigo " +
                    "HAVING COUNT(*) = (" +
                    "    SELECT MIN(veces) FROM (" +
                    "        SELECT COUNT(*) AS veces FROM prestamo " +
                    "        GROUP BY codigo_libro" +
                    "    ) AS conteos" +
                    ")";

            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

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
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }
        return libros;
    }
}