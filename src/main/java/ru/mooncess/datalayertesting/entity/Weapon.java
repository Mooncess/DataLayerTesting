package ru.mooncess.datalayertesting.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="weapon")
@Getter
@Setter
public class Weapon {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column
    private String name;
    @Column
    private int level;
    @Column
    private int damage;
}