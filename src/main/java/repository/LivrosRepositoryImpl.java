package repository;

import config.DatabaseConfig;
import lombok.RequiredArgsConstructor;
import model.Categoria;
import model.Livro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class LivrosRepositoryImpl implements LivrosRepository {
    public List<Livro> listarTodos(int page, int limit, String titulo) throws SQLException {
        List<Livro> livros = new ArrayList<>();
        int offset = (page - 1) * limit;

        StringBuilder sql = new StringBuilder("SELECT * FROM livros WHERE 1=1");

        if (titulo != null && !titulo.trim().isEmpty()) {
            sql.append(" AND titulo LIKE ?");
        }

        sql.append(" ORDER BY id DESC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (titulo != null && !titulo.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + titulo + "%");
            }
            stmt.setInt(paramIndex++, limit);
            stmt.setInt(paramIndex, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Livro livro = mapResultSetToLivro(rs);
                // Carregar categorias do livro
                livro.setCategorias(buscarCategoriasPorLivro(conn, livro.getId()));
                livros.add(livro);
            }
        }
        return livros;
    }

    public Livro buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM livros WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Livro livro = mapResultSetToLivro(rs);
                livro.setCategorias(buscarCategoriasPorLivro(conn, id));
                return livro;
            }
        }
        return null;
    }

    /**
     * Buscar livros por autor (para endpoint /autores/{id}/livros)
     */
    public List<Livro> buscarPorAutor(int autorId, int page, int limit) throws SQLException {
        List<Livro> livros = new ArrayList<>();
        int offset = (page - 1) * limit;

        String sql = "SELECT * FROM livros WHERE autor_id = ? ORDER BY titulo ASC LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, autorId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Livro livro = mapResultSetToLivro(rs);
                livro.setCategorias(buscarCategoriasPorLivro(conn, livro.getId()));
                livros.add(livro);
            }
        }
        return livros;
    }

    public int contarLivrosPorAutor(int autorId) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM livros WHERE autor_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, autorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    public Livro criar(Livro livro) throws SQLException {
        String sql = "INSERT INTO livros (titulo, descricao, ano_de_publicacao, isbn, autor_id, criado_por) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getDescricao());

            if (livro.getAnoDePublicacao() != null) {
                stmt.setInt(3, livro.getAnoDePublicacao());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, livro.getIsbn());
            stmt.setInt(5, livro.getAutorId());
            stmt.setInt(6, livro.getCriadoPor());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                livro.setId(rs.getInt(1));
            }

            // Associar categorias se fornecidas
            if (livro.getCategorias() != null && !livro.getCategorias().isEmpty()) {
                for (Categoria cat : livro.getCategorias()) {
                    associarCategoria(conn, livro.getId(), cat.getId());
                }
            }

            conn.commit(); // Confirmar transação

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Reverter em caso de erro
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConfig.closeConnection(conn);
            }
        }

        return livro;
    }

    public boolean atualizar(int id, Livro livro) throws SQLException {
        String sql = "UPDATE livros SET titulo = ?, descricao = ?, ano_de_publicacao = ?, " +
                     "isbn = ?, autor_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getDescricao());

            if (livro.getAnoDePublicacao() != null) {
                stmt.setInt(3, livro.getAnoDePublicacao());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, livro.getIsbn());
            stmt.setInt(5, livro.getAutorId());
            stmt.setInt(6, id);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM livros WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public void associarCategoria(int livroId, int categoriaId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            associarCategoria(conn, livroId, categoriaId);
        } finally {
            DatabaseConfig.closeConnection(conn);
        }
    }

    private void associarCategoria(Connection conn, int livroId, int categoriaId) throws SQLException {
        String sql = "INSERT IGNORE INTO livros_categorias (livro_id, categoria_id) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, livroId);
            stmt.setInt(2, categoriaId);
            stmt.executeUpdate();
        }
    }

    public void desassociarCategoria(int livroId, int categoriaId) throws SQLException {
        String sql = "DELETE FROM livros_categorias WHERE livro_id = ? AND categoria_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, livroId);
            stmt.setInt(2, categoriaId);
            stmt.executeUpdate();
        }
    }

    public void atualizarCategorias(int livroId, List<Integer> categoriaIds) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            String deleteSql = "DELETE FROM livros_categorias WHERE livro_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, livroId);
            deleteStmt.executeUpdate();

            if (categoriaIds != null && !categoriaIds.isEmpty()) {
                for (Integer catId : categoriaIds) {
                    associarCategoria(conn, livroId, catId);
                }
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConfig.closeConnection(conn);
            }
        }
    }

    private List<Categoria> buscarCategoriasPorLivro(Connection conn, int livroId) throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT c.* FROM categorias c " +
                     "INNER JOIN livros_categorias lc ON c.id = lc.categoria_id " +
                     "WHERE lc.livro_id = ? " +
                     "ORDER BY c.nome";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, livroId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Categoria cat = new Categoria();
                cat.setId(rs.getInt("id"));
                cat.setNome(rs.getString("nome"));
                categorias.add(cat);
            }
        }
        return categorias;
    }

    public int contarLivros(String titulo) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) as total FROM livros WHERE 1=1");

        if (titulo != null && !titulo.trim().isEmpty()) {
            sql.append(" AND titulo LIKE ?");
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            if (titulo != null && !titulo.trim().isEmpty()) {
                stmt.setString(1, "%" + titulo + "%");
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    public boolean existe(int id) throws SQLException {
        return buscarPorId(id) != null;
    }

    public boolean isbnExiste(String isbn) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM livros WHERE isbn = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        }
        return false;
    }

    private Livro mapResultSetToLivro(ResultSet rs) throws SQLException {
        Livro livro = new Livro();
        livro.setId(rs.getInt("id"));
        livro.setTitulo(rs.getString("titulo"));
        livro.setDescricao(rs.getString("descricao"));

        int ano = rs.getInt("ano_de_publicacao");
        if (!rs.wasNull()) {
            livro.setAnoDePublicacao(ano);
        }

        livro.setIsbn(rs.getString("isbn"));
        livro.setAutorId(rs.getInt("autor_id"));
        livro.setCriadoPor(rs.getInt("criado_por"));
        livro.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
        livro.setAtualizadoEm(rs.getTimestamp("atualizado_em").toLocalDateTime());

        return livro;
    }
}
