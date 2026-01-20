package repository;

import config.DatabaseConfig;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepository {
    @Override
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY id DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }
        return usuarios;
    }

    @Override
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUsuario(rs);
            }
        }
        return null;
    }

    @Override
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUsuario(rs);
            }
        }
        return null;
    }

    @Override
    public Usuario criar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, senha, funcao) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getFuncao());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                usuario.setId(rs.getInt(1));
            }
        }
        return usuario;
    }

    @Override
    public boolean atualizar(int id, Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ?, email = ?, funcao = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getFuncao());
            stmt.setInt(4, id);

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean atualizarSenha(int id, String novaSenhaHash) throws SQLException {
        String sql = "UPDATE usuarios SET senha = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novaSenhaHash);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean emailExiste(String email) throws SQLException {
        return buscarPorEmail(email) != null;
    }

    @Override
    public int contarUsuarios() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM usuarios";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    @Override
    public List<Usuario> listarPorFuncao(String funcao) throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE funcao = ? ORDER BY nome";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, funcao);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }
        return usuarios;
    }


    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();

        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setFuncao(rs.getString("funcao"));
        usuario.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
        usuario.setAtualizadoEm(rs.getTimestamp("atualizado_em").toLocalDateTime());
        return usuario;
    }
}
