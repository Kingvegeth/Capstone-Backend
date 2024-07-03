package it.epicode.capstone.user;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.epicode.capstone.comment.Comment;
import it.epicode.capstone.comment.CommentRepository;
import it.epicode.capstone.email.EmailService;
import it.epicode.capstone.exceptions.LastAdminDeletionException;
import it.epicode.capstone.exceptions.NotFoundException;
import it.epicode.capstone.review.Review;
import it.epicode.capstone.review.ReviewRepository;
import it.epicode.capstone.security.*;

import it.epicode.capstone.exceptions.InvalidLoginException;
import it.epicode.capstone.security.config.RestTemplateConfig;
import it.epicode.capstone.security.roles.Roles;
import it.epicode.capstone.security.roles.RolesRepository;
import jakarta.persistence.EntityExistsException;

import jakarta.persistence.EntityNotFoundException;
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
import java.util.UUID;
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

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Value("${CLOUDINARY_URL}")
    private String cloudinaryUrl;

    @Value("${default.avatar.url}")
    private String defaultAvatarUrl;


    public Optional<LoginResponseDTO> login(String username, String password) {
        try {
            Authentication authentication = auth.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            SecurityUserDetails userPrincipal = (SecurityUserDetails) authentication.getPrincipal();

            String token = jwtUtils.generateToken(authentication);

            // Aggiorna il token JWT nell'interceptor
            RestTemplateConfig.JwtInterceptor.setToken(token);


            LoginResponseDTO dto = LoginResponseDTO.builder()
                    .withUser(buildRegisteredUserDTO(userPrincipal))
                    .withToken(token)
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

    public void logoutCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String) {
            String token = (String) authentication.getCredentials();
            tokenService.blacklistToken(token);
            SecurityContextHolder.clearContext();
        } else {
            throw new IllegalStateException("Utente non autenticato o token non valido");
        }
    }



    public RegisteredUserDTO register(RegisterUserDTO register){
        if(usersRepository.existsByUsername(register.getUsername())){
            throw new EntityExistsException("Username gia' esistente");
        }
        if(usersRepository.existsByEmail(register.getEmail())){
            throw new EntityExistsException("Email gia' registrata");
        }
        Roles roles = rolesRepository.findById(Roles.ROLES_USER).get();

        User u = new User();
        BeanUtils.copyProperties(register, u);
        u.setPassword(encoder.encode(register.getPassword()));
        u.getRoles().add(roles);

        if (u.getAvatar() == null) {
            u.setAvatar(defaultAvatarUrl);
        }

        String activationToken = UUID.randomUUID().toString();
        u.setActivationToken(activationToken);
        u.setActive(false);

        usersRepository.save(u);
        RegisteredUserDTO response = new RegisteredUserDTO();
        BeanUtils.copyProperties(u, response);
        response.setRoles(List.of(roles));

        emailService.sendActivationEmail(u.getEmail(), u.getFirstName(), activationToken);

        return response;

    }

    public RegisteredUserDTO updateUser(UpdateUserRequest updateUserRequest) {
        var id = this.getCurrentUserId();
        User user = usersRepository.findById(id).orElseThrow(() -> new NotFoundException(id));

        if (updateUserRequest.getFirstName() != null) {
            user.setFirstName(updateUserRequest.getFirstName());
        }
        if (updateUserRequest.getLastName() != null) {
            user.setLastName(updateUserRequest.getLastName());
        }
        if (updateUserRequest.getUsername() != null) {
            user.setUsername(updateUserRequest.getUsername());
        }
        if (updateUserRequest.getEmail() != null) {
            user.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.getPassword() != null) {
            user.setPassword(encoder.encode(updateUserRequest.getPassword()));
        }

        usersRepository.save(user);
        return convertToResponse(user);
    }

    public boolean activateUser(String token) {
        Optional<User> userOpt = userRepository.findByActivationToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(true);
            user.setActivationToken(null);
            userRepository.save(user);
            return true;
        }
        return false;
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
        u.setActive(true);
        u.setActivationToken(null);

        if (u.getAvatar() == null) {
            u.setAvatar(defaultAvatarUrl);
        }

        usersRepository.save(u);

        RegisteredUserDTO response = new RegisteredUserDTO();
        BeanUtils.copyProperties(u, response);
        response.setRoles(List.of(roles));

        return response;

    }




    public List<RegisteredUserDTO> getAllUsers() {
        List<User> users = usersRepository.findAll();
        return users.stream().map(this::convertToResponse).collect(Collectors.toList());
    }



    public Optional<RegisteredUserDTO> getUserById(Long id) {
        return usersRepository.findById(id).map(this::convertToResponse);
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUserDetails) {
            SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();
            return userDetails.getUserId();
        }
        throw new IllegalStateException("Utente non autenticato");
    }


    public RegisteredUserDTO convertToResponse(User user) {
        RegisteredUserDTO dto = RegisteredUserDTO.builder()
                .withId(user.getId())
                .withFirstName(user.getFirstName())
                .withLastName(user.getLastName())
                .withUsername(user.getUsername())
                .withEmail(user.getEmail())
                .withAvatar(user.getAvatar())
                .withCreatedAt(user.getCreatedAt())
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
        userDto.setAvatar(userDetails.getAvatar());
        userDto.setCreatedAt(userDetails.getCreatedAt());

        return userDto;

    }


    public RegisteredUserDTO saveAvatar(MultipartFile file) throws IOException {
        var id = this.getCurrentUserId();
        var user = usersRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        Cloudinary cloudinary = new Cloudinary(cloudinaryUrl);
        var url = (String) cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("url");
        user.setAvatar(url);
        usersRepository.save(user);
        return convertToResponse(user);
    }

    public void deleteCurrentUser() {
        Long userId = getCurrentUserId();
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userId));

        // Controlla se l'utente da eliminare è admin
        boolean isUserToDeleteAdmin = userToDelete.getRoles().stream()
                .anyMatch(role -> role.getRoleType().equals("ADMIN"));

        // Se l'utente da eliminare è un admin, controlla che non sia l'ultimo admin
        if (isUserToDeleteAdmin) {
            long adminCount = userRepository.countByRoles_RoleType("ADMIN");
            if (adminCount <= 1) {
                throw new LastAdminDeletionException("Cannot delete the last admin user.");
            }
        }

        removeUserReferences(userId);
        userRepository.deleteById(userId);
    }

    private void removeUserReferences(Long userId) {
        // Aggiorna le recensioni dell'utente
        List<Review> reviews = reviewRepository.findByUserId(userId);
        for (Review review : reviews) {
            review.removeUser();
        }
        reviewRepository.saveAll(reviews);

        // Aggiorna i commenti dell'utente
        List<Comment> comments = commentRepository.findByUserId(userId);
        for (Comment comment : comments) {
            comment.removeUser();
        }
        commentRepository.saveAll(comments);
    }
}


