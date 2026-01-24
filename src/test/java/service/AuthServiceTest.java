package service;

import model.Usuario;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import repository.UsuarioRepositoryImpl;
import utils.PasswordUtil;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class AuthServiceTest {

    private AuthService authService;
    private UsuarioRepositoryImpl usuarioRepository;
    private Integer usuarioIdTeste;

    @Before
    public void setUp() {
        usuarioRepository = new UsuarioRepositoryImpl();
        authService = new AuthService(usuarioRepository);
    }

    @After
    public void tearDown() throws Exception {
        if (usuarioIdTeste != null) {
            try {
                usuarioRepository.deletar(usuarioIdTeste);
            } catch (Exception e) {
            }
        }
    }

    @Test
    public void testRegistrarUsuarioValido() throws Exception {
        Map<String, String> dados = new HashMap<>();
        dados.put("nome", "João Silva");
        dados.put("email", "joao.teste@email.com");
        dados.put("senha", "senha123");
        dados.put("funcao", "user");

        Map<String, Object> resultado = authService.register(dados);

        @SuppressWarnings("unchecked")
        Map<String, Object> usuario = (Map<String, Object>) resultado.get("usuario");
        usuarioIdTeste = (Integer) usuario.get("id");

        assertNotNull("Resultado não deve ser null", resultado);
        assertTrue("Deve conter token", resultado.containsKey("token"));
        assertTrue("Deve conter mensagem", resultado.containsKey("mensagem"));
        assertNotNull("Token não deve ser null", resultado.get("token"));
        assertNotNull("Usuario não deve ser null", usuario);
        assertEquals("Email deve ser igual", "joao.teste@email.com", usuario.get("email"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarUsuarioSemNome() throws Exception {
        // Arrange
        Map<String, String> dados = new HashMap<>();
        dados.put("email", "teste@email.com");
        dados.put("senha", "senha123");

        authService.register(dados);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarUsuarioSemEmail() throws Exception {
        Map<String, String> dados = new HashMap<>();
        dados.put("nome", "João Silva");
        dados.put("senha", "senha123");

        authService.register(dados);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarUsuarioComSenhaCurta() throws Exception {
        Map<String, String> dados = new HashMap<>();
        dados.put("nome", "João Silva");
        dados.put("email", "joao@email.com");
        dados.put("senha", "123");

        authService.register(dados);
    }

    @Test
    public void testLoginComCredenciaisValidas() throws Exception {
        Map<String, String> dadosRegistro = new HashMap<>();
        dadosRegistro.put("nome", "Maria Silva");
        dadosRegistro.put("email", "maria.login@email.com");
        dadosRegistro.put("senha", "senha123");

        Map<String, Object> registro = authService.register(dadosRegistro);
        @SuppressWarnings("unchecked")
        Map<String, Object> usuario = (Map<String, Object>) registro.get("usuario");
        usuarioIdTeste = (Integer) usuario.get("id");

        Map<String, String> dadosLogin = new HashMap<>();
        dadosLogin.put("email", "maria.login@email.com");
        dadosLogin.put("senha", "senha123");

        Map<String, Object> resultado = authService.login(dadosLogin);

        assertNotNull("Resultado não deve ser null", resultado);
        assertTrue("Deve conter token", resultado.containsKey("token"));
        assertNotNull("Token não deve ser null", resultado.get("token"));
    }

    @Test(expected = SecurityException.class)
    public void testLoginComSenhaIncorreta() throws Exception {
        Map<String, String> dadosRegistro = new HashMap<>();
        dadosRegistro.put("nome", "Pedro Silva");
        dadosRegistro.put("email", "pedro.senha@email.com");
        dadosRegistro.put("senha", "senhaCorreta123");

        Map<String, Object> registro = authService.register(dadosRegistro);
        @SuppressWarnings("unchecked")
        Map<String, Object> usuario = (Map<String, Object>) registro.get("usuario");
        usuarioIdTeste = (Integer) usuario.get("id");

        Map<String, String> dadosLogin = new HashMap<>();
        dadosLogin.put("email", "pedro.senha@email.com");
        dadosLogin.put("senha", "senhaErrada");

        authService.login(dadosLogin);
    }

    @Test(expected = SecurityException.class)
    public void testLoginComEmailInexistente() throws Exception {
        Map<String, String> dadosLogin = new HashMap<>();
        dadosLogin.put("email", "naoexiste@email.com");
        dadosLogin.put("senha", "senha123");

        authService.login(dadosLogin);
    }

    @Test
    public void testSenhaArmazenadaComHash() throws Exception {
        Map<String, String> dados = new HashMap<>();
        dados.put("nome", "Teste Hash");
        dados.put("email", "hash@email.com");
        dados.put("senha", "senha123");

        Map<String, Object> resultado = authService.register(dados);

        @SuppressWarnings("unchecked")
        Map<String, Object> usuarioRetorno = (Map<String, Object>) resultado.get("usuario");
        usuarioIdTeste = (Integer) usuarioRetorno.get("id");

        Usuario usuario = usuarioRepository.buscarPorEmail("hash@email.com");

        assertNotNull("Usuário deve existir", usuario);
        assertNotEquals("Senha não deve ser texto plano", "senha123", usuario.getSenha());
        assertTrue("Senha deve começar com $2a$ (BCrypt)", usuario.getSenha().startsWith("$2a$"));
        assertTrue("Hash deve validar senha", PasswordUtil.checkPassword("senha123", usuario.getSenha()));
    }
}