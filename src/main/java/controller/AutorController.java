package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.Autor;
import model.Livro;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import repository.AutorRepositoryImpl;
import service.AutorService;
import utils.ObjectMapperProvider;

import java.util.Map;

public class AutorController extends ServerResource {

    private final AutorService autorService;
    private final ObjectMapper mapper;

    public AutorController() {
        this.autorService = new AutorService(new AutorRepositoryImpl());
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Get("json")
    public Representation listar() {
        try {
            String idStr = getAttribute("id");

            if (idStr != null) {
                Autor autor = autorService.buscarPorId(Integer.parseInt(idStr));
                setStatus(Status.SUCCESS_OK);
                JacksonRepresentation<Autor> rep =
                        new JacksonRepresentation<>(autor);
                rep.setObjectMapper(ObjectMapperProvider.get());

                return rep;
            }

            int page = getQueryValue("page") != null
                    ? Integer.parseInt(getQueryValue("page")) : 1;

            int limit = getQueryValue("limit") != null
                    ? Integer.parseInt(getQueryValue("limit")) : 10;

            String nome = getQueryValue("nome");

            var response = autorService.listar(page, limit, nome);
            setStatus(Status.SUCCESS_OK);
            JacksonRepresentation<Map<String, Object>> rep =
                    new JacksonRepresentation<>(response);
            rep.setObjectMapper(ObjectMapperProvider.get());

            return rep;
        } catch (NumberFormatException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return new JacksonRepresentation<>(Map.of("erro", "ID inválido"));

        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of("erro", "Erro ao buscar autores: " + e.getMessage()));
        }
    }

    @Post("json")
    public Representation criar(Representation entity) {
        try {

            String json = entity.getText();
            Autor autor = mapper.readValue(json, Autor.class);

            if (autor.getNome() == null || autor.getNome().trim().isEmpty()) {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return new JacksonRepresentation<>(Map.of("erro", "Nome do autor é obrigatório"));
            }

            if (autor.getNome().length() > 150) {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return new JacksonRepresentation<>(Map.of("erro", "Nome não pode exceder 150 caracteres"));
            }

            if (autor.getBio() != null && autor.getBio().length() > 255) {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return new JacksonRepresentation<>(Map.of("erro", "Bio não pode exceder 255 caracteres"));
            }

            Autor criado = autorService.criar(autor);

            setStatus(Status.SUCCESS_CREATED);

            JacksonRepresentation<Autor> rep =
                    new JacksonRepresentation<>(criado);
            rep.setObjectMapper(ObjectMapperProvider.get());

            return rep;
        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of("erro", "Erro ao criar autor: " + e.getMessage()));
        }
    }

    @Put("json")
    public Representation atualizar(Representation entity) {
        try {
            String idStr = getAttribute("id");
            if (idStr == null) {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return new JacksonRepresentation<>(Map.of("erro", "ID não fornecido"));
            }

            int id = Integer.parseInt(idStr);

            String json = entity.getText();
            Autor autor = mapper.readValue(json, Autor.class);

            if (autor.getNome() == null || autor.getNome().trim().isEmpty()) {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return new JacksonRepresentation<>(Map.of("erro", "Nome do autor é obrigatório"));
            }

            if (autor.getNome().length() > 150) {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return new JacksonRepresentation<>(Map.of("erro", "Nome não pode exceder 150 caracteres"));
            }

            if (autor.getBio() != null && autor.getBio().length() > 255) {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return new JacksonRepresentation<>(Map.of("erro", "Bio não pode exceder 255 caracteres"));
            }

            autorService.atualizar(id, autor);

            setStatus(Status.SUCCESS_OK);
            return new JacksonRepresentation<>(Map.of(
                    "mensagem", "Autor atualizado com sucesso"
            ));

        } catch (NumberFormatException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return new JacksonRepresentation<>(Map.of("erro", "ID inválido"));

        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of("erro", "Erro ao atualizar autor: " + e.getMessage()));
        }
    }

    @Delete("json")
    public Representation deletar() {
        try {
            String idStr = getAttribute("id");
            if (idStr == null) {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return new JacksonRepresentation<>(Map.of("erro", "ID não fornecido"));
            }

            int id = Integer.parseInt(idStr);

            autorService.deletar(id);

            setStatus(Status.SUCCESS_OK);
            return new JacksonRepresentation<>(Map.of(
                    "mensagem", "Autor deletado com sucesso"
            ));

        } catch (NumberFormatException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return new JacksonRepresentation<>(Map.of("erro", "ID inválido"));

        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("livros associados")) {
                setStatus(Status.CLIENT_ERROR_CONFLICT);
            } else {
                setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            }
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of("erro", "Erro ao deletar autor: " + e.getMessage()));
        }
    }
}