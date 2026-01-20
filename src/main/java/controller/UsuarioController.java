package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import model.Usuario;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import service.UsuarioService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class UsuarioController extends ServerResource {

    private final UsuarioService usuarioService;
    private final ObjectMapper mapper;

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
                return new JacksonRepresentation<>(response);
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
            return new JacksonRepresentation<>(Map.of(
                    "data", usuariosSemSenha,
                    "total", usuariosSemSenha.size()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of(
                    "erro", "Erro ao buscar usuários"
            ));
        }
    }
}