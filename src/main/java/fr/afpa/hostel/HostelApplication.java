package fr.afpa.hostel;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
//@EnableConfigurationProperties(FileStorageProperties.class) // non obligatoire, si la clsse de propriété est annotée avec @Configuration
@EnableTransactionManagement
public class HostelApplication {

	@Autowired
	DataSourceProperties dataSourceProperties;

    Logger logger = LoggerFactory.getLogger(HostelApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(HostelApplication.class, args);
	}

	@PostConstruct
    public void init() {
        logger.info(String.format("URL de connexion : %s", dataSourceProperties.determineUrl()));
		logger.info(String.format("Nom de la BDD : %s", dataSourceProperties.determineDatabaseName()));
		logger.info(String.format("Nom de l'utlisateur : %s", dataSourceProperties.determineUsername()));
    }

}
