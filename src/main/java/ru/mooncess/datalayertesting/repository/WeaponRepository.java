package ru.mooncess.datalayertesting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mooncess.datalayertesting.entity.Weapon;

@Repository
public interface WeaponRepository extends JpaRepository<Weapon, Long> {
}