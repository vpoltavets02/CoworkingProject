package coworking.project.services;

import coworking.project.exceptions.ReservationNotFoundException;
import coworking.project.models.Rating;
import coworking.project.models.Reservation;
import coworking.project.repositories.ReservationsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {
    private final RatingService ratingService;
    private final ReservationsRepository reservationsRepository;

    public ReservationService(RatingService ratingService, ReservationsRepository reservationsRepository) {
        this.ratingService = ratingService;
        this.reservationsRepository = reservationsRepository;
    }

    public Reservation findById(Long id) {
        return reservationsRepository.findById(id).orElseThrow(ReservationNotFoundException::new);
    }

    public List<Reservation> findPayedReservations() {
        return reservationsRepository.findPayedReservationsToConfirm();
    }

    public List<Reservation> findAllConfirmedReservations() {
        return reservationsRepository.findAllConfirmedReservations();
    }

    public List<Reservation> findAllByWorkPlaceAndRentDay(Long workPlaceId, LocalDate date) {
        return reservationsRepository.findAllByRentDay(workPlaceId, date);
    }

    @Transactional
    public void payReservation(Long id) {
        Reservation reservation = reservationsRepository.findById(id).orElseThrow(ReservationNotFoundException::new);
        reservation.setIsPayed(true);
        Rating rating = reservation.getWorkPlace().getRating();
        rating.setNumberOfUsing(rating.getNumberOfUsing() + 1);
        ratingService.update(rating);
        reservationsRepository.save(reservation);
    }

    @Transactional
    public void save(Reservation reservation) {
        reservationsRepository.save(reservation);
    }
}