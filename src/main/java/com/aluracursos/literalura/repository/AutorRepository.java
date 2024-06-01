package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombreAutor(String nombreAutor);

    @Query("SELECT a FROM Autor a WHERE a.fechaNacimiento <= :anio AND a.fechaMuerte >= :anio")
    List<Autor> listaAutoresVivosPorAno(Integer anio);
}
