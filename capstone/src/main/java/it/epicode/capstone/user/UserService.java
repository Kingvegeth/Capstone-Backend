package it.epicode.capstone.user;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.epicode.capstone.email.EmailService;
import it.epicode.capstone.exceptions.NotFoundException;
import it.epicode.capstone.security.*;

import it.epicode.capstone.exceptions.InvalidLoginException;
import it.epicode.capstone.security.roles.Roles;
import it.epicode.capstone.security.roles.RolesRepository;
import jakarta.persistence.EntityExistsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder encoder;
    private final UserRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final AuthenticationManager auth;
    private final JwtUtils jwt;
    private final EmailService emailService;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${CLOUDINARY_URL}")
    private String cloudinaryUrl;


//    public Optional<LoginResponseDTO> login(String username, String password) {
//        try {
//            //SI EFFETTUA IL LOGIN
//            //SI CREA UNA AUTENTICAZIONE OVVERO L'OGGETTO DI TIPO AUTHENTICATION
//            var a = auth.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//
//            a.getAuthorities(); //SERVE A RECUPERARE I RUOLI/IL RUOLO
//
//            //SI CREA UN CONTESTO DI SICUREZZA CHE SARA UTILIZZATO IN PIU OCCASIONI
//            SecurityContextHolder.getContext().setAuthentication(a);
//
//            var user = usersRepository.findOneByUsername(username).orElseThrow();
//            var dto = LoginResponseDTO.builder()
//                    .withUser(RegisteredUserDTO.builder()
//                            .withId(user.getId())
//                            .withFirstName(user.getFirstName())
//                            .withLastName(user.getLastName())
//                            .withEmail(user.getEmail())
//                            .withRoles(user.getRoles())
//                            .withUsername(user.getUsername())
//                            .build())
//                    .build();
//
//            //UTILIZZO DI JWTUTILS PER GENERARE IL TOKEN UTILIZZANDO UNA AUTHENTICATION E LO ASSEGNA ALLA LOGINRESPONSEDTO
//            dto.setToken(jwt.generateToken(a));
//
//            return Optional.of(dto);
//        } catch (NoSuchElementException e) {
//            //ECCEZIONE LANCIATA SE LO USERNAME E SBAGLIATO E QUINDI L'UTENTE NON VIENE TROVATO
//            log.error("User not found", e);
//            throw new InvalidLoginException(username, password);
//        } catch (AuthenticationException e) {
//            //ECCEZIONE LANCIATA SE LA PASSWORD E SBAGLIATA
//            log.error("Authentication failed", e);
//            throw new InvalidLoginException(username, password);
//        }
//    }


    public Optional<LoginResponseDTO> login(String username, String password) {
        try {
            Authentication authentication = auth.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            SecurityUserDetails userPrincipal = (SecurityUserDetails) authentication.getPrincipal();

            LoginResponseDTO dto = LoginResponseDTO.builder()
                    .withUser(buildRegisteredUserDTO(userPrincipal))
                    .build();

            dto.setToken(jwtUtils.generateToken(authentication));

            return Optional.of(dto);
        } catch (NoSuchElementException e) {
            log.error("User not found", e);
            throw new InvalidLoginException(username, password);
        } catch (AuthenticationException e) {
            log.error("Authentication failed", e);
            throw new InvalidLoginException(username, password);
        }


    }




    public RegisteredUserDTO register(RegisterUserDTO register){
        if(usersRepository.existsByUsername(register.getUsername())){
            throw new EntityExistsException("Utente gia' esistente");
        }
        if(usersRepository.existsByEmail(register.getEmail())){
            throw new EntityExistsException("Email gia' registrata");
        }
        Roles roles = rolesRepository.findById(Roles.ROLES_USER).get();
        /*
        if(!rolesRepository.existsById(Roles.ROLES_USER)){
            roles = new Roles();
            roles.setRoleType(Roles.ROLES_USER);
        } else {
            roles = rolesRepository.findById(Roles.ROLES_USER).get();
        }

         */
        User u = new User();
        BeanUtils.copyProperties(register, u);
        u.setPassword(encoder.encode(register.getPassword()));
        u.getRoles().add(roles);
        usersRepository.save(u);
        RegisteredUserDTO response = new RegisteredUserDTO();
        BeanUtils.copyProperties(u, response);
        response.setRoles(List.of(roles));
        emailService.sendWelcomeEmail(u.getEmail());

        return response;

    }

    public RegisteredUserDTO registerAdmin(RegisterUserDTO register){
        if(usersRepository.existsByUsername(register.getUsername())){
            throw new EntityExistsException("Utente gia' esistente");
        }
        if(usersRepository.existsByEmail(register.getEmail())){
            throw new EntityExistsException("Email gia' registrata");
        }
        Roles roles = rolesRepository.findById(Roles.ROLES_ADMIN).get();
        User u = new User();
        BeanUtils.copyProperties(register, u);
        u.setPassword(encoder.encode(register.getPassword()));
        u.getRoles().add(roles);
        usersRepository.save(u);
        RegisteredUserDTO response = new RegisteredUserDTO();
        BeanUtils.copyProperties(u, response);
        response.setRoles(List.of(roles));
        return response;

    }




    public List<RegisteredUserDTO> getAllUsers() {
        List<User> users = usersRepository.findAll();
        return users.stream().map(user -> {
            RegisteredUserDTO dto = RegisteredUserDTO.builder()
                    .withId(user.getId())
                    .withFirstName(user.getFirstName())
                    .withLastName(user.getLastName())
                    .withUsername(user.getUsername())
                    .withEmail(user.getEmail())
                    .withRoles(user.getRoles())
                    .build();
            return dto;
        }).collect(Collectors.toList());
    }


    public Optional<RegisteredUserDTO> getUserById(Long id) {
        return usersRepository.findById(id).map(this::convertToResponse);
    }



    private RegisteredUserDTO convertToResponse(User user) {
        RegisteredUserDTO dto = RegisteredUserDTO.builder()
                .withId(user.getId())
                .withFirstName(user.getFirstName())
                .withLastName(user.getLastName())
                .withUsername(user.getUsername())
                .withEmail(user.getEmail())
                .withRoles(user.getRoles())
                .build();
        return dto;
    }

    private RegisteredUserDTO buildRegisteredUserDTO(SecurityUserDetails userDetails) {
        RegisteredUserDTO userDto = new RegisteredUserDTO();
        userDto.setId(userDetails.getUserId());
        userDto.setEmail(userDetails.getEmail());
        userDto.setRoles(userDetails.getRoles());
        userDto.setUsername(userDetails.getUsername());
        userDto.setFirstName(userDetails.getFirstName());
        userDto.setLastName(userDetails.getLastName());

        return userDto;

    }


    public User saveAvatar(long id, MultipartFile file) throws IOException {
        var user = usersRepository.findById(id).orElseThrow(()-> new NotFoundException(id));
        Cloudinary cloudinary = new Cloudinary(cloudinaryUrl);
        var url = (String) cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("url");
        user.setAvatar(url);
        return usersRepository.save(user);
    }
}


