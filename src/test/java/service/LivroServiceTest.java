package service;

import model.Autor;
import model.Livro;
import model.Usuario;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.AutorRepositoryImpl;
import repository.LivrosRepositoryImpl;
import repository.UsuarioRepository;
import repository.UsuarioRepositoryImpl;
import utils.PasswordUtil;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class LivroServiceTest {

    private LivroService livroService;
    private LivrosRepositoryImpl livroRepository;
    private AutorRepositoryImpl autorRepository;
    private UsuarioRepository usuarioRepository;

    private Integer livroIdTeste;
    private Integer autorIdTeste;
    private Integer usuarioIdTeste;
    private String emailUsuarioTeste;

    @Before
    public void setUp() throws Exception {
        livroRepository = new LivrosRepositoryImpl();
        autorRepository = new AutorRepositoryImpl();
        usuarioRepository = new UsuarioRepositoryImpl();
        livroService = new LivroService(livroRepository, usuarioRepository);

        Usuario usuario = new Usuario();
        usuario.setNome("Usuario Teste");
        usuario.setEmail("teste.livros@email.com");
        usuario.setSenha(PasswordUtil.hashPassword("senha123"));
        usuario.setFuncao("user");

        Usuario usuarioCriado = usuarioRepository.criar(usuario);
        usuarioIdTeste = usuarioCriado.getId();
        emailUsuarioTeste = usuarioCriado.getEmail();

        Autor autor = new Autor();
        autor.setNome("Autor de Teste");
        Autor autorCriado = autorRepository.criar(autor);
        autorIdTeste = autorCriado.getId();
    }

    @After
    public void tearDown() throws Exception {
        if (livroIdTeste != null) {
            try {
                livroRepository.deletar(livroIdTeste);
            } catch (Exception e) {
            }
        }

        if (autorIdTeste != null) {
            try {
                autorRepository.deletar(autorIdTeste);
            } catch (Exception e) {
            }
        }

        if (usuarioIdTeste != null) {
            try {
                usuarioRepository.deletar(usuarioIdTeste);
            } catch (Exception e) {
            }
        }
    }

    @Test
    public void testCriarLivroValido() throws Exception {
        Livro livro = new Livro();
        livro.setTitulo("Livro de Teste");
        livro.setDescricao("Descrição do livro");
        livro.setIsbn("9781234567890");
        livro.setAnoDePublicacao(2024);
        livro.setAutorId(autorIdTeste);

        Livro criado = livroService.criar(livro, emailUsuarioTeste);
        livroIdTeste = criado.getId();

        assertNotNull("ID não deve ser null", criado.getId());
        assertEquals("Título deve ser igual", "Livro de Teste", criado.getTitulo());
        assertEquals("ISBN deve ser igual", "9781234567890", criado.getIsbn());
        assertEquals("Autor ID deve ser igual", autorIdTeste, criado.getAutorId());
        assertTrue("ID deve ser maior que 0", criado.getId() > 0);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCriarLivroSemTitulo() throws Exception {
        Livro livro = new Livro();
        livro.setIsbn("9781234567890");
        livro.setAutorId(autorIdTeste);

        livroService.criar(livro, emailUsuarioTeste);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCriarLivroSemIsbn() throws Exception {
        Livro livro = new Livro();
        livro.setTitulo("Livro sem ISBN");
        livro.setAutorId(autorIdTeste);

        livroService.criar(livro, emailUsuarioTeste);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCriarLivroSemAutor() throws Exception {
        Livro livro = new Livro();
        livro.setTitulo("Livro sem Autor");
        livro.setIsbn("9781234567890");

        livroService.criar(livro, emailUsuarioTeste);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCriarLivroComUsuarioInexistente() throws Exception {
        Livro livro = new Livro();
        livro.setTitulo("Livro Teste");
        livro.setIsbn("9781234567890");
        livro.setAutorId(autorIdTeste);

        livroService.criar(livro, "naoexiste@email.com");
    }

    @Test
    public void testBuscarLivroPorIdExistente() throws Exception {
        Livro livro = new Livro();
        livro.setTitulo("Livro Para Buscar");
        livro.setIsbn("9780987654321");
        livro.setAutorId(autorIdTeste);
        Livro criado = livroService.criar(livro, emailUsuarioTeste);
        livroIdTeste = criado.getId();

        Livro encontrado = livroService.buscarPorId(livroIdTeste);

        assertNotNull("Livro deve ser encontrado", encontrado);
        assertEquals("IDs devem ser iguais", livroIdTeste, encontrado.getId());
        assertEquals("Títulos devem ser iguais", "Livro Para Buscar", encontrado.getTitulo());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testBuscarLivroPorIdInexistente() throws Exception {
        livroService.buscarPorId(999999);
    }


    @Test
    public void testAtualizarLivroExistente() throws Exception {
        Livro livro = new Livro();
        livro.setTitulo("Título Original");
        livro.setIsbn("9781111111111");
        livro.setAutorId(autorIdTeste);
        Livro criado = livroService.criar(livro, emailUsuarioTeste);
        livroIdTeste = criado.getId();

        Livro atualizado = new Livro();
        atualizado.setTitulo("Título Atualizado");
        atualizado.setDescricao("Nova descrição");
        atualizado.setIsbn("9781111111111");
        atualizado.setAutorId(autorIdTeste);

        livroService.atualizar(livroIdTeste, atualizado);

        Livro verificado = livroService.buscarPorId(livroIdTeste);
        assertEquals("Título deve estar atualizado", "Título Atualizado", verificado.getTitulo());
        assertEquals("Descrição deve estar atualizada", "Nova descrição", verificado.getDescricao());
    }

    @Test
    public void testDeletarLivroExistente() throws Exception {
        Livro livro = new Livro();
        livro.setTitulo("Livro Para Deletar");
        livro.setIsbn("9782222222222");
        livro.setAutorId(autorIdTeste);
        Livro criado = livroService.criar(livro, emailUsuarioTeste);
        Integer idParaDeletar = criado.getId();

        livroService.deletar(idParaDeletar);

        try {
            livroService.buscarPorId(idParaDeletar);
            fail("Deveria ter lançado exceção ao buscar livro deletado");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("não encontrado"));
        }

        livroIdTeste = null;
    }

    @Test
    public void testListarLivrosComPaginacao() throws Exception {
        for (int i = 1; i <= 3; i++) {
            Livro livro = new Livro();
            livro.setTitulo("Livro Paginação " + i);
            livro.setIsbn("978333333333" + i);
            livro.setAutorId(autorIdTeste);
            livroService.criar(livro, emailUsuarioTeste);
        }

        Map<String, Object> resultado = livroService.listar(1, 2, null);

        assertNotNull("Resultado não deve ser null", resultado);
        assertTrue("Deve conter 'data'", resultado.containsKey("data"));

        @SuppressWarnings("unchecked")
        List<Livro> livros = (List<Livro>) resultado.get("data");
        assertNotNull("Lista não deve ser null", livros);
        assertTrue("Deve retornar pelo menos 1 livro", livros.size() >= 1);
    }

    @Test
    public void testFiltrarLivrosPorTitulo() throws Exception {
        // Arrange
        Livro livro = new Livro();
        livro.setTitulo("Dom Casmurro Teste");
        livro.setIsbn("9784444444444");
        livro.setAutorId(autorIdTeste);
        Livro criado = livroService.criar(livro, emailUsuarioTeste);
        livroIdTeste = criado.getId();

        Map<String, Object> resultado = livroService.listar(1, 10, "Casmurro");
        @SuppressWarnings("unchecked")
        List<Livro> livros = (List<Livro>) resultado.get("data");

        assertNotNull("Lista não deve ser null", livros);
        assertTrue("Deve encontrar pelo menos 1 livro", livros.size() >= 1);

        boolean encontrou = false;
        for (Livro l : livros) {
            if (l.getTitulo().contains("Casmurro")) {
                encontrou = true;
                break;
            }
        }
        assertTrue("Deve encontrar livro com 'Casmurro' no título", encontrou);
    }

    @Test
    public void testLivroCriadoPorUsuarioCorreto() throws Exception {
        Livro livro = new Livro();
        livro.setTitulo("Livro Verificar Usuario");
        livro.setIsbn("9785555555555");
        livro.setAutorId(autorIdTeste);

        Livro criado = livroService.criar(livro, emailUsuarioTeste);
        livroIdTeste = criado.getId();

        assertNotNull("Livro deve ter ID de quem criou", criado.getCriadoPor());
        assertEquals("ID do criador deve ser o usuário de teste",
                usuarioIdTeste, criado.getCriadoPor());
    }
}