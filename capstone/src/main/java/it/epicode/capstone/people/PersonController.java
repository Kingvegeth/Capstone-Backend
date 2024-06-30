package it.epicode.capstone.people;

import it.epicode.capstone.movie.MovieResponse;
import it.epicode.capstone.movie.MovieService;
import it.epicode.capstone.movie.MovieSimpleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/people")
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private MovieService movieService;

    @GetMapping
    public List<PersonResponse> getAllPeople() {
        return personService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> getPersonById(@PathVariable Long id) {
        Optional<PersonResponse> person = personService.findById(id);
        return person.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PersonResponse> createPerson(@RequestBody PersonRequest request) {
        PersonResponse personResponse = personService.createPerson(request);
        return ResponseEntity.ok(personResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResponse> updatePerson(@PathVariable Long id, @RequestBody PersonRequest request) {
        Optional<PersonResponse> updatedPerson = personService.updatePerson(id, request);
        return updatedPerson.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/movies/cast")
    public ResponseEntity<List<MovieSimpleResponse>> getMoviesAsCast(@PathVariable Long id) {
        List<MovieSimpleResponse> movies = movieService.findMoviesByCastId(id);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}/movies/director")
    public ResponseEntity<List<MovieSimpleResponse>> getMoviesAsDirector(@PathVariable Long id) {
        List<MovieSimpleResponse> movies = movieService.findMoviesByDirectorId(id);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}/movies/screenwriter")
    public ResponseEntity<List<MovieSimpleResponse>> getMoviesAsScreenwriter(@PathVariable Long id) {
        List<MovieSimpleResponse> movies = movieService.findMoviesByScreenwriterId(id);
        return ResponseEntity.ok(movies);
    }
}

