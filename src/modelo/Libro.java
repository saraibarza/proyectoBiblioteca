package modelo;

public class Libro {
    private int codigo;
    private String isbn;
    private String titulo;
    private String escritor;
    private int año_publicacion;
    private double puntuacion;

    /**
     * Constructor
     */
    public Libro(int codigo, String isbn, String titulo, String escritor, int año_publicacion, double puntuacion) {
        this.codigo = codigo;
        this.isbn = isbn;
        this.titulo = titulo;
        this.escritor = escritor;
        this.año_publicacion = año_publicacion;
        this.puntuacion = puntuacion;
    }


    /**
     * Getters y setters
     */

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEscritor() {
        return escritor;
    }

    public void setEscritor(String escritor) {
        this.escritor = escritor;
    }

    public int getAño_publicacion() {
        return año_publicacion;
    }

    public void setAño_publicacion(int año_publicacion) {
        this.año_publicacion = año_publicacion;
    }

    public double getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(double puntuacion) {
        this.puntuacion = puntuacion;
    }

    /**
     * ToString
     */

    @Override
    public String toString() {
        return "Libro{" +
                "codigo=" + codigo +
                ", isbn='" + isbn + '\'' +
                ", titulo='" + titulo + '\'' +
                ", escritor='" + escritor + '\'' +
                ", año_publicacion=" + año_publicacion +
                ", puntuacion=" + puntuacion +
                '}';
    }
}