package fr.afpa.hostel.controllers;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fr.afpa.hostel.dto.LoginResponse;
import fr.afpa.hostel.dto.LoginUserDto;
import fr.afpa.hostel.models.User;
import fr.afpa.hostel.services.AuthenticationService;
import fr.afpa.hostel.services.JwtService;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    /**
     * Injection par paramètre de constructeur des 2 beans : jwtService et
     * authenticationService
     * 
     * @param jwtService            Gère les opérations sur les JWT
     * @param authenticationService Gère les opérations
     */
    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    /**
     * Traite les requête HTTP de création de compte.
     * 
     * @param userRegistration Nouvel utilisateur a créer, les données proviennent
     *                         de la désérialisation du Json par Jackson
     * @return Informations du nouvel utilisateur.
     */
    @PostMapping(value = "/register")
    public ResponseEntity<User> register(@RequestBody User userRegistration) {

        // appel du service de création d'un utilisateur
        Optional<User> user = authenticationService.register(userRegistration);
        if (user.isPresent()) {
            // Création de l'URI indiquant l'emplacement de la nouvelle ressource
            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().path("/{username}")
                    .buildAndExpand(user.get().getId()).toUriString());

            return ResponseEntity.created(uri).body(user.get());
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Traite les requête de connexion d'un utilisateur
     * 
     * @param userDto DTO permettant de récupérer les informations de connexion
     * @return La réponse de connexion
     */
    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserDto userDto) {

        // tentative de connexion via le service approprié
        User authenticatedUser = authenticationService.login(userDto);

        // création du JWT
        String jwtToken = jwtService.generateToken(authenticatedUser);

        // création de la réponse client
        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken)
                .setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

}
