package repository;

import model.Livro;

import java.sql.SQLException;
import java.util.List;

public interface LivrosRepository {
    List<Livro> listarTodos(int page, int limit, String titulo) throws SQLException;
    Livro buscarPorId(int id) throws SQLException;
    Livro criar(Livro livro) throws SQLException;
    boolean atualizar(int id, Livro livro) throws SQLException;
    boolean deletar(int id) throws SQLException;
    void associarCategoria(int livroId, int categoriaId) throws SQLException;
    List<Livro> buscarPorAutor(int autorId, int page, int limit) throws SQLException;
    int contarLivrosPorAutor(int autorId) throws SQLException;
    void desassociarCategoria(int livroId, int categoriaId) throws SQLException;
    void atualizarCategorias(int livroId, List<Integer> categoriaIds) throws SQLException ;
    boolean isbnExiste(String isbn) throws SQLException;
}
