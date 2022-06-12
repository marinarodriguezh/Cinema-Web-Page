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
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 256)
    private String title;

    @Column(nullable = false)
    private String synopsis;

    @Column(nullable = false)
    private String duration; 

    @Column(nullable = false)
    private String main_cast; 

    @Column(nullable = false)
    private String extra_data; 

    @Column(nullable = false)
    private String director; 

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String trailer;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private String country;
    
    @JoinColumn(name = "movie_id")
    @OneToMany(fetch = FetchType.LAZY)
    private List<Projection> projections;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String gettitle() {
        return title;
    }

    public void settitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCast() {
        return main_cast;
    }

    public void setCast(String main_cast) {
        this.main_cast = main_cast;

    }
    
    public String getExtra_data() {
        return extra_data;
    }

    public void setExtra_data(String extra_data) {
        this.extra_data = extra_data;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }
    
    public String getTrailer() {
        return trailer;
    }

    public List<Projection> getProjections() {
        return projections;
    }

    public void setProjection(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    
}