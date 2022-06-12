package cine.model;

import java.util.List;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface ProjectionRepository extends CrudRepository<Projection, Integer> {
    List <Projection> findByMovieId(Integer id);
    List <Projection> findByDayBetweenAndMovie(Date startDay, Date endDay, Movie movie);

    @Query("SELECT SUM(r.numSeats) FROM Reservation r WHERE r.projection=?1")
    Integer sumReservedSeats(Projection projection);

    @Transactional
    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.projection=?1")
    void deleteProjection(Projection projection);
    


}