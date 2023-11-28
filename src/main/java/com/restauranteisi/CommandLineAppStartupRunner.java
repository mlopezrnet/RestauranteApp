package com.restauranteisi;

import com.restauranteisi.models.Reservation;
import com.restauranteisi.services.ReservationService;
import com.restauranteisi.services.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");

    private final ReservationService reservationService;
    private final StaffService staffService;
    private final Scanner scanner = new Scanner(System.in);
    private boolean isStaff = false;

    @Autowired
    public CommandLineAppStartupRunner(ReservationService reservationService, StaffService staffService) {
        this.reservationService = reservationService;
        this.staffService = staffService;
    }

    @Override
    public void run(String... args) {
        System.out.println("¡Bienvenido a Restaurante ISI!");
        while (true) {
            displayMenu();
            handleOption(scanner.nextLine());
        }
    }

    private void displayMenu() {
        System.out.println("\n" + (isStaff ? "-- MENÚ DE PERSONAL --" : "¿Qué desea hacer el día de hoy?"));
        System.out.println("1. Hacer una reservación");
        System.out.println("2. Ver una reservación existente");
        System.out.println("3. Cambiar una reservación");
        System.out.println("4. Cancelar una reservación");
        if (isStaff) {
            System.out.println("5. Ver todas las reservaciones");
            System.out.println("6. Salir");
        } else {
            System.out.println("5. Salir");
        }
    }

    private void handleOption(String option) {
        switch (option) {
            case "1":
                makeReservation();
                break;
            case "2":
                checkReservation();
                break;
            case "3":
                changeReservation();
                break;
            case "4":
                cancelReservation();
                break;
            case "s":
                staffLogin();
                break;
            case "r":
                staffRegistration();
                break;
            case "5":
                if (isStaff) {
                    viewAllReservations();
                } else {
                    exitSystem();
                }
                break;
            case "6":
                if (isStaff) {
                    exitSystem();
                } else {
                    invalidOption();
                }
                break;
            default:
                invalidOption();
                break;
        }
    }

    private void invalidOption() {
        System.out.println("Opción inválida. Por favor, inténtelo de nuevo.");
    }

    private void exitSystem() {
        System.out.println("Gracias por ser cliente de Restaurante ISI. ¡Hasta luego!");
        System.exit(0);
    }

    private void makeReservation() {
        System.out.println("Por favor, ingrese su nombre:");
        String name = scanner.nextLine();

        System.out.println("Por favor, ingrese su número de teléfono:");
        String phoneNumber = scanner.nextLine();

        try {
            System.out.println("Por favor, ingrese la fecha de la reservación (dd/MM/yyyy):");
            LocalDate reservationDate = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);

            List<LocalDateTime> availableTimes = reservationService.findAvailableReservationTimes(reservationDate);
            if (availableTimes.isEmpty()) {
                System.out.println("\nLo siento, estamos completamente reservados para ese día.");
                return;
            }

            System.out.println("Por favor, elija una hora de reserva:");
            // imprimr lista de horas disponibles de forma ordenada en orden ascendente
            for (int i = 0; i < availableTimes.size(); i++) {
                System.out.println((i + 1) + ". " + availableTimes.get(i).format(TIME_FORMATTER));
            }

            int selectedTimeIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (selectedTimeIndex < 0 || selectedTimeIndex >= availableTimes.size()) {
                System.out.println("Opción inválida. Por favor, inténtelo de nuevo.");
                return;
            }

            LocalDateTime selectedDateTime = availableTimes.get(selectedTimeIndex);

            List<Integer> availableTables = reservationService.findAvailableTables(selectedDateTime);
            if (availableTables.isEmpty()) {
                System.out.println("\nLo siento, no hay mesas disponibles para esa hora.");
                return;
            }

            System.out.println("Por favor, elija una mesa de las disponibles:");
            for (Integer availableTable : availableTables) {
                System.out.println("Mesa " + availableTable);
            }

            int selectedTableNumber = Integer.parseInt(scanner.nextLine());
            if (!availableTables.contains(selectedTableNumber)) {
                System.out.println("Mesa no válida. Por favor, inténtelo de nuevo.");
                return;
            }

            System.out.println("Por favor, ingrese el número de personas que cenarán:");
            int numberOfPeople = Integer.parseInt(scanner.nextLine());

            Reservation reservation = reservationService.createReservation(selectedDateTime, numberOfPeople,
                    selectedTableNumber, name,
                    phoneNumber);
            System.out.println("Su reservación ha sido realizada. Su código de confirmación es: "
                    + reservation.getConfirmationCode());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para el número de personas o la hora de reserva.");
        }
    }

    private void checkReservation() {
        Optional<Reservation> reservation = isStaff ? searchForReservation() : findReservationByConfirmationCode();
        reservation.ifPresentOrElse(
                this::displayReservation,
                () -> System.out.println("\nLo siento, no pudimos encontrar una reservación con esa información."));
    }

    private void displayReservation(Reservation reservation) {
        System.out.printf("Su reservación para %d personas está programada para el día %s las %s, mesa %s.\n",
                reservation.getNumberOfDiners(),
                reservation.getReservationTime().format(DATE_FORMATTER),
                reservation.getReservationTime().format(TIME_FORMATTER),
                reservation.getTableNumber());
    }

    private Optional<Reservation> searchForReservation() {
        System.out.println("Ingrese un código de confirmación o 's' para buscar por información del cliente:");
        String input = scanner.nextLine();

        if ("s".equalsIgnoreCase(input)) {
            System.out.println("Por favor, ingrese el nombre o número de teléfono del cliente:");
            String nameOrPhone = scanner.nextLine();
            return reservationService.findReservationByCustomerInfo(nameOrPhone);
        } else {
            return reservationService.findReservationByConfirmationCode(input);
        }
    }

    private Optional<Reservation> findReservationByConfirmationCode() {
        System.out.println("Por favor, ingrese su código de confirmación:");
        return reservationService.findReservationByConfirmationCode(scanner.nextLine());
    }

    private void changeReservation() {
        Optional<Reservation> reservationOptional = isStaff ? searchForReservation()
                : findReservationByConfirmationCode();
        reservationOptional.ifPresentOrElse(this::processReservationChange,
                () -> System.out.println("\nLo siento, no pudimos encontrar una reservación con esa información."));
    }

    private void processReservationChange(Reservation reservation) {
        displayReservation(reservation);
    
        System.out.println(
                "Por favor, ingrese la nueva fecha de la reservación (dd/MM/yyyy) o deje en blanco para mantener la misma fecha:");
        System.out.println("Fecha actual: " + reservation.getReservationTime().toLocalDate().format(DATE_FORMATTER));
    
        String dateInput = scanner.nextLine();
        LocalDate newDate = null;
        try {
            newDate = dateInput.isEmpty() ? reservation.getReservationTime().toLocalDate()
                    : LocalDate.parse(dateInput, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Formato de fecha inválido. Manteniendo la misma fecha.");
            newDate = reservation.getReservationTime().toLocalDate();
        }
    
        List<LocalDateTime> availableTimes = reservationService.findAvailableReservationTimes(newDate);
        if (availableTimes.isEmpty()) {
            System.out.println("\nLo siento, estamos completamente reservados para ese día.");
            return;
        }
    
        System.out.println("Por favor, elija una hora de reserva o deje en blanco para mantener la misma hora:");
        System.out.println("Hora actual: " + reservation.getReservationTime().format(TIME_FORMATTER));

        // imprimir lista de horas disponibles de forma ordenada en orden ascendente
        for (int i = 0; i < availableTimes.size(); i++) {
            System.out.println((i + 1) + ". " + availableTimes.get(i).format(TIME_FORMATTER));
        }
    
        String timeInput = scanner.nextLine();
        int selectedTimeIndex = -1;
    
        if (!timeInput.isEmpty()) {
            selectedTimeIndex = Integer.parseInt(timeInput) - 1;
            if (selectedTimeIndex < 0 || selectedTimeIndex >= availableTimes.size()) {
                System.out.println("Opción inválida. Manteniendo la misma hora.");
                selectedTimeIndex = availableTimes.indexOf(reservation.getReservationTime());
            }
        } else {
            // Mantener la misma hora si no se ingresa una nueva
            selectedTimeIndex = availableTimes.indexOf(reservation.getReservationTime());
        }
    
        LocalDateTime selectedDateTime = availableTimes.get(selectedTimeIndex);
        
        List<Integer> availableTables = reservationService.findAvailableTables(selectedDateTime);
        if (availableTables.isEmpty()) {
            System.out.println("\nLo siento, no hay mesas disponibles para esa hora. Manteniendo la misma mesa.");
            return;
        }
    
        System.out.println("Por favor, elija una mesa de las disponibles o deje en blanco para mantener la misma mesa:");
        // Mostrar la mesa actual
        System.out.println("Mesa actual: " + reservation.getTableNumber());

        // Imprimir lista de mesas disponibles de forma ordenada en orden ascendente
        for (Integer availableTable : availableTables) {
            System.out.println("Mesa " + availableTable);
        }
    
        String tableInput = scanner.nextLine();
        int selectedTableNumber = -1;
    
        if (!tableInput.isEmpty()) {
            selectedTableNumber = Integer.parseInt(tableInput);
            if (!availableTables.contains(selectedTableNumber)) {
                System.out.println("Mesa no válida. Manteniendo la misma mesa.");
                selectedTableNumber = reservation.getTableNumber();
            }
        } else {
            // Mantener la misma mesa si no se ingresa una nueva
            selectedTableNumber = reservation.getTableNumber();
        }
    
        reservationService.updateReservation(reservation.getConfirmationCode(), selectedDateTime,
                reservation.getNumberOfDiners(), selectedTableNumber, reservation.getCustomerName(),
                reservation.getCustomerPhone());
        System.out.println("Su reservación ha sido actualizada exitosamente.");
        displayReservation(reservation);
    }

    private void cancelReservation() {
        Optional<Reservation> reservationOptional = isStaff ? searchForReservation()
                : findReservationByConfirmationCode();
        reservationOptional.ifPresentOrElse(this::processReservationCancellation,
                () -> System.out.println("\nLo siento, no pudimos encontrar una reservación con esa información."));
    }

    private void processReservationCancellation(Reservation reservation) {
        displayReservation(reservation);
        System.out.println("¿Está seguro de que desea cancelar esta reservación? (sí/no)");
        String input = scanner.nextLine();
        if ("s".equalsIgnoreCase(input.trim()) || "si".equalsIgnoreCase(input.trim())) {
            reservationService.cancelReservation(reservation.getConfirmationCode());
            System.out.println("La reservación ha sido cancelada.");
        } else {
            System.out.println("La reservación no ha sido cancelada.");
        }
    }

    private void staffLogin() {
        System.out.println("Por favor, ingrese su nombre de usuario:");
        String username = scanner.nextLine();

        System.out.println("Por favor, ingrese su contraseña:");
        String password = scanner.nextLine();

        if (staffService.login(username, password)) {
            isStaff = true;
            System.out.println("Ha iniciado sesión exitosamente.");
        } else {
            System.out.println("Nombre de usuario o contraseña inválidos.");
        }
    }

    private void staffRegistration() {
        System.out.println("Por favor, ingrese su nombre de usuario:");
        String username = scanner.nextLine();

        System.out.println("Por favor, ingrese su contraseña:");
        String password = scanner.nextLine();

        if (staffService.register(username, password) != null) {
            System.out.println("Se ha registrado exitosamente.");
        } else {
            System.out.println("Registro fallido.");
        }
    }

    private void viewAllReservations() {
        System.out.println("Por favor, ingrese la fecha de las reservaciones (dd/MM/yyyy):");
        String dateInput = scanner.nextLine();
        try {
            LocalDateTime date = LocalDateTime.parse(dateInput + " 0:00", DATE_TIME_FORMATTER);
            List<Reservation> reservations = reservationService.getReservationsByDate(date.toLocalDate());
            if (reservations.isEmpty()) {
                System.out.println("No hay reservaciones para esta fecha.");
            } else {
                System.out
                        .println("reservaciones para " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ":");
                System.out.println("Cod.\tNombre\t\tTel.\t\tComensales\tMesa\tHora");
                for (Reservation reservation : reservations) {
                    System.out.printf("%s\t%s\t%s\t%d\t\t%s\t%s%n",
                            reservation.getConfirmationCode(),
                            reservation.getCustomerName(),
                            reservation.getCustomerPhone(),
                            reservation.getNumberOfDiners(),
                            reservation.getTableNumber(),
                            reservation.getReservationTime().format(TIME_FORMATTER));
                }
            }
        } catch (Exception e) {
            System.out.println("Formato de fecha inválido.");
        }
    }
}
