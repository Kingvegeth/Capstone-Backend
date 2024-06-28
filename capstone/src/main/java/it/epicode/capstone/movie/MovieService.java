package it.epicode.capstone.movie;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.epicode.capstone.company.CompanyResponse;
import it.epicode.capstone.company.CompanyService;
import it.epicode.capstone.exceptions.NotFoundException;
import it.epicode.capstone.people.Person;
import it.epicode.capstone.company.Company;
import it.epicode.capstone.people.PersonRepository;
import it.epicode.capstone.company.CompanyRepository;
import it.epicode.capstone.people.PersonResponse;
import it.epicode.capstone.people.PersonService;
import it.epicode.capstone.review.Review;
import it.epicode.capstone.review.ReviewService;
import it.epicode.capstone.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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

    @Autowired
    private PersonService personService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ReviewService reviewService;

    @Value("${CLOUDINARY_URL}")
    private String cloudinaryUrl;


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
            movie.setDescription(request.getDescription());
            movie.setGenre(request.getGenre());
            movie.setPosterImg(request.getPosterImg());

            // Controlla se gli elenchi sono null e imposta i campi solo se non sono nulli
            if (request.getCastIds() != null) {
                movie.setCast(getPersonsByIds(request.getCastIds()));
            }
            if (request.getDirectorIds() != null) {
                movie.setDirectors(getPersonsByIds(request.getDirectorIds()));
            }
            if (request.getScreenwriterIds() != null) {
                movie.setScreenwriters(getPersonsByIds(request.getScreenwriterIds()));
            }
            if (request.getProducerIds() != null) {
                movie.setProducers(getCompaniesByIds(request.getProducerIds()));
            }
            if (request.getDistributorId() != null) {
                movie.setDistributor(getCompanyById(request.getDistributorId()));
            }
            if (request.getReviewIds() != null) {
                movie.setReviews(getReviewsByIds(request.getReviewIds()));
            }

            movieRepository.save(movie);
            return movieToResponse(movie);
        });
    }

    public Optional<MovieResponse> patchMovie(Long id, MovieRequest request) {
        return movieRepository.findById(id).map(movie -> {
            if (request.getTitle() != null) {
                movie.setTitle(request.getTitle());
            }
            if (request.getYear() != 0) {
                movie.setYear(request.getYear());
            }
            if (request.getDuration() != 0) {
                movie.setDuration(request.getDuration());
            }
            if (request.getDescription() != null) {
                movie.setDescription(request.getDescription());
            }
            if (request.getGenre() != null) {
                movie.setGenre(request.getGenre());
            }
            if (request.getPosterImg() != null) {
                movie.setPosterImg(request.getPosterImg());
            }
            if (request.getCastIds() != null) {
                movie.setCast(getPersonsByIds(request.getCastIds()));
            }
            if (request.getDirectorIds() != null) {
                movie.setDirectors(getPersonsByIds(request.getDirectorIds()));
            }
            if (request.getScreenwriterIds() != null) {
                movie.setScreenwriters(getPersonsByIds(request.getScreenwriterIds()));
            }
            if (request.getProducerIds() != null) {
                movie.setProducers(getCompaniesByIds(request.getProducerIds()));
            }
            if (request.getDistributorId() != null) {
                movie.setDistributor(getCompanyById(request.getDistributorId()));
            }
            if (request.getReviewIds() != null) {
                movie.setReviews(getReviewsByIds(request.getReviewIds()));
            }
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
        movie.setDescription(request.getDescription());
        movie.setGenre(request.getGenre());
        movie.setPosterImg(request.getPosterImg());
        movie.setCast(getPersonsByIds(Optional.ofNullable(request.getCastIds()).orElse(List.of())));
        movie.setDirectors(getPersonsByIds(Optional.ofNullable(request.getDirectorIds()).orElse(List.of())));
        movie.setScreenwriters(getPersonsByIds(Optional.ofNullable(request.getScreenwriterIds()).orElse(List.of())));
        movie.setProducers(getCompaniesByIds(Optional.ofNullable(request.getProducerIds()).orElse(List.of())));
        movie.setDistributor(getCompanyById(request.getDistributorId()));
        movie.setReviews(getReviewsByIds(Optional.ofNullable(request.getReviewIds()).orElse(List.of())));
        return movie;
    }

    public MovieResponse movieToResponse(Movie movie) {
        MovieResponse response = new MovieResponse();
        response.setId(movie.getId());
        response.setTitle(movie.getTitle());
        response.setYear(movie.getYear());
        response.setDuration(movie.getDuration());
        response.setDescription(movie.getDescription());
        response.setGenre(movie.getGenre());
        response.setPosterImg(movie.getPosterImg());
        response.setCast(movie.getCast().stream().map(personService::personToResponse).collect(Collectors.toList()));
        response.setDirectors(movie.getDirectors().stream().map(personService::personToResponse).collect(Collectors.toList()));
        response.setScreenwriters(movie.getScreenwriters().stream().map(personService::personToResponse).collect(Collectors.toList()));
        response.setProducers(movie.getProducers().stream()
                .filter(Objects::nonNull)  // Filter out null companies
                .map(companyService::companyToResponse)
                .collect(Collectors.toList()));
        response.setDistributor(companyService.companyToResponse(movie.getDistributor()));
        response.setReviews(movie.getReviews().stream()
                .map(reviewService::convertToResponseForMovie)
                .collect(Collectors.toList()));
        return response;
    }



    public Movie savePosterImg(long id, MultipartFile file) throws IOException {
        var movie = movieRepository.findById(id).orElseThrow(()-> new NotFoundException(id));
        Cloudinary cloudinary = new Cloudinary(cloudinaryUrl);
        var url = (String) cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("url");
        movie.setPosterImg(url);
        return movieRepository.save(movie);
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

    private List<Review> getReviewsByIds(List<Long> ids) {
        return ids.stream().map(id -> reviewService.findById(id)
                        .map(reviewService::convertToEntity)
                        .orElseThrow(() -> new NoSuchElementException("Review not found with id: " + id)))
                .collect(Collectors.toList());
    }

    private Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Company not found with id: " + id));
    }
}
