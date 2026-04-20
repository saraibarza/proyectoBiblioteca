package excepciones;

public class ExcepcionesBiblioteca extends Exception {
    // No se dan las condiciones necesarias para realizar el prestamo
    public static final String LIBRO_NO_DISPONIBLE = "Este libro no se encuentra disponible, ya ha sido prestado.";
    public static final String SOCIO_NO_DISPONIBLE = "Este socio ya ha realizado un préstamo hoy.";

    // Errores de bases con la fk
    public static final String CODIGO_LIBRO_NO_ENCONTRADO ="El código introducido no pertenece a ningún libro.";
    public static final String CODIGO_SOCIO_NO_ENCONTRADO ="El código introducido no pertenece a ningún socio.";

    // Errores de validar fecha
    public static final String FECHA_NULA ="La fecha no puede ser nula.";
    public static final String FECHA_VACIA ="La fecha no puede estar vacía.";
    public static final String FORMATO_FECHA_INCORRESTO ="El formato de la fecha debe ser dd-mm-yyyy.";

    public ExcepcionesBiblioteca(String mensaje) {
        super(mensaje);
    }
}
