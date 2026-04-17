package vista;

import dao.AccesoPrestamo;
import dao.AccesoSocio;
import entrada.Teclado;
import excepciones.BDException;
import excepciones.ExcepcionesBiblioteca;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Socio;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Principal {
    public static int menu() {
        System.out.println("MENU");
        System.out.println("1. Insertar un socio en la base de datos.");
        System.out.println("2. Eliminar un socio, por dni, de la base de datos.");
        System.out.println("3. Consultar todos los socios de la base de datos.");
        System.out.println("4. Consultar varios socios, por localidad, de la base de datos.");
        System.out.println("5. Consultar los socios sin prestamos de la base de datos.");
        System.out.println("6. Consultar los socios con prestamos en una fecha de la base de datos.");
        System.out.println("13. Insertar un prestamo en la base de datos.");
        System.out.println("14. Actualizar la fecha de devolucion de un prestamo en la base de datos.");
        System.out.println("15. Eliminar un prestamo en la base de datos.");
        System.out.println("16. Consultar todos los prestamos de la base de datos.");
        System.out.println("17. Consultar los prestamos no devueltos de la base de datos.");
        System.out.println("18. Consultar DNI y nombre de socio, ISBN y titulo de libro y fecha de devolucion.");
        System.out.println("21. Consultar los libros que han sido prestados una cantidad de veces inferior a la media.");
        System.out.println("22. Consultar los socios que han realizado una cantidad de prestamos superior a la media.");
        System.out.println("23. Consultar el ISBN, el titulo y el numero de veces de los libros prestados.");
        System.out.println("24. Consultar el DNI, el nombre y el numero de veces de los socios con prestamos.");
        System.out.println("0. Finalizar programa");
        return Teclado.leerEntero("Opcion: ");
    }

    public static void main(String[] args) {
        int opcion;

        do {
            opcion = menu();

            try {
                switch (opcion) {
                    case 0:
                        System.out.println("Saliendo del programa...");
                        break;
                    case 1:
                        insertarSocio();
                        break;
                    case 2:
                        eliminarSocio();
                        break;
                    case 3:
                        consultarTodosLosSocios();
                        break;
                    case 4:
                        consultarSociosPorLocalidad();
                        break;
                    case 5:
                        consultarSociosSinPrestamos();
                        break;
                    case 6:
                        consultarSociosConPrestamosEnFecha();
                        break;
                    case 13:
                        insertarPrestamo();
                        break;
                    case 14:
                        actualizarPrestamo();
                        break;
                    case 15:
                        eliminarPrestamo();
                        break;
                    case 16:
                        consultarTodosLosPrestamos();
                        break;
                    case 17:
                        consultarPrestamosNoDevueltos();
                        break;
                    case 18:
                        consultarInformacionPrestamosPorFecha();
                        break;
                    case 21:
                        consultarLibrosPrestadosInferiorMedia();
                        break;
                    case 22:
                        consultarSociosPrestadosSuperiorMedia();
                        break;
                    case 23:
                        consultarVecesLibrosPrestados();
                        break;
                    case 24:
                        consultarVecesSociosPrestados();
                        break;
                    default:
                        System.out.println("La opcion del menu no es valida.");
                        break;
                }
            } catch (ExcepcionesBiblioteca e) {
                System.out.println("ERROR: " + e.getMessage());
            } catch (BDException e) {
                System.out.println("ERROR: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("ERROR: La fecha debe tener formato AAAA-MM-DD.");
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }

            System.out.println();
        } while (opcion != 0);
    }

    private static void insertarSocio() throws BDException {
        String dni = Teclado.leerCadena("DNI: ");
        if (!AccesoSocio.validarDni(dni)) {
            System.out.println("El DNI no es valido.");
            return;
        }

        String nombre = Teclado.leerCadena("Nombre: ");
        String domicilio = Teclado.leerCadena("Domicilio: ");
        String telefono = Teclado.leerCadena("Telefono: ");
        String correo = Teclado.leerCadena("Correo: ");

        Socio socio = new Socio(0, dni, nombre, domicilio, telefono, correo);
        AccesoSocio.insertarSocio(socio);
        System.out.println("Se ha insertado un socio en la base de datos.");
    }

    private static void eliminarSocio() throws BDException {
        String dni = Teclado.leerCadena("DNI del socio: ");
        if (!AccesoSocio.validarDni(dni)) {
            System.out.println("El DNI no es valido.");
            return;
        }

        int resultado = AccesoSocio.borrarSocio(dni);
        if (resultado > 0) {
            System.out.println("Se ha eliminado un socio de la base de datos.");
        } else if (resultado == 0) {
            System.out.println("No existe ningun socio con ese dni en la base de datos.");
        } else {
            System.out.println("El socio esta referenciado en un prestamo de la base de datos.");
        }
    }

    private static void consultarTodosLosSocios() throws BDException {
        mostrarSocios(
                AccesoSocio.consultarSocios(),
                "No se ha encontrado ningun socio en la base de datos."
        );
    }

    private static void consultarSociosPorLocalidad() throws BDException {
        String localidad = Teclado.leerCadena("Localidad: ");
        mostrarSocios(
                AccesoSocio.consultarSociosPorLocalidad(localidad),
                "No existe ningun socio con esa localidad en la base de datos."
        );
    }

    private static void consultarSociosSinPrestamos() throws BDException {
        mostrarSocios(
                AccesoSocio.consultarSociosSinPrestamos(),
                "No existe ningun socio sin prestamos en la base de datos."
        );
    }

    private static void consultarSociosConPrestamosEnFecha() throws BDException {
        String fechaTexto = Teclado.leerCadena("Fecha de inicio (AAAA-MM-DD): ");
        Date fecha = Date.valueOf(fechaTexto);
        mostrarSocios(
                AccesoSocio.consultarSociosConPrestamosFecha(fecha),
                "No existe ningun socio con prestamos en esa fecha en la base de datos."
        );
    }

    private static void insertarPrestamo() throws BDException, ExcepcionesBiblioteca {
        int codigoLibro = Teclado.leerEntero("Codigo del libro: ");
        int codigoSocio = Teclado.leerEntero("Codigo del socio: ");
        String fechaInicio = Teclado.leerCadena("Fecha de inicio: ");
        String fechaFin = Teclado.leerCadena("Fecha de fin: ");

        Prestamo prestamo = new Prestamo(codigoLibro, codigoSocio, fechaInicio, fechaFin);
        if (AccesoPrestamo.insertarPrestamo(prestamo)) {
            System.out.println("Se ha insertado un prestamo en la base de datos.");
        } else {
            System.out.println("No se ha podido insertar el prestamo.");
        }
    }

    private static void actualizarPrestamo() throws BDException {
        int codigoLibro = Teclado.leerEntero("Codigo del libro: ");
        int codigoSocio = Teclado.leerEntero("Codigo del socio: ");
        String fechaInicio = Teclado.leerCadena("Fecha de inicio: ");
        String fechaDevolucion = Teclado.leerCadena("Fecha de devolucion: ");

        if (AccesoPrestamo.actualizarDevolucionDelPrestamo(codigoLibro, codigoSocio, fechaInicio, fechaDevolucion)) {
            System.out.println("Se ha actualizado un prestamo de la base de datos.");
        } else {
            System.out.println("No existe ningun prestamo con esos datos identificativos en la base de datos.");
        }
    }

    private static void eliminarPrestamo() throws BDException {
        int codigoLibro = Teclado.leerEntero("Codigo del libro: ");
        int codigoSocio = Teclado.leerEntero("Codigo del socio: ");
        String fechaInicio = Teclado.leerCadena("Fecha de inicio: ");

        if (AccesoPrestamo.eliminarPrestamo(codigoLibro, codigoSocio, fechaInicio)) {
            System.out.println("Se ha eliminado un prestamo de la base de datos.");
        } else {
            System.out.println("No existe ningun prestamo con esos datos identificativos en la base de datos.");
        }
    }

    private static void consultarTodosLosPrestamos() throws BDException {
        mostrarPrestamos(
                AccesoPrestamo.consultarTodosLosPrestamos(),
                "No se ha encontrado ningun prestamo en la base de datos."
        );
    }

    private static void consultarPrestamosNoDevueltos() throws BDException {
        mostrarPrestamos(
                AccesoPrestamo.consultarTodosLosPrestamosNoDevueltos(),
                "No existe ningun prestamo no devuelto en la base de datos."
        );
    }

    private static void consultarInformacionPrestamosPorFecha() throws BDException {
        String fechaInicio = Teclado.leerCadena("Fecha de inicio: ");
        List<String[]> prestamos = AccesoPrestamo.consultarInfrmacionDePrestamos(fechaInicio);

        if (prestamos.isEmpty()) {
            System.out.println("No existe ningun prestamo realizado en esa fecha en la base de datos.");
            return;
        }

        for (String[] prestamo : prestamos) {
            System.out.println("DNI: " + prestamo[0]);
            System.out.println("Nombre: " + prestamo[1]);
            System.out.println("ISBN: " + prestamo[2]);
            System.out.println("Titulo: " + prestamo[3]);
            System.out.println("Fecha de devolucion: " + prestamo[4]);
        }
        System.out.println("Se han consultado " + prestamos.size() + " lineas de resumen de la base de datos.");
    }

    private static void consultarLibrosPrestadosInferiorMedia() throws BDException {
        List<Libro> libros = AccesoPrestamo.librosPrestadosInferiorMedia();
        if (libros.isEmpty()) {
            System.out.println("Ningun libro ha sido prestado.");
            return;
        }

        for (Libro libro : libros) {
            System.out.println(libro);
        }
    }

    private static void consultarSociosPrestadosSuperiorMedia() throws BDException {
        List<Socio> socios = AccesoPrestamo.sociosPrestadosSuperiorMedia();
        if (socios.isEmpty()) {
            System.out.println("Ningun socio ha hecho un prestamo.");
            return;
        }

        for (Socio socio : socios) {
            System.out.println(socio);
        }
    }

    private static void consultarVecesLibrosPrestados() throws BDException {
        List<String[]> libros = AccesoPrestamo.vecesLibrosPrestados();
        if (libros.isEmpty()) {
            System.out.println("Ningun libro ha sido prestado.");
            return;
        }

        for (String[] libro : libros) {
            System.out.println("ISBN: " + libro[0]);
            System.out.println("Titulo: " + libro[1]);
            System.out.println("Total de veces que ha sido el libro prestado: " + libro[2]);
        }
    }

    private static void consultarVecesSociosPrestados() throws BDException {
        List<String[]> socios = AccesoPrestamo.vecesSociosPrestados();
        if (socios.isEmpty()) {
            System.out.println("Ningun socio ha hecho un prestamo.");
            return;
        }

        for (String[] socio : socios) {
            System.out.println("DNI: " + socio[0]);
            System.out.println("Nombre: " + socio[1]);
            System.out.println("Total de veces que ha realizado un prestamo: " + socio[2]);
        }
    }

    private static void mostrarSocios(ArrayList<Socio> socios, String mensajeVacio) {
        if (socios.isEmpty()) {
            System.out.println(mensajeVacio);
            return;
        }

        for (Socio socio : socios) {
            System.out.println(socio);
        }
        System.out.println("Se han consultado " + socios.size() + " socios de la base de datos.");
    }

    private static void mostrarPrestamos(List<Prestamo> prestamos, String mensajeVacio) {
        if (prestamos.isEmpty()) {
            System.out.println(mensajeVacio);
            return;
        }

        for (Prestamo prestamo : prestamos) {
            System.out.println(prestamo);
        }
        System.out.println("Se han consultado " + prestamos.size() + " prestamos de la base de datos.");
    }
}
