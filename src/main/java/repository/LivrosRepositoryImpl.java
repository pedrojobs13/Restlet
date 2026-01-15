package repository;

import config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LivrosRepositoryImpl implements LivrosRepository {
    public List<String> listar() {

        List<String> usuarios = new ArrayList<>();

        String sql = "SELECT nome FROM usuario";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(rs.getString("nome"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuarios;
    }
}
