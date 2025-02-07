package fr.afpa.hostel.controllers;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fr.afpa.hostel.models.Role;
import fr.afpa.hostel.models.User;
import fr.afpa.hostel.repositories.UserRepository;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("users")
public class UserController {

    /**
     * Injection via "autowired" du Bean "UserREpository"
     */
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Retrouve tous les utilisateurs de la base de données.
     * 
     * @return
     */
    @GetMapping
    public ResponseEntity<Iterable<User>> findAll() {
        Iterable<User> users = userRepository.findAll();
        return ResponseEntity.ok().body(users);
    }

    /**
     * Retrouve l'utilisateur d'identifiant "id"
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> findByUsername(@PathVariable Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    /**
     * Retrouve l'utilisateur par nom
     */
    @GetMapping("/{name}")
    public ResponseEntity<String> findByUsername(@PathVariable String name) {
        System.out.println(name);
        return ResponseEntity.ok().body(name);
    }

    /**
     * Crée un nouvel utilisateur
     */
    @PostMapping
    public ResponseEntity<User> save(@RequestBody User user) {

        // on encode le mot de passe récupéré
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // une fois le mot de passe encodé on peut le sauvegardé
        User newUser = userRepository.save(user);

        // Création de l'URI indiquant l'emplacement de la nouvelle ressource
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().path("/{username}")
                .buildAndExpand(newUser.getName()).toUriString());
        return ResponseEntity.created(uri).body(user);
    }

    /**
     * Permet de récupérer tous les roles d'un utilisateur donné
     */
    @GetMapping("/{id}/roles")
    public ResponseEntity<Collection<Role>> getRoles(@PathVariable Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(user.get().getRoles());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Associe un role à un utilisateur
     */
    @PostMapping("/{id}/roles")
    public ResponseEntity<?> addRole(@PathVariable Integer id, @RequestBody Role role) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().addRole(role);
            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(user.get().getId()).toUriString());
            return ResponseEntity.created(uri).body(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
