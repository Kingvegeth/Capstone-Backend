package it.epicode.capstone.user;

import com.cloudinary.Cloudinary;
import it.epicode.capstone.security.*;
import it.epicode.capstone.exceptions.ApiValidationException;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService user;

    @Autowired
    private UserRepository usersRepository;




    @GetMapping
    public ResponseEntity<List<RegisteredUserDTO>> getAllUsers() {
        List<RegisteredUserDTO> users = user.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegisteredUserDTO> getUserById(@PathVariable Long id) {
        Optional<RegisteredUserDTO> userDTO = user.getUserById(id);
        return userDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @GetMapping("me")
    public ResponseEntity<RegisteredUserDTO> getCurrentUser() {
        Optional<RegisteredUserDTO> userDTO = user.getUserById(user.getCurrentUserId());
        return userDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @PostMapping("register")
    public ResponseEntity<RegisteredUserDTO> register(@RequestBody @Validated RegisterUserModel model, BindingResult validator){
        if (validator.hasErrors()) {
            throw new ApiValidationException(validator.getAllErrors());
        }
        try {
            var registeredUser = user.register(
                    RegisterUserDTO.builder()
                            .withFirstName(model.firstName())
                            .withLastName(model.lastName())
                            .withUsername(model.username())
                            .withEmail(model.email())
                            .withPassword(model.password())
                            .build());
            return new ResponseEntity<>(registeredUser, HttpStatus.OK);
        } catch (EntityExistsException ex) {
            throw ex;
        }
    }

//    @GetMapping("/activate")
//    public ResponseEntity<String> activateUser(@RequestParam String token) {
//        boolean isActivated = user.activateUser(token);
//        if (isActivated) {
//            return ResponseEntity.ok("Account attivato con successo!");
//        } else {
//            return ResponseEntity.status(400).body("Token di attivazione non valido!");
//        }
//    }

    @GetMapping("/activate")
    public ResponseEntity<Void> activateUser(@RequestParam String token, HttpServletResponse response) throws IOException {
        boolean isActivated = user.activateUser(token);
        if (isActivated) {
            response.sendRedirect("http://localhost:4200/activation-success");
        } else {
            response.sendRedirect("http://localhost:4200/activation-error");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Validated LoginModel model, BindingResult validator) {
        if (validator.hasErrors()) {
            throw  new ApiValidationException(validator.getAllErrors());
        }
        return new ResponseEntity<>(user.login(model.username(), model.password()).orElseThrow(), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutCurrentUser() {
        user.logoutCurrentUser();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<RegisteredUserDTO> registerAdmin(@RequestBody RegisterUserDTO registerUser){
        return ResponseEntity.ok(user.registerAdmin(registerUser));
    }

    @PatchMapping("edit")
    public ResponseEntity<RegisteredUserDTO> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        RegisteredUserDTO updatedUser = user.updateUser(updateUserRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/avatar")
    public ResponseEntity<RegisteredUserDTO> uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        try {
            RegisteredUserDTO userDto = user.saveAvatar(file);
            return ResponseEntity.ok(userDto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCurrentUser() {
        user.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }

}

