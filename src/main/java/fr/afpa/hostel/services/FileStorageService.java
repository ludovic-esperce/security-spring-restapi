package fr.afpa.hostel.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * Classe service s'occupant du stockage de fichiers sur le disque.
 * 
 * Les classes "service" intègrent la logique métier (tout ne doit pas être dans le controleur).
 * 
 * 
 * @param properties
 */
@Service
public class FileStorageService {

    /**
     * Attribut permettant d'utiliser le système de log "slf4j" (API de journalisation Java)
     * Pour plus d'informations sur slf4j -> https://www.baeldung.com/slf4j-with-log4j2-logback
     */
    Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final Path rootLocation;

    /**
     * Constructeur de la classe prenant des propriétés en paramètre.
     * @param properties
     */
    @Autowired
    public FileStorageService(FileStorageProperties properties) {
        this.rootLocation = Paths.get(properties.getPath());
    }

    /**
     * Sauvegarde un fichier temporairement stocké sous la forme d'un objet de
     * "MultipartFile" sur le disque.
     * 
     * @param file Le fichier à stocker
     */
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(Paths.get(file.getOriginalFilename())).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException("Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    /***
     * Permet de charger tous les fichiers contenu dans le dossier racine.
     * @return
     */
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageException("Could not read file: " + filename, e);
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);

            // logging d'information s'affichant dans la console en utilisant le formatage d'une chaîne de caractères
            // pou plus d'informations sur le fonctionnement du formatage d'une chaîne en utilisant la classe "Logger"
            // de slf4j veuillez vous référer à la ressource suivante : 
            // https://examples.javacodegeeks.com/java-development/enterprise-java/slf4j/slf4j-format-string-example/
            logger.info("Chemin de sauvegarde des fichiers {}", rootLocation.toAbsolutePath().toString());
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    public Path getRootLocation() {
        return rootLocation;
    }
}