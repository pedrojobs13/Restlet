package repository;

import config.DatabaseConfig;
import lombok.RequiredArgsConstructor;
import model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CategoriaRepositoryImpl implements CategoriaRepository {
    public List<Categoria> listarTodas() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY nome ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categorias.add(mapResultSetToCategoria(rs));
            }
        }
        return categorias;
    }

    public Categoria buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM categorias WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCategoria(rs);
            }
        }
        return null;
    }

    public Categoria buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM categorias WHERE nome = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCategoria(rs);
            }
        }
        return null;
    }

    public Categoria criar(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categorias (nome) VALUES (?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, categoria.getNome());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                categoria.setId(rs.getInt(1));
            }
        }
        return categoria;
    }

    public boolean atualizar(int id, Categoria categoria) throws SQLException {
        String sql = "UPDATE categorias SET nome = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNome());
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM categorias WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Se houver constraint violation
            if (e.getErrorCode() == 1451) {
                throw new SQLException("Não é possível deletar categoria com livros associados", e);
            }
            throw e;
        }
    }

    public boolean existe(int id) throws SQLException {
        return buscarPorId(id) != null;
    }

    public boolean nomeExiste(String nome) throws SQLException {
        return buscarPorNome(nome) != null;
    }

    public int contarCategorias() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM categorias";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    public List<Categoria> buscarPorLivro(int livroId) throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT c.* FROM categorias c " +
                     "INNER JOIN livros_categorias lc ON c.id = lc.categoria_id " +
                     "WHERE lc.livro_id = ? " +
                     "ORDER BY c.nome";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, livroId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                categorias.add(mapResultSetToCategoria(rs));
            }
        }
        return categorias;
    }

    public int contarLivrosPorCategoria(int categoriaId) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM livros_categorias WHERE categoria_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoriaId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    private Categoria mapResultSetToCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getInt("id"));
        categoria.setNome(rs.getString("nome"));
        return categoria;
    }
}
