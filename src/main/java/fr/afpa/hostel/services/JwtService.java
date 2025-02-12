package fr.afpa.hostel.services;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service permettant de gérer les JWT :
 * - création d'un JWT ;
 * - vérification de la validité de token ;
 * - extraction des "claims" de token.
 * 
 * Cette classe est une ré-implémentation de la classe proposée ici : https://medium.com/@tericcabrel/implement-jwt-authentication-in-a-spring-boot-3-application-5839e4fd8fac#4ada
 */
@Service
public class JwtService {

    /**
     * Récupération de la clef secrète utilisée pour la signature du JWT à partir du fichier ".env"
     */
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    /**
     * Récupération de la durée de validité d'un JWT à partir du fichier ".env"
     */
    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    /**
     * Extrait le nom d'utilisateur (ce qui sert à la connexion) du JWT
     * @param token Le JWT à traiter
     * @return Le "username"
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait toute les "claims" d'un JWT
     * 
     * Ceci est une "fonction générique" pouvant être paramétrée pour traiter le type de la "claim" attendu.
     * 
     * public <T> T extractClaim(...
     *         ^  ^
     *         |  + Return type
     *         + Generic type argument
     * 
     * @param <T> Le type générique qui devra être paramétré avec le type de la claim attendu
     * @param token Le JWT à traiter
     * @param claimsResolver La fonction qui sera appelée pour extraire la "claim" attendue
     * @return La "claim" attendue
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Génére un token en fonction d'un "UserDetails" et pas de "claims".
     * 
     * Il vous est conseillé de faire hériter votre entity représentant un utilisateur de la classe "UserDetails" (cf. diagramme UML associé à ce dépot).
     * Ainsi les objets de cette entity pourront être utilisées par le framework Spring Security
     * 
     * @param userDetails Objet de la classe "UserDetails" utilisé pour générer le token
     * @return Le token généré
     */
    public String generateToken(UserDetails userDetails) {

        // Par défaaut, aucune "claim" n'est ajoutée au token (HashMap vide)
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
    * Génére un token en fonction d'un "UserDetails" en ajoutant au token un ensemble de "claims"

     * @param extraClaims
     * @param userDetails
     * @return
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Renvoie la durée d'expiration associé à tout JWT
     * @return La durée d'expiration
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * Construit le token en fonction de "claims", d'un objet de la classe "UserDetails" et d'une durée de validité

     * @return Nouveau JWT
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {

        // On appelle le "builder" issu de la classe factory "Jwts"
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /** 
     * Vérifie la validité du JWT
     * 
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Indique si le JWT est expiré
     * @param token le token à traiter
     * @return Vrai is expiré
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrait la date d'expiration d'un token.
     * 
     * @param token Le token à traiter
     * @return Date d'expiration
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait toutes les "claims" d'un JWT.
     * 
     * @param token Le JWT à traiter
     * @return Un set de "claims"
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Renvoie la clef utilisée pour signée le JWT.
     * 
     * @return Une clef secrète
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}