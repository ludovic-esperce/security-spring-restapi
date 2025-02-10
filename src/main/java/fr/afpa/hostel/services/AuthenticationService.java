package fr.afpa.hostel.services;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fr.afpa.hostel.dto.LoginUserDto;
import fr.afpa.hostel.models.User;
import fr.afpa.hostel.repositories.UserRepository;

/**
 * Service gérant la connexion d'un utilisateur
 */
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Construction utilisé pour l'injection de dépendances.
     * 
     * @param userRepository Permet d'effectuer la persistance d'un nouvel utilisateur en base de données
     * @param authenticationManager Utilisé pour vérifier qu'un utilisateur peut se connecter
     * @param passwordEncoder Utilisé pour encoder les mots de passe lors de la création d'un nouvel utilisateur.
     */
    public AuthenticationService(
        UserRepository userRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Permet d'enregistrer un nouvel utilisateur.
     * @param user l'utilisateur à créer en base de données
     */
    public Optional<User> register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User newUser = null;
        try {
            newUser = userRepository.save(user);
        } catch (DataIntegrityViolationException constraintViolation) {
            System.err.println(constraintViolation.getMessage());
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return Optional.of(newUser);
    }

    /**
     * Méthode utilisé pour tenter d'authentifier un utilisateur.
     * Dans le cas d'un utilisateur inexistant la fonction lance une exception NoSuchElementException
     * 
     * @param input
     * @return Une instance de l'utilisateur authentifié
     */
    public User login(LoginUserDto input) {
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail()).orElseThrow();
    }
}