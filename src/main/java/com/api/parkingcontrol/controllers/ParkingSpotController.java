package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/parking-spot")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ParkingSpotController {

    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @GetMapping
    public ResponseEntity<List<ParkingSpotModel>> getAllParkingSpots(){
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
    }

    @GetMapping("/{id}") //URI
    public ResponseEntity<Object> getOneParkingSpot (@PathVariable(value = "id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (parkingSpotModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
    }


    @PostMapping //Método POST
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto){
        //VERIFICAÇÕES
        if(parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use.");
        }

        if(parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot Number is already in use.");
        }

        if(parkingSpotService.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already registered in this apartment/block");
        }

        //ResponseEntity é usado para gerar uma resposta a essa criação. Tanto um status quando um corpo de response
        ParkingSpotModel parkingSpotModel = new ParkingSpotModel();
        //O DTO é usado para fazer a validação dos atributos, mas se salva no banco de dados na verdade o Model.
        //Então se faz a conversão de DTO para Model antes de salvar no banco de dados
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        //No DTO, não fiz uma validação de data para o usuário, a data é setada através desse método
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") UUID id,
                                                    @Valid @RequestBody ParkingSpotDto parkingSpotDto){
        Optional<ParkingSpotModel> optionalParkingSpotModel= parkingSpotService.findById(id);
        if(optionalParkingSpotModel.isPresent()){
            ParkingSpotModel parkingSpotModel = new ParkingSpotModel();
            BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
            parkingSpotModel.setId(optionalParkingSpotModel.get().getId());
            parkingSpotModel.setRegistrationDate(optionalParkingSpotModel.get().getRegistrationDate());
            return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id") UUID id){
        Optional<ParkingSpotModel> optionalParkingSpotModel = parkingSpotService.findById(id);
        if(optionalParkingSpotModel.isPresent()){
            parkingSpotService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
    }


}
