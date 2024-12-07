package ru.mooncess.datalayertesting.hibernatepackage;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.datalayertesting.entity.Hero;
import ru.mooncess.datalayertesting.entity.HeroDTO;
import ru.mooncess.datalayertesting.entity.Weapon;

import java.util.List;

@RestController
@RequestMapping("/hib")
public class HibernateController {

    private final SessionFactory factory;

    public HibernateController() {
        this.factory = new org.hibernate.cfg.Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Hero.class)
                .addAnnotatedClass(Weapon.class)
                .buildSessionFactory();
    }


    @GetMapping("/hero/all")
    public ResponseEntity<List<Hero>> getHeroes() {
        Session session = factory.getCurrentSession();
        List<Hero> heroes = null;
        try {
            session.beginTransaction();
            heroes = session.createQuery("FROM Hero", Hero.class).getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        } finally {
            session.close();
        }
        return ResponseEntity.ok(heroes);
    }

    @GetMapping("/hero/{id}")
    public ResponseEntity<Hero> getHero(@PathVariable long id) {
        Session session = factory.getCurrentSession();
        Hero hero = null;

        try {
            session.beginTransaction();

            hero = session.get(Hero.class, id);

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } finally {
            session.close();
        }

        if (hero != null) {
            return ResponseEntity.ok(hero);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/hero/create")
    public ResponseEntity<?> createHero(@RequestBody HeroDTO heroDto) {
        Session session = factory.getCurrentSession();
        Hero hero = null;

        try {
            session.beginTransaction();

            // Получаем Weapon с помощью Hibernate
            Weapon weapon = session.get(Weapon.class, heroDto.getWeapon());

            if (weapon != null) {
                hero = new Hero();
                hero.setName(heroDto.getName());
                hero.setLevel(heroDto.getLevel());
                hero.setWeapon(weapon);
                session.persist(hero); // Сохраняем героя в базе данных
                session.getTransaction().commit();
            } else {
                return ResponseEntity.badRequest().body("The specified weapon was not found");
            }
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } finally {
            session.close();
        }

        return ResponseEntity.ok(hero);
    }

    @PutMapping("/hero/update/{id}")
    public ResponseEntity<?> updateHero(@RequestBody HeroDTO heroDto, @PathVariable long id) {
        Session session = factory.getCurrentSession();
        Hero updateHero = null;

        try {
            session.beginTransaction();

            // Получаем существующего героя и оружие
            Weapon weapon = session.get(Weapon.class, heroDto.getWeapon());
            updateHero = session.get(Hero.class, id);

            if (weapon != null && updateHero != null) {
                updateHero.setName(heroDto.getName());
                updateHero.setLevel(heroDto.getLevel());
                updateHero.setWeapon(weapon);
                session.update(updateHero); // Обновляем героя в базе данных
                session.getTransaction().commit();
            } else {
                return ResponseEntity.badRequest().body("Incorrect data is specified");
            }
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } finally {
            session.close();
        }

        return ResponseEntity.ok(updateHero);
    }

    @DeleteMapping("/hero/delete/{id}")
    public ResponseEntity<Void> deleteHero(@PathVariable long id) {
        Session session = factory.getCurrentSession();

        try {
            session.beginTransaction();
            Hero hero = session.get(Hero.class, id);

            if (hero != null) {
                session.delete(hero); // Удаляем героя из базы данных
                session.getTransaction().commit();
                return ResponseEntity.noContent().build(); // Возвращаем 204 No Content
            }
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            session.close();
        }

        return ResponseEntity.notFound().build(); // Возвращаем 404 Not Found, если герой не найден
    }

    @GetMapping("/weapon/all")
    public ResponseEntity<List<Weapon>> getWeapons() {
        Session session = factory.getCurrentSession();
        List<Weapon> weapons = null;
        try {
            session.beginTransaction();
            weapons = session.createQuery("FROM Weapon", Weapon.class).getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        } finally {
            session.close();
        }
        return ResponseEntity.ok(weapons);
    }

    @GetMapping("/weapon/{id}")
    public ResponseEntity<?> getWeapon(@PathVariable long id) {
        Session session = factory.getCurrentSession();
        Weapon weapon = null;

        try {
            session.beginTransaction();

            weapon = session.get(Weapon.class, id);

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } finally {
            session.close();
        }

        if (weapon != null) {
            return ResponseEntity.ok(weapon);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/weapon/create")
    public ResponseEntity<?> createWeapon(@RequestBody Weapon weapon) {
        Session session = factory.getCurrentSession();
        try {
            session.beginTransaction();

            session.persist(weapon); // Сохраняем героя в базе данных

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } finally {
            session.close();
        }

        return ResponseEntity.ok(weapon);
    }

    @PutMapping("/weapon/update/{id}")
    public ResponseEntity<?> updateWeapon(@RequestBody Weapon weapon, @PathVariable long id) {
        Session session = factory.getCurrentSession();
        Weapon updateWeapon = null;

        try {
            session.beginTransaction();

            updateWeapon = session.get(Weapon.class, id);

            if (weapon != null) {
                weapon.setId(id);
                session.update(weapon); // Обновляем героя в базе данных
                session.getTransaction().commit();
            } else {
                return ResponseEntity.badRequest().body("Incorrect data is specified");
            }
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } finally {
            session.close();
        }

        return ResponseEntity.ok(weapon);
    }

    @DeleteMapping("/weapon/delete/{id}")
    public ResponseEntity<Void> deleteWeapon(@PathVariable long id) {
        Session session = factory.getCurrentSession();

        try {
            session.beginTransaction();
            Weapon weapon = session.get(Weapon.class, id);

            if (weapon != null) {
                session.delete(weapon);
                session.getTransaction().commit();
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            session.close();
        }

        return ResponseEntity.notFound().build();
    }
}
