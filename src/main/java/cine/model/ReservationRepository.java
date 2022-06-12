package cine.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ReservationRepository extends CrudRepository<Reservation, Integer> {

    List<Reservation> findByProjectionId(Integer projectionId);
    List<Reservation> findByUserId(Integer userId);


}