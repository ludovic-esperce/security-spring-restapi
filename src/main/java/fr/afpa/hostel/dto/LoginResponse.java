package fr.afpa.hostel.dto;

/**
 * Classe encapsulant les informations à communiquer au client en cas de connexion étbalie avec succès.
 */
public class LoginResponse {
    /**
     * JWT a utilisé pour les échanges avec l'API
     */
    private String token;

    /**
     * Temps avant l'expiration du JWT
     */
    private long expiresIn;

    public String getToken() {
        return token;
    }

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public LoginResponse setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    
}