package controller;

import model.Categoria;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import repository.CategoriaRepositoryImpl;
import service.CategoriaService;
import utils.ObjectMapperProvider;

import java.util.Map;

public class CategoriaController extends ServerResource {

    private final CategoriaService categoriaService;

    public CategoriaController() {
        this.categoriaService = new CategoriaService(new CategoriaRepositoryImpl());
    }

    @Get("json")
    public Representation listar() {
        try {
            String idStr = getAttribute("id");

            if (idStr != null) {
                Map<String, Object> categoria =
                        categoriaService.buscarPorId(Integer.parseInt(idStr));

                JacksonRepresentation<Map<String, Object>> rep =
                        new JacksonRepresentation<>(categoria);
                rep.setObjectMapper(ObjectMapperProvider.get());

                setStatus(Status.SUCCESS_OK);
                return rep;
            }

            var categorias = categoriaService.listarTodas();

            JacksonRepresentation<?> rep =
                    new JacksonRepresentation<>(categorias);
            rep.setObjectMapper(ObjectMapperProvider.get());

            setStatus(Status.SUCCESS_OK);
            return rep;

        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return jsonErro(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return jsonErro("Erro ao buscar categorias");
        }
    }

    @Post("json")
    public Representation criar(Representation entity) {
        try {
            JacksonRepresentation<Categoria> repr =
                    new JacksonRepresentation<>(entity, Categoria.class);
            repr.setObjectMapper(ObjectMapperProvider.get());

            Categoria categoria = repr.getObject();

            Categoria criada = categoriaService.criar(categoria);

            JacksonRepresentation<Categoria> rep =
                    new JacksonRepresentation<>(criada);
            rep.setObjectMapper(ObjectMapperProvider.get());

            setStatus(Status.SUCCESS_CREATED);
            return rep;

        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return jsonErro(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return jsonErro("Erro ao criar categoria");
        }
    }

    @Put("json")
    public Representation atualizar(Representation entity) {
        try {
            String idStr = getAttribute("id");

            if (idStr == null) {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return jsonErro("ID não informado");
            }

            JacksonRepresentation<Categoria> repr =
                    new JacksonRepresentation<>(entity, Categoria.class);
            repr.setObjectMapper(ObjectMapperProvider.get());

            Categoria categoria = repr.getObject();


            categoriaService.atualizar(Integer.parseInt(idStr), categoria);

            setStatus(Status.SUCCESS_OK);
            return new JacksonRepresentation<>(Map.of(
                    "mensagem", "Autor atualizado com sucesso"
            ));


        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return jsonErro(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return jsonErro("Erro ao atualizar categoria");
        }
    }

    @Delete("json")
    public Representation deletar() {
        try {
            String idStr = getAttribute("id");

            if (idStr == null) {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return jsonErro("ID não informado");
            }

            categoriaService.deletar(Integer.parseInt(idStr));

            setStatus(Status.SUCCESS_NO_CONTENT);
            return null;

        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return jsonErro(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return jsonErro("Erro ao deletar categoria");
        }
    }

    private Representation jsonErro(String mensagem) {
        JacksonRepresentation<Map<String, String>> rep =
                new JacksonRepresentation<>(Map.of("erro", mensagem));
        rep.setObjectMapper(ObjectMapperProvider.get());
        return rep;
    }
}
