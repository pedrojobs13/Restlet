package repository;

import model.Categoria;

import java.sql.SQLException;
import java.util.List;

public interface CategoriaRepository {

    List<Categoria> listarTodas() throws SQLException;
    Categoria buscarPorId(int id) throws SQLException;
    Categoria buscarPorNome(String nome) throws SQLException;
    Categoria criar(Categoria categoria) throws SQLException;
    boolean atualizar(int id, Categoria categoria) throws SQLException;
    boolean deletar(int id) throws SQLException;
    boolean existe(int id) throws SQLException;
    boolean nomeExiste(String nome) throws SQLException;
    int contarCategorias() throws SQLException;
    List<Categoria> buscarPorLivro(int livroId) throws SQLException;
    int contarLivrosPorCategoria(int categoriaId) throws SQLException;
}
