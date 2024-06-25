package it.epicode.capstone.user;

import com.cloudinary.Cloudinary;
import it.epicode.capstone.security.*;
import it.epicode.capstone.security.exceptions.ApiValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService user;

    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private Cloudinary cloudinary;


    @GetMapping
    public ResponseEntity<List<RegisteredUserDTO>> getAllUsers() {
        List<RegisteredUserDTO> allUsers = user.getAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegisteredUserDTO> getUserById(@PathVariable Long id) {
        Optional<RegisteredUserDTO> userDTO = user.getUserById(id);
        return userDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("register")
    public ResponseEntity<RegisteredUserDTO> register(@RequestBody @Validated RegisterUserModel model, BindingResult validator){
       if (validator.hasErrors()) {
            throw new ApiValidationException(validator.getAllErrors());
        }
        var registeredUser = user.register(
                RegisterUserDTO.builder()
                        .withFirstName(model.firstName())
                        .withLastName(model.lastName())
                        .withUsername(model.username())
                        .withEmail(model.email())
                        .withPassword(model.password())
                        .build());

        return  new ResponseEntity<> (registeredUser, HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Validated LoginModel model, BindingResult validator) {
        if (validator.hasErrors()) {
            throw  new ApiValidationException(validator.getAllErrors());
        }
        return new ResponseEntity<>(user.login(model.username(), model.password()).orElseThrow(), HttpStatus.OK);
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<RegisteredUserDTO> registerAdmin(@RequestBody RegisterUserDTO registerUser){
        return ResponseEntity.ok(user.registerAdmin(registerUser));
    }


        @PostMapping("/{username}/avatar")
        public ResponseEntity<String> uploadAvatar(@PathVariable String username, @RequestParam("file") MultipartFile file) {
            try {
                // Carica l'immagine su Cloudinary
                var uploadResult = cloudinary.uploader().upload(file.getBytes(),
                        com.cloudinary.utils.ObjectUtils.asMap("public_id", username + "_avatar"));

                // Recupera l'URL dell'immagine
                String url = uploadResult.get("url").toString();

                // Aggiorna l'utente con l'URL dell'immagine avatar
                Optional<User> userOptional = usersRepository.findOneByUsername(username);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    user.setAvatar(url);
                    usersRepository.save(user);
                    return ResponseEntity.ok(url);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload avatar");
            }
        }

        @GetMapping("/{username}/avatar")
        public ResponseEntity<String> getUserAvatar(@PathVariable String username) {
            Optional<User> user = usersRepository.findOneByUsername(username);
            if (user.isPresent() && user.get().getAvatar() != null) {
                return ResponseEntity.ok(user.get().getAvatar());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Avatar not found");
            }
        }
}

