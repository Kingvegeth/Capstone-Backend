package it.epicode.capstone.company;

import it.epicode.capstone.movie.Movie;
import it.epicode.capstone.movie.MovieRepository;
import it.epicode.capstone.movie.MovieService;
import it.epicode.capstone.movie.MovieSimpleResponse;
import it.epicode.capstone.review.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private MovieRepository movieRepository;



    public List<CompanyResponse> findAll() {
        return companyRepository.findAll().stream().map(this::companyToResponse).collect(Collectors.toList());
    }

    public Optional<CompanyResponse> findById(Long id) {
        return companyRepository.findById(id).map(this::companyToResponse);
    }





    public CompanyResponse createCompany(CompanyRequest request) {
        Company company = requestToCompany(request);
        companyRepository.save(company);
        return companyToResponse(company);
    }

    public Optional<CompanyResponse> updateCompany(Long id, CompanyRequest request) {
        return companyRepository.findById(id).map(company -> {
            company.setName(request.getName());
            companyRepository.save(company);
            return companyToResponse(company);
        });
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    private Company requestToCompany(CompanyRequest request) {
        Company company = new Company();
        company.setName(request.getName());
        return company;
    }

    public CompanyResponse companyToResponse(Company company) {
        if (company == null) {
            return null;
        }
        CompanyResponse response = new CompanyResponse();
        response.setId(company.getId());
        response.setName(company.getName());
        return response;
    }
}
