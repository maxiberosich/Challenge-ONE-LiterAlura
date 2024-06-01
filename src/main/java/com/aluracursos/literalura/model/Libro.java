package com.aluracursos.literalura.model;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @ManyToOne
    //@JoinColumn(name = "autor_id")
    private Autor autor;
    private List<String> lenguaje;
    private Long descargas;

    public Libro() {
    }

    public Libro(DatosLibros datosLibros) {
        this.id = datosLibros.id();
        this.titulo = datosLibros.titulo();
        this.lenguaje = datosLibros.lenguaje();
        this.descargas = datosLibros.descargas();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public List<String> getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(List<String> lenguaje) {
        this.lenguaje = lenguaje;
    }

    public Long getDescargas() {
        return descargas;
    }

    public void setDescargas(Long descargas) {
        this.descargas = descargas;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor=" + autor +
                ", lenguaje=" + lenguaje +
                ", descargas=" + descargas +
                '}';
    }
}
