package service;

import lombok.RequiredArgsConstructor;
import model.Usuario;
import repository.UsuarioRepository;
import utils.JWTUtil;
import utils.PasswordUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Map<String, Object> register(Map<String, String> dados) throws SQLException {
        String nome = dados.get("nome");
        String email = dados.get("email");
        String senha = dados.get("senha");
        String funcao = dados.getOrDefault("funcao", "user");

        validarRegistro(nome, email, senha, funcao);

        if (usuarioRepository.buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(PasswordUtil.hashPassword(senha));
        usuario.setFuncao(funcao);

        Usuario criado = usuarioRepository.criar(usuario);

        String token = JWTUtil.generateToken(criado.getEmail(), criado.getFuncao());

        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Usuário registrado com sucesso");
        response.put("token", token);
        response.put("usuario", Map.of(
                "id", criado.getId(),
                "nome", criado.getNome(),
                "email", criado.getEmail(),
                "funcao", criado.getFuncao()
        ));

        return response;
    }

    public Map<String, Object> login(Map<String, String> dados) throws SQLException {
        String email = dados.get("email");
        String senha = dados.get("senha");

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }

        Usuario usuario = usuarioRepository.buscarPorEmail(email);

        if (usuario == null || !PasswordUtil.checkPassword(senha, usuario.getSenha())) {
            throw new SecurityException("Email ou senha inválidos");
        }

        String token = JWTUtil.generateToken(usuario.getEmail(), usuario.getFuncao());

        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Login realizado com sucesso");
        response.put("token", token);
        response.put("usuario", Map.of(
                "id", usuario.getId(),
                "nome", usuario.getNome(),
                "email", usuario.getEmail(),
                "funcao", usuario.getFuncao()
        ));

        return response;
    }

    private void validarRegistro(String nome, String email, String senha, String funcao) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }

        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
        }

        if (!funcao.equals("admin") && !funcao.equals("user")) {
            throw new IllegalArgumentException("Função deve ser 'admin' ou 'user'");
        }
    }
}
