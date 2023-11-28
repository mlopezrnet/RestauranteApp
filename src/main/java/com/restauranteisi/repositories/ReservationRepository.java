package com.restauranteisi.repositories;

import com.restauranteisi.models.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, String> {
    List<Reservation> findAllByReservationTime(LocalDateTime reservationTime);
    List<Reservation> findAllByReservationTimeBetween(LocalDateTime start, LocalDateTime end);
    Reservation findByConfirmationCode(String confirmationCode);
}
