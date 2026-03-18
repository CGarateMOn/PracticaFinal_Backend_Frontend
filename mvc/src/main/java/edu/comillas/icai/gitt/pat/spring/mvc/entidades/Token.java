package edu.comillas.icai.gitt.pat.spring.mvc.entidades;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
@Data
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) public String id;

    @OneToOne @OnDelete(action = OnDeleteAction.CASCADE) public Usuario usuario;
}
