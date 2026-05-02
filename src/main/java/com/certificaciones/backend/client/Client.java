package com.certificaciones.backend.client;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre comercial es obligatorio")
    private String businessName;

    @NotBlank(message = "La razón social es obligatoria")
    private String legalName;

    @NotBlank(message = "El NIT o identificación es obligatorio")
    @Column(unique = true, nullable = false)
    private String taxId;

    @Email(message = "El correo no tiene un formato válido")
    @NotBlank(message = "El correo es obligatorio")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    private String phone;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    private LocalDateTime createdAt = LocalDateTime.now();
}