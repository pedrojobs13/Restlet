package controller;

import model.Usuario;
import org.restlet.data.Header;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import repository.UsuarioRepositoryImpl;
import service.UsuarioService;
import utils.JWTUtil;
import utils.ObjectMapperProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioController extends ServerResource {

    private final UsuarioService usuarioService;

    public UsuarioController() {
        this.usuarioService = new UsuarioService(new UsuarioRepositoryImpl());
    }

    @Get("json")
    public Representation listar() {
        try {
            String userRole = (String) getRequest().getAttributes().get("userRole");
            String userEmail = (String) getRequest().getAttributes().get("userEmail");
            String idStr = getAttribute("id");

            if (idStr != null) {
                int id = Integer.parseInt(idStr);

                Usuario usuario = usuarioService.buscarPorId(id);
                if (usuario == null) {
                    setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                    return new JacksonRepresentation<>(Map.of(
                            "erro", "Usuário não encontrado"
                    ));
                }

                Usuario usuarioLogado = usuarioService.buscarPorEmail(userEmail);
                if (usuarioLogado == null) {
                    setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
                    return new JacksonRepresentation<>(Map.of(
                            "erro", "Usuário não autenticado"
                    ));
                }

                if (!usuarioLogado.getId().equals(id) && !"admin".equals(userRole)) {
                    setStatus(Status.CLIENT_ERROR_FORBIDDEN);
                    return new JacksonRepresentation<>(Map.of(
                            "erro", "Você não tem permissão para visualizar este usuário"
                    ));
                }

                Map<String, Object> response = new HashMap<>();
                response.put("id", usuario.getId());
                response.put("nome", usuario.getNome());
                response.put("email", usuario.getEmail());
                response.put("funcao", usuario.getFuncao());
                response.put("criadoEm", usuario.getCriadoEm());

                setStatus(Status.SUCCESS_OK);
                JacksonRepresentation<Map<String, Object>> rep =
                        new JacksonRepresentation<>(response);
                rep.setObjectMapper(ObjectMapperProvider.get());

                return rep;
            }

            if (!"admin".equals(userRole)) {
                setStatus(Status.CLIENT_ERROR_FORBIDDEN);
                return new JacksonRepresentation<>(Map.of(
                        "erro", "Apenas administradores podem listar usuários"
                ));
            }

            List<Usuario> usuarios = usuarioService.listarTodos();

            List<Map<String, Object>> usuariosSemSenha = new ArrayList<>();
            for (Usuario u : usuarios) {
                usuariosSemSenha.add(Map.of(
                        "id", u.getId(),
                        "nome", u.getNome(),
                        "email", u.getEmail(),
                        "funcao", u.getFuncao(),
                        "criadoEm", u.getCriadoEm()
                ));
            }

            setStatus(Status.SUCCESS_OK);

            JacksonRepresentation<Map<String, Object>> rep =
                    new JacksonRepresentation<>(Map.of(
                            "data", usuariosSemSenha,
                            "total", usuariosSemSenha.size()
                    ));
            rep.setObjectMapper(ObjectMapperProvider.get());

            return rep;

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of(
                    "erro", "Erro ao buscar usuários"
            ));
        }
    }

    @Put("json")
    @Patch("senha")
    public Representation atualizarSenha(Representation entity) {
        try {
            Series<Header> headers = getRequestHeaders();
            String authHeader = headers.getFirstValue("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                setStatus(org.restlet.data.Status.CLIENT_ERROR_UNAUTHORIZED);
                return new JacksonRepresentation<>(Map.of("erro", "Token não informado"));
            }

            String token = authHeader.substring("Bearer ".length());

            String email = JWTUtil.getEmailFromToken(token);

            JacksonRepresentation<Map> repr =
                    new JacksonRepresentation<>(entity, Map.class);
            repr.setObjectMapper(ObjectMapperProvider.get());

            String senha = (String) repr.getObject().get("senha");

            var response = usuarioService.atualizarSenhaUsuario(email, senha);
            setStatus(Status.SUCCESS_CREATED);
            return new JacksonRepresentation<>(response);

        } catch (IllegalArgumentException e) {
            setStatus(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND);
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            setStatus(org.restlet.data.Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of("erro", "Erro ao atualizar usuario"));
        }
    }

    @Put("json")
    public Representation atualizar(Representation entity) {
        try {
            Series<Header> headers = getRequestHeaders();
            String authHeader = headers.getFirstValue("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
                return new JacksonRepresentation<>("Token não informado");
            }

            String token = authHeader.substring("Bearer ".length());
            String email = JWTUtil.getEmailFromToken(token);

            JacksonRepresentation<Map> repr =
                    new JacksonRepresentation<>(entity, Map.class);
            repr.setObjectMapper(ObjectMapperProvider.get());

            Map<String, Object> body = repr.getObject();

            String nome = (String) body.get("nome");
            if (nome == null || nome.isBlank()) {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return new JacksonRepresentation<>("Nome não fornecido");
            }

            usuarioService.atualizarNomeUsuario(email, nome);

            setStatus(Status.SUCCESS_OK);
            return new JacksonRepresentation<>("Nome atualizado com sucesso");

        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return new JacksonRepresentation<>(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>("Erro ao atualizar usuário");
        }
    }

    @Delete("json")
    public Representation deletar() {
        try {
            Series<Header> headers = getRequestHeaders();

            String authHeader = headers.getFirstValue("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                setStatus(org.restlet.data.Status.CLIENT_ERROR_UNAUTHORIZED);
                return new JacksonRepresentation<>(Map.of("erro", "Token não informado"));
            }

            String token = authHeader.substring("Bearer ".length());

            String email = JWTUtil.getEmailFromToken(token);

            usuarioService.deletarUsuario(email);

            setStatus(Status.SUCCESS_NO_CONTENT);
            return null;

        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return new JacksonRepresentation<>(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>("Erro ao deletar categoria");
        }
    }


    @SuppressWarnings("unchecked")
    private Series<Header> getRequestHeaders() {
        return (Series<Header>) getRequest().getAttributes()
                .get("org.restlet.http.headers");
    }
}