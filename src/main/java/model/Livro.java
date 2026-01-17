package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Livro {
    private Integer id;
    private String titulo;
    private String descricao;
    private Integer anoDePublicacao;
    private String isbn;
    private Integer autorId;
    private Integer criadoPor;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    private Autor autor;
    private Collection<Categoria> categorias;
}
