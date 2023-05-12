package fr.afpa.hostel.repositories;

import org.springframework.data.repository.CrudRepository;

import fr.afpa.hostel.models.Station;

public interface StationRepository extends CrudRepository<Station, Integer> {
}
