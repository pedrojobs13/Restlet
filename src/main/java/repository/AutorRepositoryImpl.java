package repository;

import config.DatabaseConfig;
import model.Autor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutorRepositoryImpl implements AutorRepository {
    @Override
    public List<Autor> listarTodos(int page, int limit, String nome) throws SQLException {
        List<Autor> autores = new ArrayList<>();
        int offset = (page - 1) * limit;

        StringBuilder sql = new StringBuilder("SELECT * FROM autores WHERE 1=1");

        if (nome != null && !nome.trim().isEmpty()) {
            sql.append(" AND nome LIKE ?");
        }

        sql.append(" ORDER BY nome ASC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (nome != null && !nome.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + nome + "%");
            }
            stmt.setInt(paramIndex++, limit);
            stmt.setInt(paramIndex, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                autores.add(mapResultSetToAutor(rs));
            }
        }
        return autores;
    }

    @Override
    public Autor buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM autores WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAutor(rs);
            }
        }
        return null;
    }

    @Override
    public Autor criar(Autor autor) throws SQLException {
        String sql = "INSERT INTO autores (nome, bio, data_nascimento) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, autor.getNome());
            stmt.setString(2, autor.getBio());

            if (autor.getDataNascimento() != null) {

                stmt.setDate(3, Date.valueOf(autor.getDataNascimento()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                autor.setId(rs.getInt(1));
            }
        }
        return autor;
    }

    @Override
    public boolean atualizar(int id, Autor autor) throws SQLException {
        String sql = "UPDATE autores SET nome = ?, bio = ?, data_nascimento = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, autor.getNome());
            stmt.setString(2, autor.getBio());

            if (autor.getDataNascimento() != null) {
                stmt.setDate(3, Date.valueOf(autor.getDataNascimento()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setInt(4, id);

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM autores WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new SQLException("Não é possível deletar autor com livros associados", e);
            }
            throw e;
        }
    }

    @Override
    public int contarAutores(String nome) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) as total FROM autores WHERE 1=1");

        if (nome != null && !nome.trim().isEmpty()) {
            sql.append(" AND nome LIKE ?");
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            if (nome != null && !nome.trim().isEmpty()) {
                stmt.setString(1, "%" + nome + "%");
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    @Override
    public List<Autor> buscarPorNacionalidade(String nacionalidade) throws SQLException {
        List<Autor> autores = new ArrayList<>();
        String sql = "SELECT * FROM autores WHERE nacionalidade = ? ORDER BY nome";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nacionalidade);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                autores.add(mapResultSetToAutor(rs));
            }
        }
        return autores;
    }

    @Override
    public boolean existe(int id) throws SQLException {
        return buscarPorId(id) != null;
    }

    private Autor mapResultSetToAutor(ResultSet rs) throws SQLException {
        Autor autor = new Autor();
        autor.setId(rs.getInt("id"));
        autor.setNome(rs.getString("nome"));
        autor.setBio(rs.getString("bio"));

        Date dataNasc = rs.getDate("data_nascimento");
        if (dataNasc != null) {
            autor.setDataNascimento(dataNasc.toLocalDate());
        }

        autor.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
        autor.setAtualizadoEm(rs.getTimestamp("atualizado_em").toLocalDateTime());

        return autor;
    }
}
