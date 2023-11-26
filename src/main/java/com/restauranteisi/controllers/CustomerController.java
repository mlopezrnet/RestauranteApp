package com.restauranteisi.controllers;

import com.restauranteisi.models.Reservation;
import com.restauranteisi.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final ReservationService reservationService;

    @Autowired
    public CustomerController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createCustomerReservation(@RequestParam LocalDateTime reservationTime,
                                                                 @RequestParam int numberOfDiners,
                                                                 @RequestParam int tableNumber,
                                                                 @RequestParam String customerName,
                                                                 @RequestParam String customerPhone) {
        Reservation reservation = reservationService.createReservation(reservationTime, numberOfDiners, tableNumber, customerName, customerPhone);
        return ResponseEntity.ok(reservation);
    }


    @GetMapping("/reservations/{confirmationCode}")
    public ResponseEntity<Reservation> getCustomerReservation(@PathVariable String confirmationCode) {
        Optional<Reservation> reservation = reservationService.findReservationByConfirmationCode(confirmationCode);
        return reservation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/reservations/{confirmationCode}")
    public ResponseEntity<Reservation> updateCustomerReservation(@PathVariable String confirmationCode,
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

    @DeleteMapping("/reservations/{confirmationCode}")
    public ResponseEntity<Void> cancelCustomerReservation(@PathVariable String confirmationCode) {
        reservationService.cancelReservation(confirmationCode);
        return ResponseEntity.ok().build();
    }
}
