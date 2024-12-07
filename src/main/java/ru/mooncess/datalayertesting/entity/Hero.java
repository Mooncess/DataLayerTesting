package ru.mooncess.datalayertesting.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="hero")
@Getter
@Setter
public class Hero {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column
    private String name;
    @Column
    private int level;

    @ManyToOne
    @JoinColumn(name = "weapon_id", nullable = true)
    private Weapon weapon;
}
