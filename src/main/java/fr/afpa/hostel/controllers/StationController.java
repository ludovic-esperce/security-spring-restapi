package fr.afpa.hostel.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;

import fr.afpa.hostel.models.Station;
import fr.afpa.hostel.repositories.StationRepository;

@RestController
public class StationController {
    
    @Autowired
    private StationRepository stationRepository;

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
   @PostMapping(value = "/stations")
   @ResponseStatus(HttpStatus.OK)
   public Station post(@RequestBody Station station) {
        return stationRepository.save(station);
   }
}
