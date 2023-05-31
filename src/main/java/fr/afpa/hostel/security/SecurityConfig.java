package fr.afpa.hostel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Classe de configuration des mécanismes de sécurité de l'application.
 */
@Configuration
public class SecurityConfig {

    /**
     * L'annotation @Bean est PRIMORDIALE et permet de spécifier que la méthode renvoie une instance de classe qui 
     * devra être gérée par "spring context"
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Méthode permettant de configurer la chaîne de filtres de sécurité de spring
     * 
     * Pour plus d'informations concernant la chaîne de filtres de Spring veuillez vous référer à la 
     * documentation suivante : https://docs.spring.io/spring-security/reference/servlet/architecture.html#servlet-securityfilterchain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        // Le code suivant permet de modifier la chaîne de filtres afin de :
        // - autoriser toutes les requêtes sur le endpoint 'login'
        // - autoriser les requêtes sur le endpoint 'users' uniquement si l'utilisateur a le "ROLE_ADMIN" et qu'il est authentifié
        // - ajouter les filtre d'authorisation et d'authentification
        
        http.csrf(csrf -> csrf.disable()) // désactivation de la vérification par défaut des attaques CSRF (pas grave vu qu'on va mettre en place un système de jetons)
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(HttpMethod.POST, "/login/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(new CustomAuthenticationFilter(authenticationManager)) // ajout du filtre pour la phase d'authentificaiton, utilisé uniquement lors de la phase de login
                .addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class) // filtre qui va se déclencher à chaque requete juste avavant le "CustomAuthentication"
                .headers(headers -> headers.cacheControl(Customizer.withDefaults()));

        
        return http.build();
    }

    /**
	 *	Enregistrement de la calsse "BCryptPasswordEncoder" dans le contexte de l'application.
	 *
	 * L'annotation @Bean permet de le faire et garantit une seule instance en mémoire (à la manière d'un singleton)
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
        // TODO changer l'encodeur pour utiliser Pbkdf2PasswordEncoder et du sel stocké en BDD
		return new BCryptPasswordEncoder();
	}
}
