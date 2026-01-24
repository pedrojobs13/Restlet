package service;

import model.Autor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.AutorRepositoryImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class AutorServiceTest {

    private AutorService autorService;
    private AutorRepositoryImpl autorRepository;
    private Integer autorIdTeste;

    @Before
    public void setUp() {
        autorRepository = new AutorRepositoryImpl();
        autorService = new AutorService(autorRepository);
    }

    @After
    public void tearDown() throws Exception {
        if (autorIdTeste != null) {
            try {
                autorRepository.deletar(autorIdTeste);
            } catch (Exception e) {
            }
        }
    }

    @Test
    public void testCriarAutorValido() throws Exception {
        Autor autor = new Autor();
        autor.setNome("Jorge Amado");
        autor.setBio("Escritor brasileiro");
        autor.setDataNascimento(LocalDate.of(1912, 8, 10));

        Autor criado = autorService.criar(autor);
        autorIdTeste = criado.getId();

        assertNotNull("ID do autor não deve ser null", criado.getId());
        assertEquals("Nome deve ser igual", "Jorge Amado", criado.getNome());
        assertEquals("Bio deve ser igual", "Escritor brasileiro", criado.getBio());
        assertTrue("ID deve ser maior que 0", criado.getId() > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCriarAutorSemNome() throws Exception {
        Autor autor = new Autor();
        autor.setBio("Biografia sem nome");

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
        String nomeLongo = "a".repeat(151);
        Autor autor = new Autor();
        autor.setNome(nomeLongo);

        autorService.criar(autor);
    }

    @Test
    public void testBuscarAutorPorIdExistente() throws Exception {
        Autor autor = new Autor();
        autor.setNome("Clarice Lispector");
        autor.setBio("Escritora brasileira");
        Autor criado = autorService.criar(autor);
        autorIdTeste = criado.getId();

        Autor encontrado = autorService.buscarPorId(autorIdTeste);

        assertNotNull("Autor deve ser encontrado", encontrado);
        assertEquals("IDs devem ser iguais", autorIdTeste, encontrado.getId());
        assertEquals("Nomes devem ser iguais", "Clarice Lispector", encontrado.getNome());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuscarAutorPorIdInexistente() throws Exception {
        autorService.buscarPorId(999999);
    }

    @Test
    public void testAtualizarAutorExistente() throws Exception {
        Autor autor = new Autor();
        autor.setNome("Nome Original");
        autor.setBio("Bio Original");
        Autor criado = autorService.criar(autor);
        autorIdTeste = criado.getId();

        Autor atualizado = new Autor();
        atualizado.setNome("Nome Atualizado");
        atualizado.setBio("Bio Atualizada");
        atualizado.setDataNascimento(LocalDate.of(1900, 1, 1));

        autorService.atualizar(autorIdTeste, atualizado);

        Autor verificado = autorService.buscarPorId(autorIdTeste);
        assertEquals("Nome deve estar atualizado", "Nome Atualizado", verificado.getNome());
        assertEquals("Bio deve estar atualizada", "Bio Atualizada", verificado.getBio());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAtualizarAutorInexistente() throws Exception {
        Autor autor = new Autor();
        autor.setNome("Nome Qualquer");

        autorService.atualizar(999999, autor);
    }

    @Test
    public void testDeletarAutorExistente() throws Exception {
        Autor autor = new Autor();
        autor.setNome("Autor Para Deletar");
        Autor criado = autorService.criar(autor);
        Integer idParaDeletar = criado.getId();

        autorService.deletar(idParaDeletar);

        try {
            autorService.buscarPorId(idParaDeletar);
            fail("Deveria ter lançado exceção ao buscar autor deletado");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("não encontrado"));
        }

        autorIdTeste = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeletarAutorInexistente() throws Exception {
        autorService.deletar(999999);
    }

    @Test
    public void testListarAutoresComPaginacao() throws Exception {
        for (int i = 1; i <= 3; i++) {
            Autor autor = new Autor();
            autor.setNome("Autor Teste " + i);
            autorService.criar(autor);
        }

        Map<String, Object> resultado = autorService.listar(1, 2, null);

        assertNotNull("Resultado não deve ser null", resultado);
        assertTrue("Deve conter 'data'", resultado.containsKey("data"));
        assertTrue("Deve conter 'total'", resultado.containsKey("total"));

        @SuppressWarnings("unchecked")
        List<Autor> autores = (List<Autor>) resultado.get("data");

        assertNotNull("Lista de autores não deve ser null", autores);
        assertTrue("Deve retornar pelo menos 1 autor", autores.size() >= 1);
    }

    @Test
    public void testFiltrarAutoresPorNome() throws Exception {
        Autor autor = new Autor();
        autor.setNome("Machado de Assis Teste");
        Autor criado = autorService.criar(autor);
        autorIdTeste = criado.getId();

        Map<String, Object> resultado = autorService.listar(1, 10, "Machado");

        @SuppressWarnings("unchecked")
        List<Autor> autores = (List<Autor>) resultado.get("data");

        assertNotNull("Lista não deve ser null", autores);
        assertTrue("Deve encontrar pelo menos 1 autor", autores.size() >= 1);

        boolean encontrou = false;
        for (Autor a : autores) {
            if (a.getNome().contains("Machado")) {
                encontrou = true;
                break;
            }
        }
        assertTrue("Deve encontrar autor com 'Machado' no nome", encontrou);
    }
}
