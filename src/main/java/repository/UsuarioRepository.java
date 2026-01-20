package repository;

import model.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface UsuarioRepository {
    List<Usuario> listarTodos() throws SQLException;

    Usuario buscarPorId(int id) throws SQLException;

    Usuario criar(Usuario usuario) throws SQLException;

    boolean atualizar(int id, Usuario usuario) throws SQLException;

    boolean atualizarSenha(int id, String novaSenhaHash) throws SQLException;

    boolean deletar(int id) throws SQLException;

    boolean emailExiste(String email) throws SQLException;

    int contarUsuarios() throws SQLException;

    List<Usuario> listarPorFuncao(String funcao) throws SQLException;

    Usuario buscarPorEmail(String email) throws SQLException;

}
