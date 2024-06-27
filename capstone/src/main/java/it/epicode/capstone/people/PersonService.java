package it.epicode.capstone.people;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public List<PersonResponse> findAll() {
        return personRepository.findAll().stream().map(this::personToResponse).collect(Collectors.toList());
    }

    public Optional<PersonResponse> findById(Long id) {
        return personRepository.findById(id).map(this::personToResponse);
    }

    public PersonResponse createPerson(PersonRequest request) {
        Person person = requestToPerson(request);
        personRepository.save(person);
        return personToResponse(person);
    }

    public Optional<PersonResponse> updatePerson(Long id, PersonRequest request) {
        return personRepository.findById(id).map(person -> {
            person.setFirstName(request.getFirstName());
            person.setLastName(request.getLastName());
            person.setDateOfBirth(request.getDateOfBirth());
            personRepository.save(person);
            return personToResponse(person);
        });
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    private Person requestToPerson(PersonRequest request) {
        Person person = new Person();
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setDateOfBirth(request.getDateOfBirth());
        return person;
    }

    public PersonResponse personToResponse(Person person) {
        PersonResponse personResponseDto = new PersonResponse();
        personResponseDto.setId(person.getId());
        personResponseDto.setFirstName(person.getFirstName());
        personResponseDto.setLastName(person.getLastName());
        personResponseDto.setDateOfBirth(person.getDateOfBirth());
        return personResponseDto;
    }
}
