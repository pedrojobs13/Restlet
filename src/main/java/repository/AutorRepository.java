package repository;

import model.Autor;

import java.sql.SQLException;
import java.util.List;

public interface AutorRepository {
    List<Autor> listarTodos(int page, int limit, String nome) throws SQLException;

    Autor buscarPorId(int id) throws SQLException;

    Autor criar(Autor autor) throws SQLException;

    boolean atualizar(int id, Autor autor) throws SQLException;

    boolean deletar(int id) throws SQLException;

    int contarAutores(String nome) throws SQLException;

    List<Autor> buscarPorNacionalidade(String nacionalidade) throws SQLException;

    boolean existe(int id) throws SQLException;
}
