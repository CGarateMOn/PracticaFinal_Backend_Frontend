package edu.comillas.icai.gitt.pat.spring.mvc.entidades;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "pistas")
public class Pista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPista;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private String ubicacion;

    @Column(nullable = false)
    private BigDecimal precioHora;

    @Column(nullable = false)
    private Boolean activa = true;

    @Column(nullable = false, updatable = false)
    private LocalDate fechaAlta;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "pista")
    private List<Reserva> reservas = new ArrayList<>();

    protected void Pista() {
        this.fechaAlta = LocalDate.now();
    }

}
