package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Usuario {
    private Integer id;
    private String nome;
    private String email;
    private String senha;
    private String funcao;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
