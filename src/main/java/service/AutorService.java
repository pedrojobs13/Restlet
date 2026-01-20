package service;

import lombok.RequiredArgsConstructor;

import model.Autor;
import repository.AutorRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AutorService {
    private final AutorRepository autorRepository;

    public Autor buscarPorId(int id) throws SQLException {
        Autor autor = autorRepository.buscarPorId(id);
        if (autor == null) {
            throw new IllegalArgumentException("Autor não encontrado");
        }
        return autor;
    }

    public Map<String, Object> listar(int page, int limit, String nome) throws SQLException {
        List<Autor> autores = autorRepository.listarTodos(page, limit, nome);
        int total = autorRepository.contarAutores(nome);

        Map<String, Object> response = new HashMap<>();
        response.put("data", autores);
        response.put("page", page);
        response.put("limit", limit);
        response.put("total", total);
        response.put("totalPages", (int) Math.ceil((double) total / limit));

        return response;
    }

    public Autor criar(Autor autor) throws SQLException {
        validarAutor(autor);

        return autorRepository.criar(autor);
    }

    public void atualizar(int id, Autor autor) throws SQLException {
        if (!autorRepository.existe(id)) {
            throw new IllegalArgumentException("Autor não encontrado");
        }

        validarAutor(autor);

        boolean atualizado = autorRepository.atualizar(id, autor);
        if (!atualizado) {
            throw new IllegalStateException("Falha ao atualizar autor");
        }
    }

    public void deletar(int id) throws SQLException {
        if (!autorRepository.existe(id)) {
            throw new IllegalArgumentException("Autor não encontrado");
        }

        boolean deletado = autorRepository.deletar(id);
        if (!deletado) {
            throw new IllegalStateException("Falha ao deletar autor");
        }
    }

    private void validarAutor(Autor autor) {
        if (autor.getNome() == null || autor.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do autor é obrigatório");
        }

        if (autor.getNome().length() > 150) {
            throw new IllegalArgumentException("Nome não pode exceder 150 caracteres");
        }

        if (autor.getBio() != null && autor.getBio().length() > 255) {
            throw new IllegalArgumentException("Bio não pode exceder 255 caracteres");
        }
    }
}
