package com.onlinelearningmanagementsystem.ControllerService;

import com.onlinelearningmanagementsystem.Dto.AuthRequest;
import com.onlinelearningmanagementsystem.Dto.AuthResponse;
import com.onlinelearningmanagementsystem.Dto.RegisterRequest;
import com.onlinelearningmanagementsystem.Entity.*;
import com.onlinelearningmanagementsystem.EntityService.TrackerService;
import com.onlinelearningmanagementsystem.EntityService.UserService;
import com.onlinelearningmanagementsystem.Enum.EducationLevel;
import com.onlinelearningmanagementsystem.Enum.RoleType;
import com.onlinelearningmanagementsystem.Exception.EmailAlreadyRegisteredException;
import com.onlinelearningmanagementsystem.Exception.InvalidCredentialsException;
import com.onlinelearningmanagementsystem.Exception.TrackerNotInitializedException;
import com.onlinelearningmanagementsystem.JwtService.CustomUserDetailsService;
import com.onlinelearningmanagementsystem.JwtService.JwtUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegisterLoginControllerService {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TrackerService trackerService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(RegisterLoginControllerService.class);


    private void trackerIncrementer(Tracker tracker, Role role) {
        if (role.getRole().equals(RoleType.STUDENT))
            tracker.setLateststudent(tracker.getLateststudent() + 1);
        else if (role.getRole().equals(RoleType.INSTRUCTOR))
            tracker.setLatestinstructor(tracker.getLatestinstructor() + 1);

        else
            tracker.setLatestadmin(tracker.getLatestadmin() + 1);

        trackerService.save(tracker);
        logger.info("Updated tracker: {} new student count: {}, instructor count: {}, admin count: {}",
                tracker.getId(), tracker.getLateststudent(), tracker.getLatestinstructor(), tracker.getLatestadmin());

    }

    private void setStudentProfile(User user) {
        user.getProfile().setStudentProfile(new StudentProfile());
        user.getProfile().getStudentProfile().setEducationLevel(EducationLevel.NOT_DECLARED);
        user.getProfile().getStudentProfile().setAboutMe("Tell us about yourself");
    }

    private void setInstructorProfile(User user) {
        user.getProfile().setInstructorProfile(new InstructorProfile());
        user.getProfile().getInstructorProfile().setQualification("Tell us your qualifications");
        user.getProfile().getInstructorProfile().setTeachingExperience(0);
    }


    private void profileIntializer(User user, RegisterRequest registerRequest, Role role) {
        user.setProfile(new Profile());
        user.getProfile().setFirstName(registerRequest.getFirstName());
        user.getProfile().setLastName(registerRequest.getLastName());
        user.getProfile().setDateOfBirth(registerRequest.getDateOfBirth());
        user.getProfile().setPhoneNumber(registerRequest.getPhoneNumber());
        if (role.getRole().equals(RoleType.STUDENT)) {
            setStudentProfile(user);
        } else if (role.getRole().equals(RoleType.INSTRUCTOR)) {
            setInstructorProfile(user);
        }

        user.getProfile().setCreatedAt(LocalDateTime.now());
        user.getProfile().setUpdatedAt(LocalDateTime.now());


    }

    @Transactional
    private void assignUserRolesAndProfile(RegisterRequest registerRequest, Role role, String assinedid) {
        logger.info("Assigning roles, id and profile for new user ");
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        user.addRoles(role);
        user.setAssignedId(assinedid);
        profileIntializer(user, registerRequest, role);

        userService.saveUser(user);


    }

    private boolean checkIfEmailIsAlreadyRegistered(String reqemail) {
        return (userService.findUserByEmail(reqemail) != null);

    }

    private String newIdGenerator(RoleType role, Tracker tracker) {

        String newId = "";
        int temp;
        switch (role) {
            case RoleType.INSTRUCTOR:
                temp = tracker.getLatestinstructor() + 1;
                if (temp < 10)
                    newId += "INS-000" + temp;
                else
                    newId += "INS-00" + temp;
                break;
            case RoleType.ADMIN:
                temp = tracker.getLatestadmin() + 1;
                if (temp < 10)
                    newId += "ADM-000" + temp;
                else
                    newId += "ADM-00" + temp;
                break;


            default:
                temp = tracker.getLateststudent() + 1;
                if (temp < 10)
                    newId += "49-000" + temp;
                else
                    newId += "49-00" + temp;
                break;
        }
        return newId;
    }

    private ResponseEntity<?> authentication(AuthRequest authRequest) {
        try {
            authenticate(authRequest.getEmail(), authRequest.getPassword());
        } catch (BadCredentialsException e) {
            logger.warn("Failed login attempt ");
            throw new InvalidCredentialsException("Invalid credentials. Please try again.");
        }
        logger.info("Attempting sign in is authenticated");
        return generateLoginResponse(authRequest.getEmail());
    }

    private void authenticate(String id, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(id, password));
    }

    private ResponseEntity<?> generateLoginResponse(String id) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(id);
        String token = jwtUtil.generateToken(
                userDetails.getUsername(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
        User user = userService.findUserById(id);
        logger.info("{} Successfully signed in ", user.getAssignedId());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    public ResponseEntity<?> registerUser(RegisterRequest registerRequest) {


        if (checkIfEmailIsAlreadyRegistered(registerRequest.getEmail())) {
            logger.warn("Failed to register as email: {} is already registered", registerRequest.getEmail());
            throw new EmailAlreadyRegisteredException("Email is already registered. Please login.");

        }


        Role role = new Role();
        role.setRole(registerRequest.getRoleType());
        Tracker tracker = trackerService.findTrackerbyId(1);
        if (tracker == null) {
            logger.error("No tracker instaniated");
            throw new TrackerNotInitializedException("Tracker needs to be initialized in the database");
        }
        String generatedId = newIdGenerator(registerRequest.getRoleType(), tracker);
        assignUserRolesAndProfile(registerRequest, role, generatedId);
        trackerIncrementer(tracker, role);

        logger.info("Successfully registered as {} with role {}", generatedId, registerRequest.getRoleType());
        return ResponseEntity.status(HttpStatus.CREATED).body("Registered successfully as " + role.getRole());


    }


    public ResponseEntity<?> login(AuthRequest authRequest) {



        User user = userService.findUserByEmail(authRequest.getEmail());
        if (user == null) {
            logger.warn("Failed to login as email: {} unregistered",authRequest.getEmail());
            throw new InvalidCredentialsException("Invalid credentials. Please try again.");
        }
        authRequest.setUsername(user.getId());
        return authentication(authRequest);
    }


    }

