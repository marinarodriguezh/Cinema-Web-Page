package cine.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.List;
import java.text.ParseException;

import javax.validation.Valid;
import org.springframework.validation.BindingResult;


import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import cine.services.UserService;

import cine.model.Movie;
import cine.model.User;
import cine.model.Projection;
import cine.model.Reservation;
import cine.model.Screen;

import java.security.Principal;
import cine.model.UserRepository;
import cine.model.MovieRepository;
import cine.model.ProjectionRepository;
import cine.model.ReservationRepository;
import cine.model.ScreenRepository;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.Query;

@Controller
@RequestMapping(path = "/")
public class MainController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ProjectionRepository projectionRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ScreenRepository screenRepository;

    // MAIN PAGE

    @GetMapping(path = "/")
    public String mainView(Model model, Principal principal) {
        Iterable<Movie> movies;
        if (principal != null){
            User user = userRepository.findByEmail(principal.getName());
            movies = movieRepository.findAll();
            List<String> listgenre = movieRepository.listgenre();

            model.addAttribute("listgenre", listgenre);
            model.addAttribute("movies", movies);
            model.addAttribute("user", user);
            
            String role = user.getRole();
            String usernull = null;

            if (role == usernull){
                String usertype = "user";
                model.addAttribute("usertype", usertype);
            }
            else{
                String usertype = "manager";
                model.addAttribute("usertype", usertype);
            } 
            return "main_view";
        }

        movies = movieRepository.findAll();
        List<String> listgenre = movieRepository.listgenre();

        model.addAttribute("movies", movies);
        model.addAttribute("listgenre", listgenre);

        String usertype = "unknown";
        model.addAttribute("usertype",usertype);
        
        return "main_view";
    }
    
    // MAIN PAGE DEPENDING ON THE GENRE

    @GetMapping(path = "/genre/{genre}")
    public String mainViewGenre(@PathVariable String genre, Model model, Principal principal) {
        if (principal != null){
            User user = userRepository.findByEmail(principal.getName());
            List<Movie> movies = movieRepository.findByGenre(genre);
            List<String> listgenre = movieRepository.listgenre();

            model.addAttribute("movies", movies);
            model.addAttribute("user", user);
            model.addAttribute("listgenre", listgenre);
            String role = user.getRole();
            String usernull = null;

            if (role == usernull){
                String usertype = "user";
                model.addAttribute("usertype", usertype);
            }
            else{
                String usertype = "manager";
                model.addAttribute("usertype", usertype);
            } 
            return "main_view";
        }
        List<String> listgenre = movieRepository.listgenre();
        List<Movie> movies = movieRepository.findByGenre(genre);
        model.addAttribute("movies", movies);
        String usertype = "unknown";
        model.addAttribute("usertype",usertype);
        
        return "main_view";
    }

    // USER VIEW

    @GetMapping(path = "/user")
    public String viewUser(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());

        if (user==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        model.addAttribute("user", user);
        String role = user.getRole();
        String usernull = null;

        if (role == usernull){
            String usertype = "user";
            model.addAttribute("usertype", usertype);
        }
        else{
            String usertype = "manager";
            model.addAttribute("usertype", usertype);
        } 
        Iterable<Reservation> reservsmanager;
        List<Reservation> reservs = reservationRepository.findByUserId(user.getId());
        reservsmanager = reservationRepository.findAll();
        model.addAttribute("reservs", reservs);
        model.addAttribute("reservsmanager", reservsmanager);
        
        return "CustomerView";
    } 

    // MOVIE VIEW

    @GetMapping(path = "/movie/{movieId}")
    public String viewMovie(@PathVariable int movieId, Model model, Principal principal) {
        Optional<Movie> movie = movieRepository.findById(movieId);
        if (!movie.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
        }
        model.addAttribute("movie", movie.get());
        
        List<Projection> movieprojections = projectionRepository.findByMovieId(movieId);
        model.addAttribute("movieprojections", movieprojections);

        Date startDay = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDay);
        calendar.add(Calendar.DATE, 30);
        Date dayPlusOne = calendar.getTime();
        List<Projection> projectionsByDate = projectionRepository.findByDayBetweenAndMovie(startDay, dayPlusOne,movie.get());
        model.addAttribute("projectionsByDate", projectionsByDate);
        System.out.println(projectionsByDate);

        if (principal != null){
            User user = userRepository.findByEmail(principal.getName());
            model.addAttribute("user",user);
            String role = user.getRole();
            String usernull = null;

            if (role == usernull){
                String usertype = "user";
                model.addAttribute("usertype", usertype);
            }
            else{
                String usertype = "manager";
                model.addAttribute("usertype", usertype);
            } 
        }else{
            String usertype = "unknown";
            model.addAttribute("usertype", usertype);
        } 
        

        return "MovieView";

    } 

    // PROJECTION VIEW 

    @GetMapping(path = "/projection/{projectionId}")
    public String viewProjection(@PathVariable int projectionId, Model model, Principal principal) {

        User user = userRepository.findByEmail(principal.getName());
        model.addAttribute("user",user);
        Optional<Projection> projection = projectionRepository.findById(projectionId);
        Movie movie = projection.get().getMovie();
        Optional<Screen> screen = screenRepository.findById(projection.get().getScreen().getId());
        model.addAttribute("screen",screen.get());
        model.addAttribute("movie",movie);
        model.addAttribute("projection",projection.get());


        if (!projection.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projection not found");
        }
        if (projection.get().getReservations().isEmpty()){

            Integer numavailable = projection.get().getScreen().getSeatsAvailable();
            model.addAttribute("numavailable",(numavailable));

            return "ProjectionView";
        }else{ 
        List<Reservation> reservations = reservationRepository.findByProjectionId(projectionId);

        model.addAttribute("reservations",reservations);

        Integer numavailable = projection.get().getScreen().getSeatsAvailable();
        Integer seatsreserv = projectionRepository.sumReservedSeats(projection.get());
        model.addAttribute("numavailable",(numavailable-seatsreserv));

        return "ProjectionView";
        }  

    } 

    // RESERVATION VIEW

    @GetMapping(path = "/reservation/{reservationId}")
    public String viewReservation(@PathVariable int reservationId, Model model, Principal principal) {
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
        Optional<Movie> movie = movieRepository.findById(reservation.get().getProjection().getMovie().getId());
        Optional<User> user = userRepository.findById(reservation.get().getUser().getId());
        Optional<Projection> projection = projectionRepository.findById(reservation.get().getProjection().getId());
        Optional<Screen> screen = screenRepository.findById(reservation.get().getProjection().getScreen().getId());


        if (!reservation.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found");
        }
        model.addAttribute("reservation", reservation.get());
        model.addAttribute("screen", screen.get());
        model.addAttribute("movie", movie.get());
        model.addAttribute("user", user.get());
        model.addAttribute("projection", projection.get());
        return "ReservationInfo";

    } 

    // BUTTON RESERVATION FROM MOVIE PAGE TO JUMP TO RESERVATION VIEW

    @PostMapping(path = "/reservationlink")
    public String reservationlink(@Valid @RequestParam Integer projectionId, Model model,Principal principal){

        Optional<Projection> movieproj = projectionRepository.findById(projectionId);
        if (!movieproj.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projection movie not found");
        }
        model.addAttribute("movieproj", movieproj.get());
        User user = userRepository.findByEmail(principal.getName());

        model.addAttribute("user",user);
        
        Integer numavailable = movieproj.get().getScreen().getSeatsAvailable();
        Integer seatsreserv = projectionRepository.sumReservedSeats(movieproj.get());

        if (seatsreserv == null){
            seatsreserv = new Integer(0);
        } 

        model.addAttribute("numavailable",(numavailable-seatsreserv));
        return "ReservationView";
    } 

    @GetMapping(path = "/errorseats")
    public String errorseats() {
        return "errorseats";
    } 
     
    // BUTTON TO CONFIRM TICKETS FROM RESERVATION PAGE

    @PostMapping(path = "/reservationform")
    public String reservationform(@Valid @RequestParam Integer seats, Integer projectionId, Model model, Principal principal){
        User user = userRepository.findByEmail(principal.getName());

        Reservation reservation = new Reservation();
        Optional<Projection> projection = projectionRepository.findById(projectionId);
        reservation.setUser(user);
        reservation.setProjection(projection.get());
        Date day = new Date();
        reservation.setDay(day);

        Integer numavailable = projection.get().getScreen().getSeatsAvailable();
        Integer seatsreserv = projectionRepository.sumReservedSeats(projection.get());

        if (seatsreserv == null){
            seatsreserv = new Integer(0);
        } 

        model.addAttribute("numavailable",(numavailable-seatsreserv-seats));

        if (seatsreserv+seats > numavailable){
            return "redirect:/errorseats";    

        } else {

            reservation.setNumSeats(seats);
            reservationRepository.save(reservation);
        } 
       
        return "redirect:/user";    
    } 


    // DELETE PROJECTION BUTTON 

    @PostMapping(path = "/deleteproj")

    public String deleteproj(@RequestParam Integer projectionId, Model model){

        Optional<Projection> projection = projectionRepository.findById(projectionId);
        Optional<Movie> movie = movieRepository.findById(projection.get().getMovie().getId());

        projectionRepository.deleteProjection(projection.get());
        projectionRepository.deleteById(projectionId);
        
        return "redirect:/movie/" + movie.get().getId();

    } 

    // ADD MOVIE ACTION
      
    @GetMapping(path = "/add_movie")
    public String addMovie() {
        return "add_movie";
    }

    @PostMapping(path = "/add_movie")
    public String register(@Valid @RequestParam String title, String duration, String director, String synopsis, String genre, String cast, String extra_data, String country, String email, String image, String trailer, Model model) {

        Movie moviecheck = movieRepository.findByTitle(title);

        if (moviecheck == null) {
            Movie movie = new Movie();
            movie.settitle(title);
            movie.setDuration(duration);
            movie.setDirector(director);
            movie.setSynopsis(synopsis);
            movie.setCast(cast);
            movie.setCountry(country);

            movie.setGenre(genre);
            movie.setExtra_data(extra_data);
            movie.setImage(image);
            movie.setTrailer(trailer);
            model.addAttribute("movie", movie);
            movieRepository.save(movie);
            
            return "redirect:/movie/" + movie.getId();

        }else{
            String error1 = "Already exists this movie";
            model.addAttribute("error1",error1);
            return "add_movie";
        }
    }

    @GetMapping(path = "/add_screen")
    public String addScreen() {

        return "add_screen";
    }

    // ADD SCREEN ACTION

    @PostMapping(path = "/add_screen")
    public String addscreen(@Valid @RequestParam Integer num, Integer NumbSeats,Model model) {

        Screen screen = new Screen();

        if (screenRepository.findByNumber(num) == null) {
                 
            screen.setNumber(num);
            screen.setSeatsAvailable(NumbSeats);
            screenRepository.save(screen);
            
            return "redirect:/";
                 
        }else{
            String error1 = "That screen already exists";
            model.addAttribute("error1", error1);
            return "add_screen";
        } 
        
    } 

    // ADD PROJECTION ACTION

    @GetMapping(path = "/add_projection")
    public String addProjection() {

        return "add_projection";
    }

    @PostMapping(path = "/add_projection")
    public String addprojection(@Valid @RequestParam String title, Integer number, String day,Model model) {

        Projection projection = new Projection();

        Movie movie = new Movie();
        if (movieRepository.findByTitle(title) == null) {
            String error1 = "That movie is not added in the data base";
            model.addAttribute("error1", error1);
            return "add_projection";

        }if (screenRepository.findByNumber(number) == null){
            String error1 = "That screen is not added in the data base";
            model.addAttribute("error1", error1);
            return "add_projection";
        }else{ 
        
            movie = movieRepository.findByTitle(title);  
            projection.setMovie(movie);         
            Screen screen = new Screen();
            screen = screenRepository.findByNumber(number);
            projection.setScreen(screen);
            
            String pattern = "yyyy-MM-dd'T'HH:mm";
            SimpleDateFormat simpledateformat = new SimpleDateFormat(pattern);
 
            Date date; 
            try{
                date = simpledateformat.parse(day); 
            } catch (ParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INCORRECT DAY FORMAT");
            }         
            projection.setDay(date);

            model.addAttribute("movie", movie);
            projectionRepository.save(projection);
            
            return "redirect:/movie/" + movie.getId();
                 
        }
        
    }
    // DELETE MOVIE

    @PostMapping(path = "/delete_movie")
    public String deletemovie(@RequestParam Integer movieId, Model model){

        Optional<Movie> movie = movieRepository.findById(movieId);
        List<Projection> projections = projectionRepository.findByMovieId(movieId);

        
        for (Integer i=0; i < projections.size(); i++){

            projectionRepository.deleteProjection(projections.get(i));

        } 
        movieRepository.deleteMovie(movie.get());
        movieRepository.deleteById(movieId);
        
        return "redirect:/";

    } 

    // LOGIN AND REGISTER FORMS

    @GetMapping(path = "/login")
    public String loginForm() {
        return "login";
    }
    
    @GetMapping(path = "/register")
    public String registerForm() {
        User user = new User();
        return "register";
    }

    @PostMapping(path = "/register")
    public String register(@Valid @ModelAttribute("user") User user,
                        BindingResult bindingResult,
                        @RequestParam String passwordRepeat) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "redirect:register?duplicate_email";
        }
        if (userRepository.findByName(user.getName()) != null) {
            return "redirect:register?duplicate_name";
        }
        if (user.getPassword().equals(passwordRepeat)) {
            userService.register(user);
        } else {
            return "redirect:register?passwords";
        }
        return "redirect:login?registered";  

    }
 
    

}


