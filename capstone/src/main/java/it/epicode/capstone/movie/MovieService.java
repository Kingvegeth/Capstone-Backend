package it.epicode.capstone.movie;

import it.epicode.capstone.people.Person;
import it.epicode.capstone.company.Company;
import it.epicode.capstone.people.PersonRepository;
import it.epicode.capstone.company.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public List<MovieResponse> findAll() {
        return movieRepository.findAll().stream().map(this::movieToResponse).collect(Collectors.toList());
    }

    public Optional<MovieResponse> findById(Long id) {
        return movieRepository.findById(id).map(this::movieToResponse);
    }

    public MovieResponse createMovie(MovieRequest request) {
        Movie movie = requestToMovie(request);
        movieRepository.save(movie);
        return movieToResponse(movie);
    }

    public Optional<MovieResponse> updateMovie(Long id, MovieRequest request) {
        return movieRepository.findById(id).map(movie -> {
            movie.setTitle(request.getTitle());
            movie.setYear(request.getYear());
            movie.setDuration(request.getDuration());
            movie.setCast(getPersonsByIds(request.getCastIds()));
            movie.setDirectors(getPersonsByIds(request.getDirectorIds()));
            movie.setScreenwriters(getPersonsByIds(request.getScreenwriterIds()));
            movie.setProducers(getCompaniesByIds(request.getProducerIds()));
            movie.setDistributor(getCompanyById(request.getDistributorId()));
            movieRepository.save(movie);
            return movieToResponse(movie);
        });
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    private Movie requestToMovie(MovieRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setYear(request.getYear());
        movie.setDuration(request.getDuration());
        movie.setCast(getPersonsByIds(request.getCastIds()));
        movie.setDirectors(getPersonsByIds(request.getDirectorIds()));
        movie.setScreenwriters(getPersonsByIds(request.getScreenwriterIds()));
        movie.setProducers(getCompaniesByIds(request.getProducerIds()));
        movie.setDistributor(getCompanyById(request.getDistributorId()));
        return movie;
    }

    private MovieResponse movieToResponse(Movie movie) {
        MovieResponse response = new MovieResponse();
        response.setId(movie.getId());
        response.setTitle(movie.getTitle());
        response.setYear(movie.getYear());
        response.setDuration(movie.getDuration());
        response.setCast(movie.getCast());
        response.setDirectors(movie.getDirectors());
        response.setScreenwriters(movie.getScreenwriters());
        response.setProducers(movie.getProducers());
        response.setDistributor(movie.getDistributor());
        return response;
    }

    private List<Person> getPersonsByIds(List<Long> ids) {
        return ids.stream().map(id -> personRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("Person not found with id: " + id)))
                .collect(Collectors.toList());
    }

    private List<Company> getCompaniesByIds(List<Long> ids) {
        return ids.stream().map(id -> companyRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("Company not found with id: " + id)))
                .collect(Collectors.toList());
    }

    private Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Company not found with id: " + id));
    }
}
