package it.epicode.capstone.company;

import it.epicode.capstone.movie.MovieService;
import it.epicode.capstone.movie.MovieSimpleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private MovieService movieService;

    @GetMapping
    public List<CompanyResponse> getAllCompanies() {
        return companyService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable Long id) {
        Optional<CompanyResponse> company = companyService.findById(id);
        return company.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/movies/produced")
    public ResponseEntity<List<MovieSimpleResponse>> getMoviesProducedByCompany(@PathVariable Long id) {
        List<MovieSimpleResponse> movies = movieService.findMoviesByProducerId(id);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}/movies/distributed")
    public ResponseEntity<List<MovieSimpleResponse>> getMoviesDistributedByCompany(@PathVariable Long id) {
        List<MovieSimpleResponse> movies = movieService.findMoviesByDistributorId(id);
        return ResponseEntity.ok(movies);
    }

    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(@RequestBody CompanyRequest request) {
        CompanyResponse response = companyService.createCompany(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(@PathVariable Long id, @RequestBody CompanyRequest request) {
        Optional<CompanyResponse> updatedCompany = companyService.updateCompany(id, request);
        return updatedCompany.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
