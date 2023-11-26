package com.restauranteisi.controllers;

import com.restauranteisi.models.Reservation;
import com.restauranteisi.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestParam LocalDateTime reservationTime,
                                                         @RequestParam int numberOfDiners,
                                                         @RequestParam int tableNumber,
                                                         @RequestParam String customerName,
                                                         @RequestParam String customerPhone) {
        Reservation reservation = reservationService.createReservation(reservationTime, numberOfDiners, tableNumber, customerName, customerPhone);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/{confirmationCode}")
    public ResponseEntity<Reservation> getReservation(@PathVariable String confirmationCode) {
        Optional<Reservation> reservation = reservationService.findReservationByConfirmationCode(confirmationCode);
        return reservation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("?date={date}")
    public ResponseEntity<List<Reservation>> getReservationsByDate(@PathVariable String date) {
        //parse date in dd-MM-yyyy:HH:mm format
        LocalDate localDate = LocalDate.parse(date);
        List<Reservation> reservations = reservationService.getReservationsByDate(localDate);
        return ResponseEntity.ok(reservations);
    }

    @PutMapping("/{confirmationCode}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable String confirmationCode,
                                                         @RequestParam LocalDateTime reservationTime,
                                                         @RequestParam int numberOfDiners,
                                                         @RequestParam int tableNumber,
                                                         @RequestParam String customerName,
                                                         @RequestParam String customerPhone) {
        Reservation updatedReservation = reservationService.updateReservation(confirmationCode, reservationTime, numberOfDiners, tableNumber, customerName, customerPhone);
        if (updatedReservation != null) {
            return ResponseEntity.ok(updatedReservation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{confirmationCode}")
    public ResponseEntity<Void> cancelReservation(@PathVariable String confirmationCode) {
        reservationService.cancelReservation(confirmationCode);
        return ResponseEntity.ok().build();
    }
}
