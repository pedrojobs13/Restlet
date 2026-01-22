package service;

import lombok.RequiredArgsConstructor;
import model.Autor;
import model.Livro;
import repository.AutorRepository;
import repository.LivrosRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutorLivrosService {

    private final AutorRepository autorRepository;
    private final LivrosRepository livrosRepository;

    public AutorLivrosService(AutorRepository autorRepository, LivrosRepository livrosRepository) {
        this.autorRepository = autorRepository;
        this.livrosRepository = livrosRepository;
    }

    public Map<String, Object> listarLivrosDoAutor(int autorId, int page, int limit) throws SQLException {
        Autor autor = autorRepository.buscarPorId(autorId);
        if (autor == null) {
            throw new IllegalArgumentException("Autor não encontrado");
        }

        List<Livro> livros = livrosRepository.buscarPorAutor(autorId, page, limit);
        int total = livrosRepository.contarLivrosPorAutor(autorId);

        Map<String, Object> response = new HashMap<>();

        response.put("autor", Map.of(
                "id", autor.getId(),
                "nome", autor.getNome(),
                "bio", autor.getBio(),
                "dataNascimento", autor.getDataNascimento()
        ));

        response.put("livros", livros);

        response.put("pagination", Map.of(
                "page", page,
                "limit", limit,
                "total", total,
                "totalPages", (int) Math.ceil((double) total / limit)
        ));

        return response;
    }
}
