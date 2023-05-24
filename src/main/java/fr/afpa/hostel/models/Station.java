package fr.afpa.hostel.models;


import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name="station")
public class Station {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "altitude")
    private int altitude;

    /**
     * Le CascadeTYpe.PERSIST permet d'indiquer à l'ORM qu'il lui faut insérer les objets de "hostels" en relation
     * lors de la création d'une station
     * 
     * Pour plus d'informations concernant les CascadeType : https://openjpa.apache.org/builds/2.4.0/apache-openjpa/docs/jpa_overview_meta_field.html#jpa_overview_meta_cascade
     */
    @OneToMany(mappedBy = "station", cascade = CascadeType.PERSIST)
    private List<Hostel> hostels;

    @Column(name = "image_name")
    private String imageName;

    /**
     * Attribut permettant de récupérer une image à partir d'une requête http "multipart/data".
     * Attention, le contenu du fichier est stocké de manière temporaire (en mémoire ou sur le disque)
     */
    @JsonIgnore
    @Transient
    private MultipartFile imageFile;

    public Station() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public List<Hostel> getHostels() {
        return hostels;
    }

    public void setHostels(List<Hostel> hostels) {
        this.hostels = hostels;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
