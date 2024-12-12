package ru.mooncess.datalayertesting.hibernatepackage;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.datalayertesting.entity.Hero;
import ru.mooncess.datalayertesting.entity.HeroDTO;
import ru.mooncess.datalayertesting.entity.Weapon;

import java.util.List;

@RestController
@RequestMapping("/hib")
public class HibernateController {

    @PersistenceContext
    private EntityManager entityManager;
    @Transactional(readOnly = true)
    @GetMapping("/hero/all")
    public ResponseEntity<List<Hero>> getHeroes() {
        return ResponseEntity.ok(entityManager.createQuery("FROM Hero", Hero.class).getResultList());
    }
    @Transactional(readOnly = true)
    @GetMapping("/hero/{id}")
    public ResponseEntity<Hero> getHero(@PathVariable long id) {
        Hero hero = entityManager.find(Hero.class, id);
        if (hero != null) {
            return ResponseEntity.ok(hero);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @Transactional
    @PostMapping("/hero/create")
    public ResponseEntity<?> createHero(@RequestBody HeroDTO heroDto) {
        Weapon weapon = entityManager.find(Weapon.class, heroDto.getWeapon());
        Hero hero;
        if (weapon != null) {
            hero = new Hero();
            hero.setName(heroDto.getName());
            hero.setLevel(heroDto.getLevel());
            hero.setWeapon(weapon);
            entityManager.persist(hero);
        } else {
            return ResponseEntity.badRequest().body("The specified weapon was not found");
        }
        return ResponseEntity.ok(hero);
    }
    @Transactional
    @PutMapping("/hero/update/{id}")
    public ResponseEntity<?> updateHero(@RequestBody HeroDTO heroDto, @PathVariable long id) {
        Weapon weapon = entityManager.find(Weapon.class, heroDto.getWeapon());
        Hero updateHero = entityManager.find(Hero.class, id);

        if (weapon != null && updateHero != null) {
            updateHero.setName(heroDto.getName());
            updateHero.setLevel(heroDto.getLevel());
            updateHero.setWeapon(weapon);
            entityManager.persist(updateHero); // Обновляем героя в базе данных
        } else {
            return ResponseEntity.badRequest().body("Incorrect data is specified");
        }

        return ResponseEntity.ok(updateHero);
    }
    @Transactional
    @DeleteMapping("/hero/delete/{id}")
    public ResponseEntity<Void> deleteHero(@PathVariable long id) {
        Hero hero = entityManager.find(Hero.class, id);
        if (hero != null) {
            entityManager.refresh(hero);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    @Transactional(readOnly = true)
    @GetMapping("/weapon/all")
    public ResponseEntity<List<Weapon>> getWeapons() {
        return ResponseEntity.ok(entityManager.createQuery("FROM Weapon", Weapon.class).getResultList());
    }
    @Transactional(readOnly = true)
    @GetMapping("/weapon/{id}")
    public ResponseEntity<?> getWeapon(@PathVariable long id) {
        Weapon weapon = entityManager.find(Weapon.class, id);
        if (weapon != null) {
            return ResponseEntity.ok(weapon);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @Transactional
    @PostMapping("/weapon/create")
    public ResponseEntity<?> createWeapon(@RequestBody Weapon weapon) {
        entityManager.persist(weapon);
        return ResponseEntity.ok(weapon);
    }
    @Transactional
    @PutMapping("/weapon/update/{id}")
    public ResponseEntity<?> updateWeapon(@RequestBody Weapon weapon, @PathVariable long id) {
        Weapon updateWeapon = entityManager.find(Weapon.class, id);
        if (updateWeapon != null) {
            weapon.setId(id);
            entityManager.persist(weapon);
        } else {
            return ResponseEntity.badRequest().body("Incorrect data is specified");
        }
        return ResponseEntity.ok(weapon);
    }
    @Transactional
    @DeleteMapping("/weapon/delete/{id}")
    public ResponseEntity<Void> deleteWeapon(@PathVariable long id) {
        Weapon weapon = entityManager.find(Weapon.class, id);

        if (weapon != null) {
            entityManager.remove(weapon);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
