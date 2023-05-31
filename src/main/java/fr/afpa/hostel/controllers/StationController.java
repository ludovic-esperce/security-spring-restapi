package fr.afpa.hostel.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
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
import fr.afpa.hostel.services.filestorage.FileStorageService;

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
          try {
               // on récupère l'image provenant de la classe Station (traitement automatique à partir de la requête)
               MultipartFile imageFile = station.getImageFile();
               if (!imageFile.isEmpty()) {
                    logger.info("Sauvegarde du fichier image");

                    // calcul du hash du fichier pour obtenir un nom unique
                    String storageHash = getStorageHash(imageFile).get();
                    Path rootLocation = this.fileStorageService.getRootLocation();
                    // récupération de l'extension
                    String fileExtension = mimeTypeToExtension(imageFile.getContentType());
                    // ajout de l'extension au nom du fichier
                    storageHash = storageHash + fileExtension;
                    // on retrouve le chemin de stockage de l'image
                    Path saveLocation = rootLocation.resolve(storageHash);

                    // suppression du fichier au besoin
                    Files.deleteIfExists(saveLocation);

                    // tentative de sauvegarde
                    Files.copy(imageFile.getInputStream(), saveLocation);

                    // à ce niveau il n'y a pas eu d'exception
                    // on ajoute le nom utilisé pour stocké l'image
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

     @CrossOrigin
     @GetMapping(value = "/stations/{id}/image", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
     public @ResponseBody byte[] getImage(@PathVariable int id) {
          
          Path rootLocation = this.fileStorageService.getRootLocation();
          Optional<Station> station = stationRepository.findById(id);

          if (station.isPresent()) {

               String imageName = station.get().getImageName();
               Path imagePath = rootLocation.resolve(imageName);
               try {
                    return Files.readAllBytes(imagePath);
               } catch (IOException e) {
                    logger.error(e.getMessage());
               }
     
          }

          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Impossible de trouver l'image demandée.");
     }

     /**
      * Retourne l'extension d'un fichier en fonction d'un type MIME
      * pour plus d'informations sur les types MIME : https://developer.mozilla.org/fr/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
      */
     private String mimeTypeToExtension(String mimeType) {
          return switch (mimeType) {
               case "image/jpeg" -> ".jpeg";
               case "image/png" -> ".png";
               case "image/svg" -> ".svg";
               default -> "";
          };
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

          if (!file.isEmpty()){
               try {
                    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
     
                    // La méthode digest de la classe "MessageDigest" prend en paramètre un byte[]
                    // il faut donc transformer les différents objets utilisés pour le hachage en
                    // tableau d'octets
                    // Nous utiliserons la classe "ByteArrayOutputStream" pour se faire
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(file.getOriginalFilename().getBytes());
                    outputStream.write(file.getContentType().getBytes());
                    LocalDate date = LocalDate.now();
                    outputStream.write(date.toString().getBytes());
     
                    // calcul du hash, on obtient un tableau d'octets
                    byte[] hashBytes = messageDigest.digest(outputStream.toByteArray());

                    // on retrouve une chaîne de caractères à partir d'un tableau d'octets
                    hashString = String.format("%032x", new BigInteger(1, hashBytes));
               } catch (NoSuchAlgorithmException | IOException e) {
                    logger.error(e.getMessage());
               }
          }

          return Optional.ofNullable(hashString);
     }
}
