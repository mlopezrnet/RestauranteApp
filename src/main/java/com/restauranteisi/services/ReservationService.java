package com.restauranteisi.services;

import com.restauranteisi.models.Reservation;
import com.restauranteisi.models.Restaurant;
import com.restauranteisi.repositories.ReservationRepository;
import com.restauranteisi.utils.ReservationCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation createReservation(LocalDateTime reservationTime, int numberOfDiners, int tableNumber,
            String customerName,
            String customerPhone) {
        Reservation reservation = new Reservation();
        reservation.setReservationTime(reservationTime);
        reservation.setNumberOfDiners(numberOfDiners);
        reservation.setTableNumber(tableNumber);
        reservation.setCustomerName(customerName);
        reservation.setCustomerPhone(customerPhone);
        reservation.setConfirmationCode(ReservationCodeGenerator.generateCode());
        return reservationRepository.save(reservation);
    }

    public Optional<Reservation> findReservationByConfirmationCode(String confirmationCode) {
        return Optional.ofNullable(reservationRepository.findByConfirmationCode(confirmationCode));
    }

    public Optional<Reservation> findReservationByCustomerInfo(String nameOrPhone) {
        // find all reservations and then filter by the parameter nameOrPhone
        return findAllReservations().stream()
                .filter(reservation -> reservation.getCustomerName().contains(nameOrPhone)
                        || reservation.getCustomerPhone().equals(nameOrPhone))
                .findFirst();
    }

    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getReservationsByDate(LocalDate localDate) {
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        return reservationRepository.findAllByReservationTimeBetween(startOfDay, endOfDay);
    }

    public List<Reservation> getReservationsByDateTime(LocalDateTime localDateTime) {
        return reservationRepository.findAllByReservationTime(localDateTime);
    }

    public Reservation updateReservation(String confirmationCode, LocalDateTime reservationTime, int numberOfDiners, int tableNumber,
            String customerName, String customerPhone) {
        Reservation reservation = reservationRepository.findByConfirmationCode(confirmationCode);
        if (reservation != null) {
            reservation.setReservationTime(reservationTime);
            reservation.setNumberOfDiners(numberOfDiners);
            reservation.setTableNumber(tableNumber);
            reservation.setCustomerName(customerName);
            reservation.setCustomerPhone(customerPhone);
            return reservationRepository.save(reservation);
        }
        return null;
    }

    public void cancelReservation(String confirmationCode) {
        Reservation reservation = reservationRepository.findByConfirmationCode(confirmationCode);
        if (reservation != null) {
            reservationRepository.delete(reservation);
        }
    }

    public List<Integer> findAvailableTables(LocalDateTime dateTime) {
        List<Reservation> reservations = getReservationsByDateTime(dateTime);
         //System.out.println("reservations: " + reservations);


        // Generar una lista con todas las mesas del restaurante
        List<Integer> allTables = IntStream.rangeClosed(1, Restaurant.TABLES).boxed()
                .collect(Collectors.toList());
        //System.out.println("allTables: " + allTables);

        // Filtrar las mesas disponibles basándonos en las mesas reservadas
        List<Integer> reservedTables = reservations.stream().map(Reservation::getTableNumber)
                .collect(Collectors.toList());
        allTables.removeAll(reservedTables);

        return allTables;
    
    }

    public List<LocalDateTime> findAvailableReservationTimes(LocalDate date) {
        Set<LocalDateTime> allTimes = generateAllReservationTimes(date);

        // Filtrar las horas disponibles basándonos en si hay mesas disponibles
        Set<LocalDateTime> availableTimes = allTimes.stream().filter(dateTime -> {
            List<Integer> availableTables = findAvailableTables(dateTime);
            return !availableTables.isEmpty();
        }).collect(Collectors.toSet());

        // Regresar array en orden numerico ascendente
        List<LocalDateTime> sortedTimes = new ArrayList<>(availableTimes);
        sortedTimes.sort(LocalDateTime::compareTo);
        return sortedTimes;
    }

    public Set<LocalDateTime> generateAllReservationTimes(LocalDate date) {
        Set<LocalDateTime> allTimes = new HashSet<>();
        LocalTime startTime = LocalTime.of(Restaurant.OPENING_TIME, 0);
        LocalTime endTime = LocalTime.of(Restaurant.CLOSING_TIME, 0);

        LocalDateTime currentDateTime = LocalDateTime.of(date, startTime);
        while (!currentDateTime.toLocalTime().isAfter(endTime)) {
            allTimes.add(currentDateTime);
            currentDateTime = currentDateTime.plusMinutes(60);
        }
        return allTimes;
    }

}
