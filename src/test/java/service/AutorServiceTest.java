package service;

import model.Autor;
import org.junit.Before;
import org.junit.Test;
import repository.AutorRepositoryImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AutorServiceTest {

    private AutorService autorService;
    private AutorRepositoryImpl autorRepository;

    @Before
    public void setUp() {
        autorRepository = mock(AutorRepositoryImpl.class);
        autorService = new AutorService(autorRepository);
    }

    @Test
    public void testCriarAutorValido() throws Exception {
        Autor autor = new Autor();
        autor.setNome("Jorge Amado");
        autor.setBio("Escritor brasileiro");
        autor.setDataNascimento(LocalDate.of(1912, 8, 10));

        Autor salvo = new Autor();
        salvo.setId(1);
        salvo.setNome("Jorge Amado");
        salvo.setBio("Escritor brasileiro");

        when(autorRepository.criar(any())).thenReturn(salvo);

        Autor criado = autorService.criar(autor);

        assertNotNull(criado.getId());
        assertEquals("Jorge Amado", criado.getNome());

        verify(autorRepository).criar(any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCriarAutorSemNome() throws Exception {
        Autor autor = new Autor();
        autorService.criar(autor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCriarAutorComNomeVazio() throws Exception {
        Autor autor = new Autor();
        autor.setNome("   ");
        autorService.criar(autor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCriarAutorComNomeMuitoLongo() throws Exception {
        Autor autor = new Autor();
        autor.setNome("a".repeat(151));
        autorService.criar(autor);
    }

    @Test
    public void testBuscarAutorPorIdExistente() throws Exception {
        Autor autor = new Autor();
        autor.setId(10);
        autor.setNome("Clarice Lispector");

        when(autorRepository.buscarPorId(10)).thenReturn(autor);

        Autor encontrado = autorService.buscarPorId(10);

        assertEquals("Clarice Lispector", encontrado.getNome());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuscarAutorPorIdInexistente() throws Exception {
        when(autorRepository.buscarPorId(999)).thenReturn(null);
        autorService.buscarPorId(999);
    }

    @Test
    public void testAtualizarAutorExistente() throws Exception {

        when(autorRepository.existe(1)).thenReturn(true);
        when(autorRepository.atualizar(eq(1), any())).thenReturn(true);

        Autor atualizado = new Autor();
        atualizado.setNome("Atualizado");

        autorService.atualizar(1, atualizado);

        verify(autorRepository).existe(1);
        verify(autorRepository).atualizar(eq(1), any());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testAtualizarAutorInexistente() throws Exception {
        when(autorRepository.buscarPorId(99)).thenReturn(null);

        Autor autor = new Autor();
        autor.setNome("Teste");

        autorService.atualizar(99, autor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAtualizarAutorNaoExiste() throws Exception {
        when(autorRepository.existe(1)).thenReturn(false);

        Autor autor = new Autor();
        autor.setNome("Teste");

        autorService.atualizar(1, autor);
    }

    @Test
    public void testDeletarAutorExistente() throws Exception {

        when(autorRepository.existe(5)).thenReturn(true);
        when(autorRepository.deletar(5)).thenReturn(true);

        autorService.deletar(5);

        verify(autorRepository).existe(5);
        verify(autorRepository).deletar(5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeletarAutorInexistente() throws Exception {
        when(autorRepository.buscarPorId(99)).thenReturn(null);
        autorService.deletar(99);
    }

    @Test
    public void testListarAutoresComPaginacao() throws Exception {
        List<Autor> lista = new ArrayList<>();

        Autor a1 = new Autor();
        a1.setNome("Autor 1");

        lista.add(a1);

        when(autorRepository.listarTodos(anyInt(), anyInt(), any()))
                .thenReturn(lista);

        when(autorRepository.contarAutores(any()))
                .thenReturn(1);

        Map<String, Object> resultado = autorService.listar(1, 10, null);

        assertNotNull(resultado);
        assertTrue(resultado.containsKey("data"));
        assertTrue(resultado.containsKey("total"));
    }

    @Test
    public void testFiltrarAutoresPorNome() throws Exception {

        Autor autor = new Autor();
        autor.setNome("Machado de Assis");

        List<Autor> lista = Collections.singletonList(autor);

        when(autorRepository.listarTodos(1, 10, "Machado"))
                .thenReturn(lista);

        when(autorRepository.contarAutores("Machado"))
                .thenReturn(1);

        Map<String, Object> resultado = autorService.listar(1, 10, "Machado");

        List<Autor> autores = (List<Autor>) resultado.get("data");

        assertEquals(1, autores.size());
        assertTrue(autores.get(0).getNome().contains("Machado"));
    }

}
