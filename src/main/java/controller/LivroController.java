package controller;

import lombok.RequiredArgsConstructor;
import model.Livro;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import service.LivroService;

import java.util.Map;

@RequiredArgsConstructor
public class LivroController extends ServerResource {

    private final LivroService livroService;

    @Get("json")
    public Representation listar() {
        try {
            String idStr = getAttribute("id");

            if (idStr != null) {
                Livro livro = livroService.buscarPorId(Integer.parseInt(idStr));
                return new JacksonRepresentation<>(livro);
            }

            int page = getQueryValue("page") != null
                    ? Integer.parseInt(getQueryValue("page"))
                    : 1;

            int limit = getQueryValue("limit") != null
                    ? Integer.parseInt(getQueryValue("limit"))
                    : 10;

            String titulo = getQueryValue("titulo");

            var response = livroService.listar(page, limit, titulo);
            return new JacksonRepresentation<>(response);

        } catch (IllegalArgumentException e) {
            setStatus(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND);
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            setStatus(org.restlet.data.Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of("erro", "Erro ao buscar livros"));
        }
    }

    @Post("json")
    public Representation criar(Representation entity) {
        try {
            JacksonRepresentation<Livro> repr =
                    new JacksonRepresentation<>(entity, Livro.class);
            Livro livro = repr.getObject();

            int usuarioId = 1;

            Livro criado = livroService.criar(livro, usuarioId);

            setStatus(org.restlet.data.Status.SUCCESS_CREATED);
            return new JacksonRepresentation<>(criado);

        } catch (IllegalArgumentException e) {
            setStatus(org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST);
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            setStatus(org.restlet.data.Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of("erro", "Erro ao criar livro"));
        }
    }

    @Put("json")
    public Representation atualizar(Representation entity) {
        try {
            String idStr = getAttribute("id");
            if (idStr == null) {
                setStatus(org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST);
                return new JacksonRepresentation<>(Map.of("erro", "ID não fornecido"));
            }

            JacksonRepresentation<Livro> repr =
                    new JacksonRepresentation<>(entity, Livro.class);
            Livro livro = repr.getObject();

            livroService.atualizar(Integer.parseInt(idStr), livro);

            return new JacksonRepresentation<>(Map.of(
                    "mensagem", "Livro atualizado com sucesso"
            ));

        } catch (IllegalArgumentException e) {
            setStatus(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND);
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            setStatus(org.restlet.data.Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of("erro", "Erro ao atualizar livro"));
        }
    }

    @Delete("json")
    public Representation deletar() {
        try {
            String idStr = getAttribute("id");
            if (idStr == null) {
                setStatus(org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST);
                return new JacksonRepresentation<>(Map.of("erro", "ID não fornecido"));
            }

            livroService.deletar(Integer.parseInt(idStr));

            setStatus(org.restlet.data.Status.SUCCESS_NO_CONTENT);
            return new JacksonRepresentation<>(Map.of(
                    "mensagem", "Livro deletado com sucesso"
            ));

        } catch (IllegalArgumentException e) {
            setStatus(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND);
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            setStatus(org.restlet.data.Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of("erro", "Erro ao deletar livro"));
        }
    }
}