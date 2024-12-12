package ru.mooncess.datalayertesting.springdatajpapackage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.datalayertesting.entity.Hero;
import ru.mooncess.datalayertesting.entity.HeroDTO;
import ru.mooncess.datalayertesting.entity.Weapon;
import ru.mooncess.datalayertesting.repository.HeroRepository;
import ru.mooncess.datalayertesting.repository.WeaponRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jpa")
@RequiredArgsConstructor
public class JPAController {
    private final HeroRepository heroRepository;
    private final WeaponRepository weaponRepository;

    @GetMapping("/hero/all")
    public ResponseEntity<List<Hero>> getHeroes() {
        return ResponseEntity.ok(heroRepository.findAll());
    }
    @GetMapping("/hero/{id}")
    public ResponseEntity<?> getHero(@PathVariable long id) {
        Optional<Hero> hero = heroRepository.findById(id);
        if (hero.isPresent()) {
            return ResponseEntity.ok(hero);
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/hero/create")
    public ResponseEntity<?> createHero(@RequestBody HeroDTO heroDto) {
        Optional<Weapon> weapon = weaponRepository.findById(heroDto.getWeapon());
        if (weapon.isPresent()) {
            Hero hero = new Hero();
            hero.setName(heroDto.getName());
            hero.setLevel(heroDto.getLevel());
            hero.setWeapon(weapon.get());
            return ResponseEntity.ok(heroRepository.save(hero));
        }
        return ResponseEntity.badRequest().body("The specified weapon was not found");
    }

    @PutMapping("/hero/update/{id}")
    public ResponseEntity<?> updateHero(@RequestBody HeroDTO heroDto, @PathVariable long id) {
        Optional<Weapon> weapon = weaponRepository.findById(heroDto.getWeapon());
        Optional<Hero> hero = heroRepository.findById(id);
        if (weapon.isPresent() && hero.isPresent()) {
            Hero updateHero = hero.get();
            updateHero.setName(heroDto.getName());
            updateHero.setLevel(heroDto.getLevel());
            updateHero.setWeapon(weapon.get());
            return ResponseEntity.ok(heroRepository.save(updateHero));
        }
        return ResponseEntity.badRequest().body("Incorrect data is specified");
    }

    @DeleteMapping("/hero/delete/{id}")
    public ResponseEntity<Void> deleteHero(@PathVariable long id) {
        Optional<Hero> hero = heroRepository.findById(id);
        if (hero.isPresent()) {
            heroRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/weapon/all")
    public ResponseEntity<List<Weapon>> getWeapons() {
        return ResponseEntity.ok(weaponRepository.findAll());
    }

    @GetMapping("/weapon/{id}")
    public ResponseEntity<?> getWeapon(@PathVariable long id) {
        Optional<Weapon> weapon = weaponRepository.findById(id);
        if (weapon.isPresent()) {
            return ResponseEntity.ok(weapon);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/weapon/create")
    public ResponseEntity<?> createWeapon(@RequestBody Weapon weapon) {
        return ResponseEntity.ok(weaponRepository.save(weapon));
    }

    @PutMapping("/weapon/update/{id}")
    public ResponseEntity<?> updateWeapon(@RequestBody Weapon weapon, @PathVariable long id) {
        Optional<Weapon> existingWeapon = weaponRepository.findById(id);
        if (existingWeapon.isPresent()) {
            Weapon updateWeapon = existingWeapon.get();
            updateWeapon.setName(weapon.getName());
            updateWeapon.setLevel(weapon.getLevel());
            updateWeapon.setDamage(weapon.getDamage());
            return ResponseEntity.ok(weaponRepository.save(updateWeapon));
        }
        return ResponseEntity.badRequest().body("Incorrect data is specified");
    }

    @DeleteMapping("/weapon/delete/{id}")
    public ResponseEntity<Void> deleteWeapon(@PathVariable long id) {
        Optional<Weapon> weapon = weaponRepository.findById(id);
        if (weapon.isPresent()) {
            weaponRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
