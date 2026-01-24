package service;

import model.Usuario;
import repository.UsuarioRepository;
import utils.PasswordUtil;

import java.sql.SQLException;
import java.util.List;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        return usuarioRepository.buscarPorId(id);
    }

    public Usuario buscarPorEmail(String email) throws SQLException {
        return usuarioRepository.buscarPorEmail(email);
    }

    public List<Usuario> listarTodos() throws SQLException {
        return usuarioRepository.listarTodos();
    }

    public boolean atualizarSenhaUsuario(
            String email,
            String novaSenha
    ) throws SQLException {
        Usuario usuario = usuarioRepository.buscarPorEmail(email);

        if (novaSenha != null && !novaSenha.trim().isEmpty()) {
            String senhaHash = PasswordUtil.hashPassword(novaSenha);
            return usuarioRepository.atualizarSenha(usuario.getId(), senhaHash);
        }

        return false;
    }

    public void deletarUsuario(String email) throws SQLException {
        Usuario usuario = usuarioRepository.buscarPorEmail(email);
        usuarioRepository.deletar(usuario.getId());
    }

    public boolean atualizarNomeUsuario(String email, String nome) throws SQLException {

        Usuario usuario = usuarioRepository.buscarPorEmail(email);
        usuario.setNome(nome);

        return usuarioRepository.atualizar(usuario.getId(), usuario);
    }
}