package modelo;

public class Libro {
    private int codigo;
    private String isbn;
    private String titulo;
    private String escritor;
    private int anioPublicacion;
    private double puntuacion;

    public Libro() {
    }

    public Libro(int codigo, String isbn, String titulo, String escritor, int anioPublicacion, double puntuacion) {
        this.codigo = codigo;
        this.isbn = isbn;
        this.titulo = titulo;
        this.escritor = escritor;
        this.anioPublicacion = anioPublicacion;
        this.puntuacion = puntuacion;
    }

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

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public double getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(double puntuacion) {
        this.puntuacion = puntuacion;
    }

    @Override
    public String toString() {
        return "Libro [Codigo = " + codigo +
                ", ISBN = " + isbn +
                ", Titulo = " + titulo +
                ", Escritor = " + escritor +
                ", AnioPublicacion = " + anioPublicacion +
                ", Puntuacion = " + puntuacion + "]";
    }

}
