package vista;

import dao.AccesoLibro;
import dao.AccesoPrestamo;
import dao.AccesoSocio;
import entrada.Teclado;
import excepciones.BDException;
import excepciones.ExcepcionesBiblioteca;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Socio;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

public class Principal {
    /**
     * Valida el isbn del libro
     *
     * @param isbn
     * @return
     */
    public static boolean validarIsbn(String isbn) {
        if (isbn == null) return false;
        String digits = isbn.replace("-", "").replace(" ", "");
        if (!digits.matches("\\d{13}")) return false;
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += (digits.charAt(i) - '0') * (i % 2 == 0 ? 1 : 3);
        }
        int check = (10 - (sum % 10)) % 10;
        return check == (digits.charAt(12) - '0');
    }

    /**
     * Valida dni
     *
     * @param dni
     * @return
     */
    private static boolean validarDni(String dni) {
        if (dni == null) {
            return false;
        }

        String dniNormalizado = normalizarDni(dni);

        if (dniNormalizado.length() != 9) {
            return false;
        }

        String numeros = dniNormalizado.substring(0, 8);
        char letra = dniNormalizado.charAt(8);

        if (!numeros.matches("\\d{8}") || !Character.isLetter(letra)) {
            return false;
        }

        int numero = Integer.parseInt(numeros);
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";

        return letras.charAt(numero % 23) == letra;
    }

    /**
     * Valida el teléfono
     *
     * @param telefono
     * @return
     */
    private static boolean validarTelefono(String telefono) {
        if (telefono == null) {
            return false;
        }

        telefono = telefono.trim();
        return telefono.matches("\\d{9}");
    }

    private static boolean validarCorreo(String correo) {
        if (correo == null) {
            return false;
        }

        correo = correo.trim();
        return correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Quita los espacios en blanco y la letra la pone en mayusculas
     *
     * @param dni
     * @return
     */
    private static String normalizarDni(String dni) {
        return dni.trim().replace("-", "").replace(" ", "").toUpperCase();
    }

    /**
     * Método que valida las fechas introducidas por el usuario
     *
     * @param fechaStr
     * @throws Exception
     * @throws ExcepcionesBiblioteca
     */
    public static boolean validarFecha(String fechaStr) throws Exception, ExcepcionesBiblioteca {
        if (fechaStr == null) {
            throw new ExcepcionesBiblioteca(ExcepcionesBiblioteca.FECHA_NULA);
        }
        if (fechaStr.trim().isEmpty()) {
            throw new ExcepcionesBiblioteca(ExcepcionesBiblioteca.FECHA_VACIA);
        }
        try {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
            LocalDate.parse(fechaStr, formato);
        } catch (DateTimeParseException e) {
            throw new ExcepcionesBiblioteca(ExcepcionesBiblioteca.FORMATO_FECHA_INCORRECTO);
        }
        return true;
    }

    /**
     * Menú.
     *
     * @return
     */
    public static int menu() {
        System.out.println("MENU");
        System.out.println("0. Salir del programa");
        System.out.println("1. Insertar un libro en la base de datos");
        System.out.println("2. Eliminar un libro, por isbn, de la base de datos");
        System.out.println("3. Consultar todos los libros de la base de datos");
        System.out.println("4. Consultar varios libros, por escritor, de la base de datos, ordenados por puntuación descendente");
        System.out.println("5. Consultar los libros no prestados de la base de datos");
        System.out.println("6. Consultar los libros devueltos, en una fecha, de la base de datos");
        System.out.println("7. Insertar un socio en la base de datos.");
        System.out.println("8. Eliminar un socio, por dni, de la base de datos.");
        System.out.println("9. Consultar todos los socios de la base de datos.");
        System.out.println("10. Consultar varios socios, por domicilio, de la base de datos.");
        System.out.println("11. Consultar los socios sin prestamos de la base de datos.");
        System.out.println("12. Consultar los socios con prestamos en una fecha de la base de datos.");
        System.out.println("13. Insertar un prestamo en la base de datos.");
        System.out.println("14. Actualizar la fecha de devolucion de un prestamo en la base de datos.");
        System.out.println("15. Eliminar un prestamo en la base de datos.");
        System.out.println("16. Consultar todos los prestamos de la base de datos.");
        System.out.println("17. Consultar los prestamos no devueltos de la base de datos.");
        System.out.println("18. Consultar DNI y nombre de socio, ISBN y titulo de libro y fecha de devolucion.");
        System.out.println("19. Consultar el libro o los libros que ha/n sido prestado/s menos veces (y que como mínimo haya/n sido prestado/s una vez).");
        System.out.println("20. Consultar el socio o los socios que ha/n realizado más préstamos.");
        System.out.println("21. Consultar los libros que han sido prestados una cantidad de veces inferior a la media.");
        System.out.println("22. Consultar los socios que han realizado una cantidad de prestamos superior a la media.");
        System.out.println("23. Consultar el ISBN, el titulo y el numero de veces de los libros prestados.");
        System.out.println("24. Consultar el DNI, el nombre y el numero de veces de los socios con prestamos.");
        System.out.println("0. Finalizar programa");
        return Teclado.leerEntero("Opcion: ");
    }

    /**
     *
     * @param args
     * @throws BDException
     * @throws ExcepcionesBiblioteca
     */
    public static void main(String[] args) throws BDException, ExcepcionesBiblioteca {
        int opcion = 1;
        do {
            try {
                opcion = menu();

                switch (opcion) {
                    // Insertar un libro en la base de datos.
                    case 1: {
                        String isbn = Teclado.leerCadena("ISBN: ");
                        if (validarIsbn(isbn)) {
                            String titulo = Teclado.leerCadena("Título: ");
                            String escritor = Teclado.leerCadena("Escritor: ");
                            int año_publicacion = Teclado.leerEntero("Año de publicación: ");
                            double puntuacion = Teclado.leerReal("Puntuación:");

                            Libro libro = new Libro(0, isbn, titulo, escritor, año_publicacion, puntuacion);
                            AccesoLibro accesoLibro = new AccesoLibro();

                            if (accesoLibro.insertar(libro)) {
                                System.out.println("Se ha insertado un libro en la base de datos.");
                            }
                        } else {
                            System.out.println("La ISBN tiene el formato incorrecto.");
                        }
                        break;
                    }

                    // Eliminar un libro, por código, de la base de datos.
                    case 2: {
                        String isbn = Teclado.leerCadena("Isbn del libro a eliminar:");
                        if (validarIsbn(isbn)) {
                            boolean resultado = AccesoLibro.eliminarLibro(isbn);

                            if (resultado == true) {
                                System.out.println("Se ha eliminado un libro de la base de datos.");
                            } else {
                                System.out.println("El libro no se ha podido eliminar de la base de datos.");
                            }
                        } else {
                            System.out.println("La ISBN tiene el formato incorrecto.");
                        }
                        break;
                    }

                    // Consultar todos los libros de la base de datos.
                    case 3: {
                        List<Libro> libros3 = AccesoLibro.consultarLibros();

                        if (libros3.isEmpty()) {
                            System.out.println("No se ha encontrado ningún libro en la base de datos.");
                        } else {
                            for (Libro l : libros3) {
                                System.out.println(l);
                            }
                            System.out.println("Se han consultado " + libros3.size() + " libros de la base de datos.");
                        }
                        break;
                    }

                    // Consultar varios libros, por escritor, de la base de datos, ordenados por puntuación
                    //decendente.
                    case 4: {
                        String escritorBuscar = Teclado.leerCadena("Escritor: ");
                        List<Libro> libros4 = AccesoLibro.consultarPorEscritor(escritorBuscar);

                        if (libros4.isEmpty()) {
                            System.out.println("No existe ningún libro con ese escritor en la base de datos.");
                        } else {
                            for (Libro l : libros4) {
                                System.out.println(l);
                            }
                            System.out.println("Se han consultado " + libros4.size() + " libros de la base de datos.");
                        }
                        break;
                    }

                    // Consultar los libros no prestados de la base de datos.
                    case 5: {
                        List<Libro> libros5 = AccesoLibro.consultarNoPrestados();

                        if (libros5.isEmpty()) {
                            System.out.println("No existe ningún libro no prestado en la base de datos.");
                        } else {
                            for (Libro l : libros5) {
                                System.out.println(l);
                            }
                            System.out.println("Se han consultado " + libros5.size() + " libros de la base de datos.");
                        }
                        break;
                    }

                    // Consultar los libros devueltos, en una fecha, de la base de datos.
                    case 6: {
                        String fecha = Teclado.leerCadena("Fecha de devolución (uuuu-MM-dd): ");
                        if (validarFecha(fecha)) {
                            List<Libro> libros6 = AccesoLibro.consultarDevueltosPorFecha(fecha);

                            if (libros6.isEmpty()) {
                                System.out.println("No existe ningún libro devuelto en esa fecha en la base de datos.");
                            } else {
                                for (Libro l : libros6) {
                                    System.out.println(l);
                                }
                                System.out.println("Se han consultado " + libros6.size() + " libros de la base de datos.");
                            }
                        }
                        break;
                    }

                    // Insertar un socio en la base de datos.
                    case 7: {
                        String dniInsertar = Teclado.leerCadena("DNI: ");

                        if (!validarDni(dniInsertar)) {
                            System.out.println("El DNI no es valido.");
                        } else {
                            String nombre = Teclado.leerCadena("Nombre: ");
                            String domicilio = Teclado.leerCadena("Domicilio: ");
                            String telefono = Teclado.leerCadena("Telefono: ");
                            String correo = Teclado.leerCadena("Correo: ");

                            if (!validarTelefono(telefono)) {
                                System.out.println("El telefono no es valido.");
                            } else if (!validarCorreo(correo)) {
                                System.out.println("El correo no es valido.");
                            } else {
                                Socio socioInsertar = new Socio(
                                        0,
                                        normalizarDni(dniInsertar),
                                        nombre,
                                        domicilio,
                                        telefono.trim(),
                                        correo.trim()
                                );

                                AccesoSocio.insertarSocio(socioInsertar);
                                System.out.println("Se ha insertado un socio en la base de datos.");
                            }
                        }
                        break;
                    }

                    // Eliminar un socio, por código, de la base de datos.
                    case 8: {
                        String dniEliminar = Teclado.leerCadena("DNI del socio: ");
                        if (validarDni(dniEliminar)) {
                            int resultadoEliminar = AccesoSocio.borrarSocio(dniEliminar);
                            if (resultadoEliminar > 0) {
                                System.out.println("Se ha eliminado un socio de la base de datos.");
                            } else if (resultadoEliminar == 0) {
                                System.out.println("No existe ningun socio con ese DNI en la base de datos.");
                            } else {
                                System.out.println("El socio esta referenciado en un prestamo de la base de datos.");
                            }
                        } else {
                            System.out.println("El dni del socio no es valido.");
                        }
                        break;
                    }

                    // Consultar todos los socios de la base de datos.
                    case 9: {
                        ArrayList<Socio> socios = AccesoSocio.consultarSocios();
                        if (socios.isEmpty()) {
                            System.out.println("No se ha encontrado ningun socio en la base de datos.");
                        } else {
                            for (Socio socioListado : socios) {
                                System.out.println(socioListado);
                            }
                            System.out.println("Se han consultado " + socios.size() + " socios de la base de datos.");
                        }
                        break;
                    }

                    // Consultar varios socios, por localidad, de la base de datos, ordenados por nombre
                    //ascendente.
                    case 10: {
                        String localidad = Teclado.leerCadena("Domicilio: ");
                        ArrayList<Socio> sociosLocalidad = AccesoSocio.consultarSociosPorLocalidad(localidad);
                        if (sociosLocalidad.isEmpty()) {
                            System.out.println("No existe ningun socio con esa localidad en la base de datos.");
                        } else {
                            for (Socio socioLocalidad : sociosLocalidad) {
                                System.out.println(socioLocalidad);
                            }
                            System.out.println("Se han consultado " + sociosLocalidad.size() + " socios de la base de datos.");
                        }
                        break;
                    }

                    // Consultar los socios sin préstamos de la base de datos.
                    case 11: {
                        ArrayList<Socio> sociosSinPrestamos = AccesoSocio.consultarSociosSinPrestamos();
                        if (sociosSinPrestamos.isEmpty()) {
                            System.out.println("No existe ningun socio sin prestamos en la base de datos.");
                        } else {
                            for (Socio socioSinPrestamos : sociosSinPrestamos) {
                                System.out.println(socioSinPrestamos);
                            }
                            System.out.println("Se han consultado " + sociosSinPrestamos.size() + " socios de la base de datos.");
                        }
                        break;
                    }

                    // Consultar los socios con préstamos en una fecha de la base de datos.
                    case 12: {
                        String fechaTexto = Teclado.leerCadena("Fecha de inicio (uuuu-MM-dd): ");
                        if (validarFecha(fechaTexto)) {

                            ArrayList<Socio> sociosFecha = AccesoSocio.consultarSociosConPrestamosFecha(fechaTexto);
                            if (sociosFecha.isEmpty()) {
                                System.out.println("No existe ningun socio con prestamos en esa fecha en la base de datos.");
                            } else {
                                for (Socio socioFecha : sociosFecha) {
                                    System.out.println(socioFecha);
                                }
                                System.out.println("Se han consultado " + sociosFecha.size() + " socios de la base de datos.");
                            }
                        }
                        break;
                    }

                    // Insertar un préstamo en la base de datos.
                    case 13: {
                        String titulo = Teclado.leerCadena("Titulo: ");
                        List<Libro> libros = AccesoPrestamo.consultarLibrosPorTitulo(titulo);
                        if (!libros.isEmpty()) {
                            for (Libro libro : libros) {
                                System.out.println(libro);
                            }
                            int codigo_libro = Teclado.leerEntero("Código del libro: ");
                            String dni = Teclado.leerCadena("DNI del socio: ");
                            if (validarDni(dni)) {
                                int codigo_socio = AccesoPrestamo.dniPorCodigoSocio(dni);
                                Prestamo prestamo = new Prestamo(codigo_libro, codigo_socio, LocalDate.now().toString(), LocalDate.now().plusDays(30).toString());

                                if (AccesoPrestamo.insertarPrestamo(prestamo)) {
                                    System.out.println("Se ha insertado el prestamo correctamente, tienes 30 días para devolverlo.");
                                } else {
                                    System.out.println("No se ha podido insertar el prestamo.");
                                }
                            } else {
                                System.out.println("El dni no tiene el formato correcto.");
                            }
                        } else {
                            System.out.println("No tenemos disponible ningún libro por ese título.");
                        }
                        break;
                    }

                    // Actualizar un préstamo, por datos identificativos, de la base de datos.
                    case 14: {
                        String isbn = Teclado.leerCadena("ISBN del libro: ");
                        if (validarIsbn(isbn)) {
                            String dni = Teclado.leerCadena("DNI del socio: ");
                            if (validarDni(dni)) {
                                String fecha_inicio = Teclado.leerCadena("Fecha de inicio: ");
                                if (validarFecha(fecha_inicio)) {
                                    if (AccesoPrestamo.actualizarDevolucionDelPrestamo(isbn, dni, fecha_inicio, LocalDate.now().toString())) {
                                        System.out.println("Se ha modificado el prestamo correctamente.");
                                    } else {
                                        System.out.println("No se ha podido modificar el prestamo.");
                                    }
                                }
                            } else {
                                System.out.println("El dni no tiene el formato correcto.");
                            }
                        } else {
                            System.out.println("La ISBN tiene el formato incorrecto.");
                        }
                        break;
                    }

                    // Eliminar un préstamo, por datos identificativos, de la base de datos.
                    case 15: {
                        String isbn = Teclado.leerCadena("ISBN del libro: ");
                        if (validarIsbn(isbn)) {
                            String dni = Teclado.leerCadena("DNI del socio: ");
                            if (validarDni(dni)) {
                                String fecha_inicio = Teclado.leerCadena("Fecha de inicio: ");
                                if (validarFecha(fecha_inicio)) {
                                    if (AccesoPrestamo.eliminarPrestamo(isbn, dni, fecha_inicio)) {
                                        System.out.println("Se ha eliminado el prestamo correctamente.");
                                    } else {
                                        System.out.println("No se ha podido eliminar el prestamo.");
                                    }
                                }
                            } else {
                                System.out.println("El dni no tiene el formato correcto.");
                            }
                        } else {
                            System.out.println("La ISBN tiene el formato incorrecto.");
                        }
                        break;
                    }

                    // Consultar todos los préstamos de la base de datos.
                    case 16: {
                        List<Prestamo> prestamos = AccesoPrestamo.consultarTodosLosPrestamos();

                        if (prestamos.isEmpty()) {
                            System.out.println("No hay ningún prestamo.");
                        } else {
                            for (Prestamo prestamo : prestamos) {
                                System.out.println(prestamo.toString());
                            }
                        }
                        break;
                    }

                    // Consultar los préstamos no devueltos de la base de datos.
                    case 17: {
                        List<Prestamo> prestamos = AccesoPrestamo.consultarTodosLosPrestamosNoDevueltos();

                        if (prestamos.isEmpty()) {
                            System.out.println("No hay nigún prestamo no devuelto.");
                        } else {
                            for (Prestamo prestamo : prestamos) {
                                System.out.println(prestamo.toString());
                            }
                        }
                        break;
                    }

                    // Consultar DNI y nombre de socio, ISBN y título de libro y fecha de devolución de los
                    // préstamos realizados en una fecha de la base de datos.
                    case 18: {
                        String fecha_inicio = Teclado.leerCadena("Fecha de inicio: ");
                        if (validarFecha(fecha_inicio)) {
                            List<String[]> prestamos = AccesoPrestamo.consultarInformacionDePrestamos(fecha_inicio);
                            if (prestamos.isEmpty()) {
                                System.out.println("No se ha encontrado nigún prestamo en esa fecha.");
                            } else {
                                for (String[] prestamo : prestamos) {
                                    System.out.println("DNI: " + prestamo[0]);
                                    System.out.println("Nombre: " + prestamo[1]);
                                    System.out.println("ISBN: " + prestamo[2]);
                                    System.out.println("Titulo: " + prestamo[3]);
                                    System.out.println("Fecha de devolución: " + prestamo[4]);
                                }
                            }
                        }
                        break;
                    }

                    // Consultar el libro o los libros que ha/n sido prestado/s menos veces (y que como mínimo
                    //haya/n sido prestado/s una vez).
                    case 19: {
                        List<Libro> libros19 = AccesoLibro.consultarMenosPrestados();

                        if (libros19.isEmpty()) {
                            System.out.println("No existe ningún libro prestado en la base de datos.");
                        } else {
                            for (Libro l : libros19) {
                                System.out.println(l);
                            }
                            System.out.println("Se han consultado " + libros19.size() + " libros de la base de datos.");
                        }
                        break;
                    }

                    // Consultar el socio o los socios que ha/n realizado más préstamos.
                    case 20: {
                        ArrayList<Socio> sociosMasPrestamos = AccesoSocio.consultarSociosConMasPrestamos();
                        if (sociosMasPrestamos.isEmpty()) {
                            System.out.println("No existe ningun socio con prestamos en la base de datos.");
                        } else {
                            for (Socio socioMasPrestamos : sociosMasPrestamos) {
                                System.out.println(socioMasPrestamos);
                            }
                            System.out.println("Se han consultado " + sociosMasPrestamos.size() + " socios de la base de datos.");
                        }
                        break;
                    }

                    // Consultar los libros que han sido prestados (incluyendo los libros no devueltos) una cantidad
                    // de veces inferior a la media.
                    case 21: {
                        List<Libro> libros = AccesoPrestamo.librosPrestadosInferiorMedia();

                        if (libros.isEmpty()) {
                            System.out.println("Ningún libro ha sido prestado.");
                        } else {
                            for (Libro libro : libros) {
                                System.out.println(libro.toString());
                            }
                        }
                        break;
                    }

                    // Consultar los socios que han realizado una cantidad de préstamos superior a la media.
                    case 22: {
                        List<Socio> socios = AccesoPrestamo.sociosPrestadosSuperiorMedia();

                        if (socios.isEmpty()) {
                            System.out.println("Nigún socio ha hecho un prestamo.");
                        } else {
                            for (Socio socio : socios) {
                                System.out.println(socio.toString());
                            }
                        }
                        break;
                    }

                    // Consultar el ISBN, el título y el número de veces de los libros que han sido prestados,
                    // ordenados por el número de préstamos descendente.
                    case 23: {
                        List<String[]> totalVecesLibrosPrestados = AccesoPrestamo.vecesLibrosPrestados();

                        if (totalVecesLibrosPrestados.isEmpty()) {
                            System.out.println("Ningún libro ha sido prestado.");
                        } else {
                            for (String[] libro : totalVecesLibrosPrestados) {
                                System.out.println("ISBN: " + libro[0]);
                                System.out.println("Titulo: " + libro[1]);
                                System.out.println("Total de veces que ha sido el libro prestado: " + libro[2]);
                            }
                        }
                        break;
                    }

                    // Consultar el DNI, el nombre y el número de veces de los socios que han realizado préstamos,
                    // ordenados por el número de préstamos descendente.
                    case 24: {
                        List<String[]> totalVecesSociosPrestados = AccesoPrestamo.vecesSociosPrestados();

                        if (totalVecesSociosPrestados.isEmpty()) {
                            System.out.println("Ningún socio ha hecho un prestamo.");
                        } else {
                            for (String[] socio : totalVecesSociosPrestados) {
                                System.out.println("DNI: " + socio[0]);
                                System.out.println("Nombre: " + socio[1]);
                                System.out.println("Total de veces que ha realizado un prestamo: " + socio[2]);
                            }
                        }
                        break;
                    }
                    case 0: {
                        System.out.println("Saliendo del programa...");
                        break;
                    }
                    default: {
                        System.out.println("La opcion del menu no es valida.");
                        break;
                    }
                }
            } catch (ExcepcionesBiblioteca e) {
                System.out.println("ERROR: " + e.getMessage());
            } catch (BDException e) {
                System.out.println("ERROR: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("ERROR: La fecha debe tener formato uuuu-MM-dd.");
            } catch (SQLException e) {
                System.out.println("ERROR de base de datos: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        } while (opcion != 0);
    }
}
