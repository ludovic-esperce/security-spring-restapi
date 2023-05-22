package fr.afpa.hostel.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import fr.afpa.hostel.models.Station;
import fr.afpa.hostel.repositories.StationRepository;
import fr.afpa.hostel.services.FileStorageService;

@RestController
public class StationController {

     /**
      * Attribut permettant d'utiliser le système de log "slf4j" (API de
      * journalisation Java)
      * Pour plus d'informations sur slf4j ->
      * https://www.baeldung.com/slf4j-with-log4j2-logback
      */
     Logger logger = LoggerFactory.getLogger(FileStorageService.class);

     @Autowired
     private StationRepository stationRepository;

     @Autowired
     private FileStorageService fileStorageService;

     @CrossOrigin
     @GetMapping(value = "/stations")
     @ResponseStatus(HttpStatus.OK)
     public Iterable<Station> get() {
          return stationRepository.findAll();
     }

     /// localhost:8000/stations/1
     @CrossOrigin
     @GetMapping(value = "/stations/{id}")
     @ResponseStatus(HttpStatus.OK)
     public Station get(@PathVariable(required = true) Integer id) {
          return stationRepository.findById(id).get();
     }

     @CrossOrigin
     @PostMapping(value = "/stations", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
     @ResponseStatus(HttpStatus.OK)
     public Station post(@ModelAttribute Station station) {
          // récupération du dossier de stockage (issu du fichier de configuration
          // "application.properties")

          try {

               if (!station.getMultipartFileImage().isEmpty()) {
                    logger.info("Sauvegarde du fichier image");

                    String storageHash = getStorageHash(station.getMultipartFileImage()).get();
                    Path rootLocation = this.fileStorageService.getRootLocation();
                    Path saveLocation = rootLocation.resolve(storageHash);
                    // suppression du fichier au besoin
                    Files.deleteIfExists(saveLocation);
                    // tentative de sauvegarde
                    Files.copy(station.getMultipartFileImage().getInputStream(), saveLocation);

                    station.setImageName(storageHash);
               }

               // on modifie la BDD même sans image
               return stationRepository.save(station);

          } catch (IOException e) {
               logger.error(e.getMessage());
          }

          // Si on arrive là alors erreur
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Impossible de sauvegarder la ressource.");
     }

     @GetMapping(value = "/stations/{id}/image")
     public @ResponseBody byte[] getImage(@PathVariable Long stationId) {
          Path rootLocation = this.fileStorageService.getRootLocation();

          try {
               return Files.readAllBytes(rootLocation);
          } catch (IOException e) {
               logger.error(e.getMessage());
          }

          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Impossible de trouver l'image demandée.");
     }

     /**
      * Permet de retrouver un hash qui pourra être utilisé comme nom de fichier
      * uniquement pour le stockage.
      *
      * Le hash sera calculé à partir du nom du fichier, de son type MIME
      * (https://developer.mozilla.org/fr/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types)
      * et de la date d'upload.
      *
      * @return Le hash encodé en base64
      */
     private Optional<String> getStorageHash(MultipartFile file) {
          String hashString = null;
          try {
               MessageDigest messageDigest = MessageDigest.getInstance("MD5");

               // La méthode digest de la classe "MessageDigest" prend en paramètre un byte[]
               // il faut donc transformer les différents objets utilisés pour le hachage en
               // tableau d'octets
               // Nous utiliserons la classe "ByteArrayOutputStream" pour se faire
               ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
               outputStream.write(file.getName().getBytes());
               outputStream.write(file.getContentType().getBytes());
               LocalDate date = LocalDate.now();
               outputStream.write(date.toString().getBytes());

               byte[] hashBytes = messageDigest.digest(outputStream.toByteArray());
               hashString = Base64.getEncoder().encodeToString(hashBytes);
          } catch (NoSuchAlgorithmException | IOException e) {
               logger.error(e.getMessage());
          }

          return Optional.ofNullable(hashString);
     }
}
