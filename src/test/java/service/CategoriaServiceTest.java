package service;

import model.Categoria;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import repository.CategoriaRepository;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CategoriaServiceTest {

    private CategoriaRepository categoriaRepository;
    private CategoriaService categoriaService;

    @Before
    public void setUp() {
        categoriaRepository = Mockito.mock(CategoriaRepository.class);
        categoriaService = new CategoriaService(categoriaRepository);
    }

    @Test
    public void testBuscarPorIdSucesso() throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(1);
        categoria.setNome("Tecnologia");

        when(categoriaRepository.buscarPorId(1)).thenReturn(categoria);
        when(categoriaRepository.contarLivrosPorCategoria(1)).thenReturn(5);

        Map<String, Object> result = categoriaService.buscarPorId(1);

        assertEquals(1, result.get("id"));
        assertEquals("Tecnologia", result.get("nome"));
        assertEquals(5, result.get("totalLivros"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuscarPorIdInexistente() throws SQLException {
        when(categoriaRepository.buscarPorId(1)).thenReturn(null);
        categoriaService.buscarPorId(1);
    }

    @Test
    public void testListarTodas() throws SQLException {
        List<Categoria> categorias = Arrays.asList(
                new Categoria(1, "A"),
                new Categoria(2, "B")
        );

        when(categoriaRepository.listarTodas()).thenReturn(categorias);

        Map<String, Object> result = categoriaService.listarTodas();

        assertEquals(2, result.get("total"));
        assertEquals(categorias, result.get("data"));
    }

    @Test
    public void testCriarCategoriaSucesso() throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setNome("Educação");

        when(categoriaRepository.nomeExiste("Educação")).thenReturn(false);
        when(categoriaRepository.criar(categoria)).thenReturn(categoria);

        Categoria criada = categoriaService.criar(categoria);

        assertEquals("Educação", criada.getNome());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCriarCategoriaSemNome() throws SQLException {
        Categoria categoria = new Categoria();
        categoriaService.criar(categoria);
    }

    @Test(expected = IllegalStateException.class)
    public void testCriarCategoriaNomeDuplicado() throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setNome("Saúde");

        when(categoriaRepository.nomeExiste("Saúde")).thenReturn(true);

        categoriaService.criar(categoria);
    }

    @Test
    public void testAtualizarSucesso() throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setNome("Atualizada");

        when(categoriaRepository.existe(1)).thenReturn(true);
        when(categoriaRepository.buscarPorNome("Atualizada")).thenReturn(null);
        when(categoriaRepository.atualizar(1, categoria)).thenReturn(true);

        categoriaService.atualizar(1, categoria);

        verify(categoriaRepository).atualizar(1, categoria);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAtualizarCategoriaInexistente() throws SQLException {
        when(categoriaRepository.existe(1)).thenReturn(false);
        categoriaService.atualizar(1, new Categoria());
    }

    @Test(expected = IllegalStateException.class)
    public void testAtualizarNomeDuplicado() throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setNome("Duplicado");

        Categoria existente = new Categoria();
        existente.setId(2);
        existente.setNome("Duplicado");

        when(categoriaRepository.existe(1)).thenReturn(true);
        when(categoriaRepository.buscarPorNome("Duplicado")).thenReturn(existente);

        categoriaService.atualizar(1, categoria);
    }

    @Test
    public void testDeletarSucesso() throws SQLException {
        when(categoriaRepository.existe(1)).thenReturn(true);
        when(categoriaRepository.contarLivrosPorCategoria(1)).thenReturn(0);
        when(categoriaRepository.deletar(1)).thenReturn(true);

        categoriaService.deletar(1);

        verify(categoriaRepository).deletar(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeletarCategoriaInexistente() throws SQLException {
        when(categoriaRepository.existe(1)).thenReturn(false);
        categoriaService.deletar(1);
    }

    @Test(expected = IllegalStateException.class)
    public void testDeletarCategoriaComLivros() throws SQLException {
        when(categoriaRepository.existe(1)).thenReturn(true);
        when(categoriaRepository.contarLivrosPorCategoria(1)).thenReturn(3);

        categoriaService.deletar(1);
    }

    @Test(expected = IllegalStateException.class)
    public void testDeletarFalhaRepositorio() throws SQLException {
        when(categoriaRepository.existe(1)).thenReturn(true);
        when(categoriaRepository.contarLivrosPorCategoria(1)).thenReturn(0);
        when(categoriaRepository.deletar(1)).thenReturn(false);

        categoriaService.deletar(1);
    }
}
