package cine.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;


public interface MovieRepository extends CrudRepository<Movie, Integer> {
    Movie findByTitle(String title);
    List<Movie> findByGenre(String genre);

    @Query("SELECT DISTINCT genre FROM Movie")
    List<String> listgenre();

    @Transactional
    @Modifying
    @Query("DELETE FROM Projection p WHERE p.movie=?1")
    void deleteMovie(Movie movie);

}