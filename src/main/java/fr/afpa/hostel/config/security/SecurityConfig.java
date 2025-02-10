package fr.afpa.hostel.config.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Classe de configuration des mécanismes de sécurité de l'application.
 */
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // private final UserRepository userRepository;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Méthode permettant de configurer la chaîne de filtres de sécurité de spring
     * 
     * Pour plus d'informations concernant la chaîne de filtres de Spring veuillez
     * vous référer à la
     * documentation suivante :
     * https://docs.spring.io/spring-security/reference/servlet/architecture.html#servlet-securityfilterchain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Le code suivant permet de modifier la chaîne de filtres afin de :
        // - autoriser toutes les requêtes sur le endpoint 'login'
        // - autoriser les requêtes sur le endpoint 'users' uniquement si l'utilisateur
        // a le "ROLE_ADMIN" et qu'il est authentifié
        // - ajouter les filtre d'authorisation et d'authentification

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // désactivation de la vérification par défaut des attaques CSRF (pas
                                              // grave vu
                                              // qu'on va mettre en place un système de jetons)
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/authentication/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/authentication/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/stations/**").authenticated()
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.cacheControl(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowCredentials(false);
        config.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "Accept"));
        config.setExposedHeaders(Arrays.asList("Access_token", "refresh_token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
