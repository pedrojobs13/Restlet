package service;

import model.Categoria;
import repository.CategoriaRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }


    public Map<String, Object> buscarPorId(int id) throws SQLException {
        Categoria categoria = categoriaRepository.buscarPorId(id);
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não encontrada");
        }

        int totalLivros = categoriaRepository.contarLivrosPorCategoria(id);

        Map<String, Object> response = new HashMap<>();
        response.put("id", categoria.getId());
        response.put("nome", categoria.getNome());
        response.put("totalLivros", totalLivros);

        return response;
    }

    public Map<String, Object> listarTodas() throws SQLException{
        List<Categoria> categorias = categoriaRepository.listarTodas();

        Map<String, Object> response = new HashMap<>();
        response.put("data", categorias);
        response.put("total", categorias.size());

        return response;
    }

    public Categoria criar(Categoria categoria) throws SQLException {
        validarCategoria(categoria);

        if (categoriaRepository.nomeExiste(categoria.getNome())) {
            throw new IllegalStateException("Já existe uma categoria com este nome");
        }

        return categoriaRepository.criar(categoria);
    }

    public void atualizar(int id, Categoria categoria) throws SQLException {
        if (!categoriaRepository.existe(id)) {
            throw new IllegalArgumentException("Categoria não encontrada");
        }

        validarCategoria(categoria);

        Categoria existente = categoriaRepository.buscarPorNome(categoria.getNome());
        if (existente != null && existente.getId() != id) {
            throw new IllegalStateException("Já existe uma categoria com este nome");
        }

        boolean atualizado = categoriaRepository.atualizar(id, categoria);
        if (!atualizado) {
            throw new IllegalStateException("Falha ao atualizar categoria");
        }
    }

    public void deletar(int id) throws SQLException {
        if (!categoriaRepository.existe(id)) {
            throw new IllegalArgumentException("Categoria não encontrada");
        }

        int totalLivros = categoriaRepository.contarLivrosPorCategoria(id);
        if (totalLivros > 0) {
            throw new IllegalStateException(
                    "Não é possível deletar categoria que possui livros associados",
                    null
            );
        }

        boolean deletado = categoriaRepository.deletar(id);
        if (!deletado) {
            throw new IllegalStateException("Falha ao deletar categoria");
        }
    }

    private void validarCategoria(Categoria categoria) {
        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria é obrigatório");
        }

        if (categoria.getNome().length() > 100) {
            throw new IllegalArgumentException("Nome não pode exceder 100 caracteres");
        }
    }
}
