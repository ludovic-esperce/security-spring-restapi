package fr.afpa.hostel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm;

import fr.afpa.hostel.repositories.UserRepository;

@Configuration
public class ApplicationConfiguration {

    private final String SECRET_PASSWORD_PEPPER = "secretmagicpepper";
    
    private final UserRepository userRepository;

    public ApplicationConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        // DaoAUthenticationProvider va retrouver un objet de la classe "UserDetail" à partir d'un "UserDetailsService"
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

        
    /**
     *
     * L'annotation @Bean permet de le faire et garantit une seule instance en
     * mémoire (à la manière d'un singleton)
     */
    @Bean
    PasswordEncoder passwordEncoder() {

        // On suit une des recommandations OWasp
        // - PBKDF2 with a work factor of 600,000 or more and set with an internal hash
        // function of HMAC-SHA-256é
        return new Pbkdf2PasswordEncoder(SECRET_PASSWORD_PEPPER, 16, 600000,
                SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
    }
}