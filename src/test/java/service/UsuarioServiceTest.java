package service;

import model.Usuario;
import org.junit.Before;
import org.junit.Test;
import repository.UsuarioRepository;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {

    private UsuarioRepository usuarioRepository;
    private UsuarioService usuarioService;

    @Before
    public void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        usuarioService = new UsuarioService(usuarioRepository);
    }

    @Test
    public void testBuscarPorId() throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(1);

        when(usuarioRepository.buscarPorId(1)).thenReturn(usuario);

        Usuario result = usuarioService.buscarPorId(1);

        assertEquals(Integer.valueOf(1), result.getId());
    }

    @Test
    public void testBuscarPorEmail() throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@email.com");

        when(usuarioRepository.buscarPorEmail("teste@email.com")).thenReturn(usuario);

        Usuario result = usuarioService.buscarPorEmail("teste@email.com");

        assertEquals("teste@email.com", result.getEmail());
    }

    @Test
    public void testListarTodos() throws SQLException {
        List<Usuario> usuarios = Arrays.asList(new Usuario(), new Usuario());

        when(usuarioRepository.listarTodos()).thenReturn(usuarios);

        List<Usuario> result = usuarioService.listarTodos();

        assertEquals(2, result.size());
    }

    @Test
    public void testAtualizarSenhaUsuarioSucesso() throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(1);

        when(usuarioRepository.buscarPorEmail("teste@email.com")).thenReturn(usuario);
        when(usuarioRepository.atualizarSenha(eq(1), anyString())).thenReturn(true);

        boolean result = usuarioService.atualizarSenhaUsuario("teste@email.com", "123456");

        assertTrue(result);
    }

    @Test
    public void testAtualizarSenhaUsuarioSenhaInvalida() throws SQLException {
        boolean result = usuarioService.atualizarSenhaUsuario("teste@email.com", "");

        assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void testAtualizarSenhaUsuarioInexistente() throws SQLException {
        when(usuarioRepository.buscarPorEmail("naoexiste@email.com")).thenReturn(null);
        usuarioService.atualizarSenhaUsuario("naoexiste@email.com", "123456");
    }

    @Test
    public void testDeletarUsuario() throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(1);

        when(usuarioRepository.buscarPorEmail("teste@email.com")).thenReturn(usuario);

        usuarioService.deletarUsuario("teste@email.com");

        verify(usuarioRepository).deletar(1);
    }

    @Test(expected = NullPointerException.class)
    public void testDeletarUsuarioInexistente() throws SQLException {
        when(usuarioRepository.buscarPorEmail("naoexiste@email.com")).thenReturn(null);
        usuarioService.deletarUsuario("naoexiste@email.com");
    }

    @Test
    public void testAtualizarNomeUsuario() throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(1);

        when(usuarioRepository.buscarPorEmail("teste@email.com")).thenReturn(usuario);
        when(usuarioRepository.atualizar(eq(1), any(Usuario.class))).thenReturn(true);

        boolean result = usuarioService.atualizarNomeUsuario("teste@email.com", "Novo Nome");

        assertTrue(result);
        assertEquals("Novo Nome", usuario.getNome());
    }

    @Test(expected = NullPointerException.class)
    public void testAtualizarNomeUsuarioInexistente() throws SQLException {
        when(usuarioRepository.buscarPorEmail("naoexiste@email.com")).thenReturn(null);
        usuarioService.atualizarNomeUsuario("naoexiste@email.com", "Nome");
    }
}
