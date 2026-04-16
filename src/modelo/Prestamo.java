package modelo;

public class Prestamo {
    private int codigo_libro;
    private int codigo_socio;
    private String fecha_inicio;
    // las 3 claves
    private String fecha_fin;
    private String fecha_devolucion;

    /**
     * Constructor completo.
     *
     * @param codigo_libro
     * @param codigo_socio
     * @param fecha_inicio
     * @param fecha_fin
     * @param fecha_devolucion
     */
    public Prestamo(int codigo_libro, int codigo_socio, String fecha_inicio, String fecha_fin, String fecha_devolucion) {
        this.codigo_libro = codigo_libro;
        this.codigo_socio = codigo_socio;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.fecha_devolucion = fecha_devolucion;
    }

    /**
     * Constructor con fecha devolucion nula
     *
     * @param codigo_libro
     * @param codigo_socio
     * @param fecha_inicio
     * @param fecha_fin
     */
    public Prestamo(int codigo_libro, int codigo_socio, String fecha_inicio, String fecha_fin) {
        this.codigo_libro = codigo_libro;
        this.codigo_socio = codigo_socio;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.fecha_devolucion = null;
    }

    // getters y setters
    public int getCodigo_libro() {
        return codigo_libro;
    }

    public void setCodigo_libro(int codigo_libro) {
        this.codigo_libro = codigo_libro;
    }

    public int getCodigo_socio() {
        return codigo_socio;
    }

    public void setCodigo_socio(int codigo_socio) {
        this.codigo_socio = codigo_socio;
    }

    public String getFecha_devolucion() {
        return fecha_devolucion;
    }

    public void setFecha_devolucion(String fecha_devolucion) {
        this.fecha_devolucion = fecha_devolucion;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    @Override
    public String toString() {
        String resultado = "-Prestamo\n";
        resultado += "Código del libro: " + codigo_libro;
        resultado += "\nCódigo del socio: " + codigo_socio;
        resultado += "\nFecha del inicio del prestamo: " + fecha_inicio;
        resultado += "\nFecha del fin del prestamo: " + fecha_fin;
        if(fecha_devolucion != null) {
            resultado += "\nSe ha devuelto el: " + fecha_devolucion;
        } else{
            resultado += "\nEl libro aún no ha sido devuelto";
        }
        resultado += "\n-------------------------------------------------";
        return resultado;
    }
}
