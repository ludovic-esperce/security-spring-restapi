package fr.afpa.hostel.services;

import java.util.Optional;

import fr.afpa.hostel.models.Station;
import fr.afpa.hostel.repositories.StationRepository;

public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }
    
    public Iterable<Station> find() {
        return this.stationRepository.findAll();
    }

    public Optional<Station> findById(Integer id) {
        return this.stationRepository.findById(id);
    }

    public Station save(Station station) {
        return this.stationRepository.save(station);
    }

}
