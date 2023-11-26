package com.restauranteisi.controllers;

import com.restauranteisi.models.Reservation;
import com.restauranteisi.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    private final ReservationService reservationService;

    @Autowired
    public StaffController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createStaffReservation(@RequestParam LocalDateTime reservationTime,
                                                              @RequestParam int numberOfDiners,
                                                              @RequestParam int tableNumber,
                                                              @RequestParam String customerName,
                                                              @RequestParam String customerEmail,
                                                              @RequestParam String customerPhone) {
        Reservation reservation = reservationService.createReservation(reservationTime, numberOfDiners, tableNumber, customerName, customerPhone);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/reservations/{date}")
    public ResponseEntity<List<Reservation>> getReservationsByDate(@PathVariable String date) {
        //parse date in dd-MM-yyyy:HH:mm format
        LocalDate localDate = LocalDate.parse(date);
        List<Reservation> reservations = reservationService.getReservationsByDate(localDate);
        return ResponseEntity.ok(reservations);
    }

    @DeleteMapping("/reservations/{confirmationCode}")
    public ResponseEntity<Void> cancelStaffReservation(@PathVariable String confirmationCode) {
        reservationService.cancelReservation(confirmationCode);
        return ResponseEntity.ok().build();
    }
}
