package service;

import lombok.RequiredArgsConstructor;
import model.Livro;
import repository.LivrosRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LivroService {

    private final LivrosRepository livrosRepository;

    public LivroService(LivrosRepository livrosRepository) {
        this.livrosRepository = livrosRepository;
    }

    public Livro buscarPorId(int id) throws SQLException {
        Livro livro = livrosRepository.buscarPorId(id);
        if (livro == null) {
            throw new IllegalArgumentException("Livro não encontrado");
        }
        return livro;
    }

    public Map<String, Object> listar(int page, int limit, String titulo) throws SQLException {
        List<Livro> livros = livrosRepository.listarTodos(page, limit, titulo);

        Map<String, Object> response = new HashMap<>();
        response.put("data", livros);
        response.put("page", page);
        response.put("limit", limit);
        response.put("total", livros.size());

        return response;
    }

    public Livro criar(Livro livro, int usuarioId) throws SQLException {
        validarLivro(livro);

        livro.setCriadoPor(usuarioId);

        return livrosRepository.criar(livro);
    }

    public void atualizar(int id, Livro livro) throws SQLException {
        validarLivro(livro);

        boolean atualizado = livrosRepository.atualizar(id, livro);
        if (!atualizado) {
            throw new IllegalArgumentException("Livro não encontrado");
        }
    }

    public void deletar(int id) throws SQLException {
        boolean deletado = livrosRepository.deletar(id);
        if (!deletado) {
            throw new IllegalArgumentException("Livro não encontrado");
        }
    }

    private void validarLivro(Livro livro) {
        if (livro.getTitulo() == null || livro.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }
    }
}
