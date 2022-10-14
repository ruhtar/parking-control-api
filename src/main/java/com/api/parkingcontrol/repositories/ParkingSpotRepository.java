package com.api.parkingcontrol.repositories;

import com.api.parkingcontrol.models.ParkingSpotModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpotModel, UUID> {

    //Model = ParkingSpotModel e tipo do identificador = UUID
    //Repositório: Classe responsável por interagir com o banco de dados
    //Extendemos o JPARepository pq ele já tem vários métodos pra realizar transições com o banco de dados.
    //Caso não usasse, teria que fazer os métodos e as queries do zero.

    boolean existsByLicensePlateCar(String licensePlateCar);
    boolean existsByParkingSpotNumber(String parkingSpotNumber);
    boolean existsByApartmentAndBlock(String apartment, String block);

}
