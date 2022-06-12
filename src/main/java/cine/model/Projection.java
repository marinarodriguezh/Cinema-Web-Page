package cine.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class Projection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(optional = false)
    private Movie movie;

    @ManyToOne(optional = false)
    private Screen screen;

    @Column(nullable = false)
    private Date day; //https://docs.oracle.com/javase/7/docs/api/java/text/DateFormat.html#parse(java.lang.String)
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "projection_id")
    private List<Reservation> reservations;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public Date getDay() { 
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }


   
    
}