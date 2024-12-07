package ru.mooncess.datalayertesting.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeroDTO {
    private String name;
    private int level;
    private Long weapon;
}
