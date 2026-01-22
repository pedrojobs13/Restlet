package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.Headers;
import model.Livro;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import repository.LivrosRepositoryImpl;
import service.LivroService;
import utils.JWTUtil;
import utils.ObjectMapperProvider;

import java.util.Map;

public class LivroController extends ServerResource {

    private final LivroService livroService;

    public LivroController() {
        this.livroService = new LivroService(new LivrosRepositoryImpl());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Get("json")
    public Representation listar() {
        try {
            String idStr = getAttribute("id");

            if (idStr != null) {
                Livro livro = livroService.buscarPorId(Integer.parseInt(idStr));
                JacksonRepresentation<Livro> rep =
                        new JacksonRepresentation<>(livro);
                rep.setObjectMapper(ObjectMapperProvider.get());

                return rep;
            }

            int page = getQueryValue("page") != null
                    ? Integer.parseInt(getQueryValue("page"))
                    : 1;

            int limit = getQueryValue("limit") != null
                    ? Integer.parseInt(getQueryValue("limit"))
                    : 10;

            String titulo = getQueryValue("titulo");

            var response = livroService.listar(page, limit, titulo);
            JacksonRepresentation<Map<String, Object>> rep =
                    new JacksonRepresentation<>(response);
            rep.setObjectMapper(ObjectMapperProvider.get());

            return rep;

        } catch (IllegalArgumentException e) {
            setStatus(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND);
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            setStatus(org.restlet.data.Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of("erro", "Erro ao buscar livros"));
        }
    }

    @Post("json")
    public Representation criar(Representation entity, Headers headers) {
        try {
            JacksonRepresentation<Livro> repr =
                    new JacksonRepresentation<>(entity, Livro.class);
            Livro livro = repr.getObject();
                        Livro criado = livroService.criar(livro, livro.getAutorId());

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