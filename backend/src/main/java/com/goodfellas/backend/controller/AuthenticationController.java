package com.goodfellas.backend.controller;

import com.goodfellas.backend.dto.AuthResponseDTO;
import com.goodfellas.backend.dto.LoginDTO;
import com.goodfellas.backend.dto.PatientRegisterDTO;
import com.goodfellas.backend.dto.RegisterDTO;
import com.goodfellas.backend.model.Patient;
import com.goodfellas.backend.model.Psychologist;
import com.goodfellas.backend.repository.PatientRepository;
import com.goodfellas.backend.repository.PsychologistRepository;
import com.goodfellas.backend.security.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController
{
    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;
    private final PsychologistRepository psychologistRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JWTGenerator jwtGenerator,
                                    PsychologistRepository psychologistRepository,
                                    PatientRepository patientRepository,
                                    PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
        this.psychologistRepository = psychologistRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     ENDPOINT: POST /auth/login
     FUNCTION: Validates credentials, creates a security session, and issues a JWT token. It also fetches the specific profile data based on the user's role (Psychologist or Patient).
     @param loginDTO The DTO containing the username and password provided by the user.
     INPUT (JSON):
     {
     "username": "johndoe",
     "password": "Password123*"
     }
     @return A ResponseEntity containing the JWT token, role, and user profile data if successful; otherwise an error message.
     OUTPUT:
     - 200 OK: Returns an AuthResponseDTO containing:
     {
     "accessToken": "Bearer ${bearerToken}",
     "role": "ROLE_PATIENT"/"ROLE_PSYCHOLOGIST",
     "userData":
        - different for psychologist and therapist -
     }
     - 401 UNAUTHORIZED: "Invalid username or password"
     - 500 INTERNAL SERVER ERROR: Generic error message
     */
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO)
    {
        try
        {
            Authentication authentication = authenticationManager.authenticate
                    (
                    new UsernamePasswordAuthenticationToken
                            (
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                            )
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);

            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst().orElse("UNKNOWN");

            Object userData = null;
            if (role.equals("ROLE_PSYCHOLOGIST")) {
                userData = psychologistRepository.findByUsername(loginDTO.getUsername()).orElse(null);
            } else if (role.equals("ROLE_PATIENT")) {
                userData = patientRepository.findByUsername(loginDTO.getUsername()).orElse(null);
            }
            return new ResponseEntity<>(new AuthResponseDTO(token, role, userData), HttpStatus.OK);
        }
        catch (BadCredentialsException e)
        {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>("An error occurred during login: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     ENDPOINT: POST /auth/register/patient
     FUNCTION: Registers a new user with 'ROLE_PATIENT' authority; Validates that the username is unique and the password meets security complexity.
     @param registerDTO The DTO containing patient registration details (username, password, age, names).
     INPUT (JSON):
     {
     "username": "patient123",
     "password": "SecurePassword*",
     "firstName": "Jane",
     "lastName": "Smith",
     "age": 30
     }
     @return A string message indicating success or failure of the registration process.
     OUTPUT:
     - 200 OK: "Patient registered successfully!"
     - 400 BAD REQUEST: "Error: Username is already taken!" or password validation error
     - 500 INTERNAL SERVER ERROR: Error message
     */
    @PostMapping("register/patient")
    public ResponseEntity<String> registerPatient(@RequestBody PatientRegisterDTO registerDTO) {
        try
        {
            if (patientRepository.existsByUsername(registerDTO.getUsername()))
            {
                return new ResponseEntity<>("Error: Username is already taken!", HttpStatus.BAD_REQUEST);
            }
            if (!isValidPassword(registerDTO.getPassword()))
            {
                return new ResponseEntity<>("Error: Password must contain at least one uppercase letter, one lowercase letter, and one special character (#, %, or *)", HttpStatus.BAD_REQUEST);
            }
            Patient patient = new Patient();
            patient.setUsername(registerDTO.getUsername());
            patient.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            patient.setAge(registerDTO.getAge());
            patient.setFirstName(registerDTO.getFirstName());
            patient.setLastName(registerDTO.getLastName());
            patientRepository.save(patient);
            return new ResponseEntity<>("Patient registered successfully!", HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>("An error occurred during registration: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     ENDPOINT: POST /auth/register/patient
     FUNCTION: Registers a new user with 'ROLE_PSYCHOLOGIST' authority; Validates that the username is unique and the password meets security complexity.
     @param registerDTO The DTO containing patient registration details (username, password, age, names).
     INPUT (JSON):
     {
     "username": "psychologist1234",
     "password": "SecurePassword*",
     "firstName": "Mary",
     "lastName": "Sue",
     "age": 40
     }
     @return A string message indicating success or failure of the registration process.
     OUTPUT:
     - 200 OK: "Psychologist registered successfully!"
     - 400 BAD REQUEST: "Error: Username is already taken!" or password validation error
     - 500 INTERNAL SERVER ERROR: Error message
     */
    @PostMapping("register/psychologist")
    public ResponseEntity<String> registerPsychologist(@RequestBody RegisterDTO registerDTO) {
        try
        {
            if (psychologistRepository.existsByUsername(registerDTO.getUsername()))
            {
                return new ResponseEntity<>("Error: Username is already taken!", HttpStatus.BAD_REQUEST);
            }
            if (!isValidPassword(registerDTO.getPassword()))
            {
                return new ResponseEntity<>("Error: Password must contain at least one uppercase letter, one lowercase letter, and one special character (#, %, or *)", HttpStatus.BAD_REQUEST);
            }
            var psychologist = new Psychologist();
            psychologist.setUsername(registerDTO.getUsername());
            psychologist.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            psychologist.setFirstName(registerDTO.getFirstName());
            psychologist.setLastName(registerDTO.getLastName());
            psychologistRepository.save(psychologist);
            return new ResponseEntity<>("Psychologist registered successfully!", HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>("An error occurred during registration: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValidPassword(String password)
    {
        if (password == null || password.length() < 4)
            return false;
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasSpecialChar = password.contains("#") || password.contains("%") || password.contains("*");
        return hasUppercase && hasLowercase && hasSpecialChar;
    }
}