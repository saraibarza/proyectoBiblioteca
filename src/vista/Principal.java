package vista;

import dao.AccesoPrestamo;
import entrada.Teclado;
import excepciones.BDException;
import excepciones.ExcepcionesBiblioteca;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Socio;

import java.util.List;

public class Principal {
    /**
     * Menú.
     *
     * @return
     */
    public static int menu() {
        System.out.println("MENU");
        System.out.println("13. Insertar un préstamo en la base de datos.");
        System.out.println("14. Actualizar la fecha de devolución de un prestamo en la base de datos.");
        System.out.println("15. Eliminar un prestamo en la base de datos.");
        System.out.println("16. Consultar todos los préstamos de la base de datos.");
        System.out.println("17. Consultar los préstamos no devueltos de la base de datos.");
        System.out.println("18. Consultar DNI, nombre de socio, ISBN, título de libro y la fecha de devolución de los\n" +
                "préstamos realizados en una fecha de la base de datos.");
        System.out.println("21. Consultar los libros que han sido prestados una cantidad de veces inferior a la media.");
        System.out.println("22. Consultar los socios que han realizado una cantidad de préstamos superior a la media.");
        System.out.println("23. Consultar el ISBN, el título y el número de veces de los libros que han sido prestados,\n" +
                "ordenados por el número de préstamos descendente.");
        System.out.println("24. Consultar el DNI, el nombre y el número de veces de los socios que han realizado préstamos,\n" +
                "ordenados por el número de préstamos descendente.");
        System.out.println("0. Finalizar Programa");
        return Teclado.leerEntero("Opción: ");
    }

    /**
     *
     * @param args
     * @throws BDException
     * @throws ExcepcionesBiblioteca
     */
    public static void main(String[] args) throws BDException, ExcepcionesBiblioteca {
        int opcion = 1;
        try {
            do {
                opcion = menu();

                switch (opcion) {
                    // Insertar un préstamo en la base de datos.
                    case 13: {
                        int codigo_libro = Teclado.leerEntero("Código del libro: ");
                        int codigo_socio = Teclado.leerEntero("Código del socio: ");
                        String fecha_inicio = Teclado.leerCadena("Fecha de inicio: ");
                        String fecha_final = Teclado.leerCadena("Fecha final: ");
                        Prestamo prestamo = new Prestamo(codigo_libro, codigo_socio, fecha_inicio, fecha_final);

                        if (AccesoPrestamo.insertarPrestamo(prestamo)) {
                            System.out.println("Se ha insertado el prestamo correctamente.");
                        } else {
                            System.out.println("No se ha podido insertar el prestamo.");
                        }
                        break;
                    }

                    // Actualizar un préstamo, por datos identificativos, de la base de datos.
                    case 14: {
                        int codigo_libro = Teclado.leerEntero("Código del libro: ");
                        int codigo_socio = Teclado.leerEntero("Código del socio: ");
                        String fecha_inicio = Teclado.leerCadena("Fecha de inicio: ");
                        String fecha_devolucion = Teclado.leerCadena("Fecha de devolucion: ");

                        if (AccesoPrestamo.actualizarDevolucionDelPrestamo(codigo_libro, codigo_socio, fecha_inicio, fecha_devolucion)) {
                            System.out.println("Se ha modificado el prestamo correctamente.");
                        } else {
                            System.out.println("No se ha podido modificar el prestamo.");
                        }
                        break;
                    }

                    // Eliminar un préstamo, por datos identificativos, de la base de datos.
                    case 15: {
                        int codigo_libro = Teclado.leerEntero("Código del libro: ");
                        int codigo_socio = Teclado.leerEntero("Codigo del socio: ");
                        String fecha_inicio = Teclado.leerCadena("Fecha de inicio: ");

                        if (AccesoPrestamo.eliminarPrestamo(codigo_libro, codigo_socio, fecha_inicio)) {
                            System.out.println("Se ha eliminado el prestamo correctamente.");
                        } else {
                            System.out.println("No se ha podido eliminar el prestamo.");
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
                        String fecha_inicial = Teclado.leerCadena("Fecha de inicial: ");

                        List<String[]> prestamos = AccesoPrestamo.consultarInfrmacionDePrestamos(fecha_inicial);

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
                        System.out.println("La opción del ménu no es valida.");
                        break;
                    }
                }
            } while (opcion != 0);
        } catch (ExcepcionesBiblioteca e) {
            System.out.println("ERROR: " + e.getMessage());

        } catch (BDException e) {
            System.err.println("ERROR: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
}
