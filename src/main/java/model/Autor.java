package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Autor {
    private Integer id;
    private String nome;
    private String bio;
    private LocalDate dataNascimento;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

}
