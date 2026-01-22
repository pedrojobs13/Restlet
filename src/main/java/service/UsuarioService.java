package service;

import lombok.RequiredArgsConstructor;
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

    public boolean atualizarUsuario(
            int id,
            Usuario usuario,
            String novaSenha
    ) throws SQLException {
        boolean atualizado = usuarioRepository.atualizar(id, usuario);

        if (novaSenha != null && !novaSenha.trim().isEmpty()) {
            String senhaHash = PasswordUtil.hashPassword(novaSenha);
            usuarioRepository.atualizarSenha(id, senhaHash);
        }

        return atualizado;
    }

    public boolean deletarUsuario(int id) throws SQLException {
        return usuarioRepository.deletar(id);
    }
}