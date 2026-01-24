package service;

import model.Livro;
import model.Usuario;
import repository.LivrosRepository;
import repository.UsuarioRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LivroService {

    private final LivrosRepository livrosRepository;
    private final UsuarioRepository usuarioRepository;

    public LivroService(LivrosRepository livrosRepository, UsuarioRepository usuarioRepository) {
        this.livrosRepository = livrosRepository;
        this.usuarioRepository = usuarioRepository;
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

    public Livro criar(Livro livro, String email) throws SQLException {
        validarLivro(livro);
        Usuario user = usuarioRepository.buscarPorEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("Usuário inexistente");
        }

        livro.setCriadoPor(user.getId());
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
        if (livro == null) {
            throw new IllegalArgumentException("Livro não pode ser nulo");
        }

        if (livro.getTitulo() == null || livro.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }

        if (livro.getIsbn() == null || livro.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN é obrigatório");
        }

        if (livro.getAutorId() == null) {
            throw new IllegalArgumentException("Autor é obrigatório");
        }
    }
}
