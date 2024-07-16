package it.epicode.capstone.movie;

import it.epicode.capstone.company.Company;
import it.epicode.capstone.people.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findAll(Pageable pageable);
    List<Movie> findByCastContains(Person person);
    List<Movie> findByDirectorsContains(Person person);
    List<Movie> findByScreenwritersContains(Person person);
    List<Movie> findByProducersContains(Company company);
    List<Movie> findByDistributor(Company company);
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    @Query("SELECT m FROM Movie m LEFT JOIN m.reviews r GROUP BY m.id ORDER BY COALESCE(AVG(r.rating), 0) DESC")
    List<Movie> findTopRatedMovies(Pageable pageable);
}
