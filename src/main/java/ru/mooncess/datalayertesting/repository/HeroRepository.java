package ru.mooncess.datalayertesting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mooncess.datalayertesting.entity.Hero;

@Repository
public interface HeroRepository extends JpaRepository<Hero, Long> {
}
