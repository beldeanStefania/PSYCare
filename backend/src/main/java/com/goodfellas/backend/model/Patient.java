package com.goodfellas.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "patient")
@Data
@NoArgsConstructor
@Getter
@Setter
public class Patient
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private int age;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "psychologist_id")
    @JsonIgnore
    private Psychologist psychologist;
}