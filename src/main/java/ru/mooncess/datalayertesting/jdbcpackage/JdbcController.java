package ru.mooncess.datalayertesting.jdbcpackage;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mooncess.datalayertesting.entity.Hero;
import ru.mooncess.datalayertesting.entity.HeroDTO;
import ru.mooncess.datalayertesting.entity.Weapon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jdbc")
public class JdbcController {
    private final String url = "jdbc:postgresql://localhost:5438/mydb";
    private final String user = "postgres";
    private final String passwd = "postgres";

    private Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection(url, user, passwd);
    }

    @GetMapping("/hero/all")
    public ResponseEntity<List<Hero>> getHeroes() {
        String query = "SELECT h.id AS hero_id, h.name AS hero_name, h.level AS hero_level, "
                + "w.id AS weapon_id, w.name AS weapon_name, w.level AS weapon_level, "
                + "w.damage AS weapon_damage "
                + "FROM hero h LEFT JOIN weapon w ON h.weapon_id = w.id";

        try (Connection connection = getNewConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            List<Hero> heroes = new ArrayList<>();
            while (resultSet.next()) {
                Hero hero = new Hero();
                hero.setId(resultSet.getLong("hero_id"));
                hero.setName(resultSet.getString("hero_name"));
                hero.setLevel(resultSet.getInt("hero_level"));

                Weapon weapon = new Weapon();
                weapon.setId(resultSet.getLong("weapon_id"));
                weapon.setName(resultSet.getString("weapon_name"));
                weapon.setLevel(resultSet.getInt("weapon_level"));
                weapon.setDamage(resultSet.getInt("weapon_damage"));

                if (resultSet.getObject("weapon_id") != null) {
                    hero.setWeapon(weapon);
                } else {
                    hero.setWeapon(null);
                }

                heroes.add(hero);
            }
            return ResponseEntity.ok(heroes);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/hero/{id}")
    public ResponseEntity<Hero> getHero(@PathVariable long id) {
        String query = "SELECT h.id AS hero_id, h.name AS hero_name, h.level AS hero_level, "
                + "w.id AS weapon_id, w.name AS weapon_name, w.level AS weapon_level, "
                + "w.damage AS weapon_damage "
                + "FROM hero h LEFT JOIN weapon w ON h.weapon_id = w.id WHERE h.id = ?";

        try (Connection connection = getNewConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Hero hero = new Hero();
                hero.setId(resultSet.getLong("hero_id"));
                hero.setName(resultSet.getString("hero_name"));
                hero.setLevel(resultSet.getInt("hero_level"));

                Weapon weapon = new Weapon();
                weapon.setId(resultSet.getLong("weapon_id"));
                weapon.setName(resultSet.getString("weapon_name"));
                weapon.setLevel(resultSet.getInt("weapon_level"));
                weapon.setDamage(resultSet.getInt("weapon_damage"));

                if (resultSet.getObject("weapon_id") != null) {
                    hero.setWeapon(weapon);
                } else {
                    hero.setWeapon(null);
                }
                return ResponseEntity.ok(hero);
            }
            return ResponseEntity.notFound().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/hero/create")
    public ResponseEntity<String> createHero(@RequestBody HeroDTO heroDto, @RequestParam Long id) {
        String selectWeaponQuery = "SELECT * FROM weapon WHERE id = ?";
        String insertHeroQuery = "INSERT INTO hero (id, name, level, weapon_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = getNewConnection()) {
            long weaponId = heroDto.getWeapon();
            try (PreparedStatement weaponStatement = connection.prepareStatement(selectWeaponQuery)) {
                weaponStatement.setLong(1, weaponId);
                ResultSet weaponResultSet = weaponStatement.executeQuery();
                if (!weaponResultSet.next()) {
                    return ResponseEntity.badRequest().body("The specified weapon was not found");
                }
            }

            try (PreparedStatement heroStatement = connection.prepareStatement(insertHeroQuery)) {
                heroStatement.setLong(1, id);
                heroStatement.setString(2, heroDto.getName());
                heroStatement.setInt(3, heroDto.getLevel());
                heroStatement.setLong(4, weaponId);
                heroStatement.executeUpdate();
                return ResponseEntity.ok("Hero created successfully");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/hero/update/{id}")
    public ResponseEntity<String> updateHero(@RequestBody HeroDTO heroDto, @PathVariable long id) {
        String selectWeaponQuery = "SELECT * FROM weapon WHERE id = ?";
        String updateHeroQuery = "UPDATE hero SET name = ?, level = ?, weapon_id = ? WHERE id = ?";

        try (Connection connection = getNewConnection()) {
            Optional<Weapon> weapon = Optional.empty();
            try (PreparedStatement weaponStatement = connection.prepareStatement(selectWeaponQuery)) {
                weaponStatement.setLong(1, heroDto.getWeapon());
                ResultSet weaponResultSet = weaponStatement.executeQuery();
                if (!weaponResultSet.next()) {
                    return ResponseEntity.badRequest().body("The specified weapon was not found");
                }
            }

            try (PreparedStatement heroStatement = connection.prepareStatement(updateHeroQuery)) {
                heroStatement.setString(1, heroDto.getName());
                heroStatement.setInt(2, heroDto.getLevel());
                heroStatement.setLong(3, heroDto.getWeapon());
                heroStatement.setLong(4, id);
                heroStatement.executeUpdate();
                return ResponseEntity.ok("Hero updated successfully");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/hero/delete/{id}")
    public ResponseEntity<Void> deleteHero(@PathVariable long id) {
        String query = "DELETE FROM hero WHERE id = ?";

        try (Connection connection = getNewConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/weapon/all")
    public ResponseEntity<List<Weapon>> getWeapons() {
        String query = "SELECT * FROM weapon";

        try (Connection connection = getNewConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            List<Weapon> weapons = new ArrayList<>();
            while (resultSet.next()) {
                Weapon weapon = new Weapon();
                weapon.setId(resultSet.getLong("id"));
                weapon.setName(resultSet.getString("name"));
                weapon.setLevel(resultSet.getInt("level"));
                weapon.setDamage(resultSet.getInt("damage"));
                weapons.add(weapon);
            }
            return ResponseEntity.ok(weapons);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/weapon/{id}")
    public ResponseEntity<Weapon> getWeapon(@PathVariable long id) {
        String query = "SELECT * FROM weapon WHERE id = ?";

        try (Connection connection = getNewConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Weapon weapon = new Weapon();
                weapon.setId(resultSet.getLong("id"));
                weapon.setName(resultSet.getString("name"));
                weapon.setLevel(resultSet.getInt("level"));
                weapon.setDamage(resultSet.getInt("damage"));
                return ResponseEntity.ok(weapon);
            }
            return ResponseEntity.notFound().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/weapon/create")
    public ResponseEntity<String> createWeapon(@RequestBody Weapon weapon, @RequestParam Long id) {
        String insertWeaponQuery = "INSERT INTO weapon (id, name, level, damage) VALUES (?, ?, ?, ?)";

        try (Connection connection = getNewConnection()) {
            try (PreparedStatement weaponStatement = connection.prepareStatement(insertWeaponQuery)) {
                weaponStatement.setLong(1, id);
                weaponStatement.setString(2, weapon.getName());
                weaponStatement.setInt(3, weapon.getLevel());
                weaponStatement.setLong(4, weapon.getDamage());
                weaponStatement.executeUpdate();
                return ResponseEntity.ok("Weapon created successfully");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/weapon/update/{id}")
    public ResponseEntity<String> updateWeapon(@RequestBody Weapon weapon, @PathVariable long id) {
        String updateWeaponQuery = "UPDATE weapon SET name = ?, level = ?, damage = ? WHERE id = ?";

        try (Connection connection = getNewConnection()) {
            try (PreparedStatement heroStatement = connection.prepareStatement(updateWeaponQuery)) {
                heroStatement.setString(1, weapon.getName());
                heroStatement.setInt(2, weapon.getLevel());
                heroStatement.setLong(3, weapon.getDamage());
                heroStatement.setLong(4, id);
                heroStatement.executeUpdate();
                return ResponseEntity.ok("Weapon updated successfully");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/weapon/delete/{id}")
    public ResponseEntity<Void> deleteWeapon(@PathVariable long id) {
        String query = "DELETE FROM weapon WHERE id = ?";

        try (Connection connection = getNewConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
