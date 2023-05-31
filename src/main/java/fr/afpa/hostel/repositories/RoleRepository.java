package fr.afpa.hostel.repositories;

import org.springframework.data.repository.CrudRepository;

import fr.afpa.hostel.models.Role;

public interface RoleRepository extends CrudRepository<Role, Integer> {

    /**
     * Permet de retrouver un utilisateur suivant son nom.
     * 
     * Si une fonction commence par "findBy" et est suivi d'un nom de colonne il n'est pas nécessaire
     * d'écrire une requête JPQL (pour information  : https://gayerie.dev/epsi-b3-orm/javaee_orm/jpa_queries.html)
     *  
     * @param name Le role recherché
     * @return Le role retrouvé
     */
    Role findByName(String name);
 }
 
