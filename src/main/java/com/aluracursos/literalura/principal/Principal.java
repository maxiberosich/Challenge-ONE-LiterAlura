package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
@Component
public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private ConvierteDatos conversor = new ConvierteDatos();
    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private AutorRepository autorRepository;
    private List<Libro> listaLibrosBuscados;
    private List<Autor> listaAutoresBuscados;

    public  void mostrarMenu(){
        var opcion = -1;
        while(opcion != 0){

            var menu = """
                    
                    Ingrese la opcion que desee:
                    1 - Buscar Libro por Titulo
                    2 - Lista de libros buscados
                    3 - Lista de autores guardados
                    4 - Lista de autores vivos por año
                    5 - Buscar libros guardados por idioma
                    6 - Top 10 libros
                    7 - Cantidad de descargas por libro
                    
                    0-Salir""";

            System.out.println(menu);
            try{
                System.out.print("Seleccione una opción: ");
                opcion = teclado.nextInt();

                switch (opcion){
                    case 1:
                        busquedaLibro();
                        break;
                    case 2:
                        listaLibros();
                        break;
                    case 3:
                        listaAutores();
                        break;
                    case 4:
                        autoresVivos();
                        break;
                    case 5:
                        buscarLibrosPorIdioma();
                        break;
                    case 6:
                        top10Libros();
                        break;
                    case 7:
                        cantidadDescargasPorLibro();
                        break;
                    case 0:
                        System.out.println("Finalizando programa, nos vemos pronto");
                        break;
                    default:
                        System.out.println("Debe ingresar una opcion valida");
                }
            } catch (InputMismatchException e) {
                System.out.println("Debe ingresar un número válido.");
                teclado.next(); // Limpiar la entrada inválida
            }catch (NullPointerException e){
                System.out.println("No existen datos en la BD, ingrese otra opcion" + e.getMessage());
            }
        }
    }

    private DatosBusqueda getBusqueda() {
        System.out.print("Ingrese el titulo del libro:");
        // Limpiar cualquier entrada previa
        teclado.nextLine(); // Para consumir la línea de nueva entrada
        String tituloLibro = teclado.nextLine();
        var resultado = consumoAPI.obtenerDatos(URL_BASE + tituloLibro.replace(" ", "%20"));
        System.out.println(tituloLibro);
        var datos = conversor.obtenerDatos(resultado, DatosBusqueda.class);
        return datos;
    }

    private void busquedaLibro(){
        DatosBusqueda datoBusqueda = getBusqueda();

        if(datoBusqueda != null && !datoBusqueda.resultados().isEmpty()){
            DatosLibros libroEncontrado = datoBusqueda.resultados().get(0);
            Libro libroAGuardar = new Libro(libroEncontrado);
            System.out.println("Libro encontrado: " + libroAGuardar);

            Optional<Libro> libroExistente = libroRepository.findByTitulo(libroAGuardar.getTitulo());
            System.out.println("Verificando existencia del libro: " + libroExistente);

            if (libroExistente.isPresent()){
                System.out.println("El libro " + libroExistente.get().getTitulo() + " ya está registrado");

            }else {
                if (!libroEncontrado.autor().isEmpty()){
                    DatosAutor datosAutor = libroEncontrado.autor().get(0);
                    Autor primerAutor = new Autor(datosAutor);

                    Optional<Autor> autorBusquedaExistencia = autorRepository.findByNombreAutor(primerAutor.getNombreAutor());


                    if (autorBusquedaExistencia.isPresent()) {
                        primerAutor = autorBusquedaExistencia.get();
                    } else {
                        primerAutor = autorRepository.save(primerAutor);
                    }

                    libroAGuardar.setAutor(primerAutor);
                    libroRepository.save(libroAGuardar);
                    System.out.println("Libro " + libroAGuardar.getTitulo() + " guardado exitosamente");

                } else {
                    System.out.println("No se encuentra el autor del libro");
                }
            }
        }else {
            System.out.println("Libro no encontrado, vuelva a intentar");
        }
    }

    private void listaLibros() {
        listaLibrosBuscados = libroRepository.findAll();
        listaLibrosBuscados.stream()
                .forEach(System.out::println);
    }

    private void listaAutores(){
        listaAutoresBuscados = autorRepository.findAll();
        listaAutoresBuscados.stream()
                .forEach(System.out::println);
    }

    private void autoresVivos(){
        System.out.println("Ingresa el año que deseas ver que autores se encontraban vivos:");
        Integer anoIngresado = teclado.nextInt();
        listaAutoresBuscados = autorRepository.listaAutoresVivosPorAno(anoIngresado);
        if(listaAutoresBuscados.isEmpty()){
            System.out.println("No hay autores vivos en el año " + anoIngresado + " dentro de la BD para mostrar");
        }else {
            listaAutoresBuscados.stream()
                    .forEach(System.out::println);
        }

    }

    private void buscarLibrosPorIdioma() {
        System.out.print("""
                Ingrese el idioma del libro
                'en' para inglés
                'es' para español
                'fr' para frances:""");
        teclado.nextLine();
        String idioma = teclado.nextLine();

        listaLibrosBuscados = libroRepository.findAll();
        List<Libro> librosEnIdioma = listaLibrosBuscados.stream()
                .filter(libro -> libro.getLenguaje().contains(idioma))
                .collect(Collectors.toList());

        if (librosEnIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma: " + idioma);
        } else {
            librosEnIdioma.forEach(System.out::println);
        }
    }

    private void top10Libros(){
        List<Libro> topLibros = libroRepository.top10LibrosMasDescargados();
        topLibros.forEach(System.out::println);
    }

    private void cantidadDescargasPorLibro() {
        listaLibrosBuscados = libroRepository.findAll();
        LongSummaryStatistics est = listaLibrosBuscados.stream()
                .filter(l -> l.getDescargas() > 0)
                .collect(Collectors.summarizingLong(Libro::getDescargas));

        Optional<Libro> libroConMasDescargas = listaLibrosBuscados.stream()
                .filter(l -> l.getDescargas() > 0)
                .max(Comparator.comparingLong(Libro::getDescargas));

        Optional<Libro> libroConMenosDescargas = listaLibrosBuscados.stream()
                .filter(l -> l.getDescargas() > 0)
                .min(Comparator.comparingLong(Libro::getDescargas));

        System.out.println("Resumen de descargas:");
        System.out.println("Máximo: " + est.getMax());
        libroConMasDescargas.ifPresent(libro -> System.out.println("Libro con más descargas: " + libro.getTitulo() + " (" + libro.getDescargas() + " descargas)"));
        System.out.println("Mínimo: " + est.getMin());
        libroConMenosDescargas.ifPresent(libro -> System.out.println("Libro con menos descargas: " + libro.getTitulo() + " (" + libro.getDescargas() + " descargas)"));
        System.out.println("Promedio: " + est.getAverage());
        System.out.println("Total: " + est.getSum());
    }

}
