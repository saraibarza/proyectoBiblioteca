package excepciones;

public class ExcepcionesBiblioteca extends Exception {
    public static final String LIBRO_NO_DISPONIBLE = "Este libro no se encuentra disponible, ya ha sido prestado.";
    public static final String CODIGO_LIBRO_NO_ENCONTRADO ="El código introducido no pertenece a ningún libro.";
    public static final String SOCIO_NO_DISPONIBLE = "Este socio ya ha realizado un prestamo hoy.";
    public static final String CODIGO_SOCIO_NO_ENCONTRADO ="El código introducido no pertenece a ningún socio.";

    public ExcepcionesBiblioteca(String mensaje) {
        super(mensaje);
    }
}
