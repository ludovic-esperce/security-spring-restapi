package fr.afpa.hostel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import fr.afpa.hostel.controllers.StationController;

/**
 * Classe contenant les tests de l'application.
 * 
 * L'annotation @SpringBootTest demande à Spring Boot de regarder pour une
 * classe de configuration.
 */
@SpringBootTest
class HostelApplicationTests {

	@Autowired
	private StationController stationController;

	@Test
	void contextLoads() {
		assertThat(stationController).isNotNull();
	}

}
