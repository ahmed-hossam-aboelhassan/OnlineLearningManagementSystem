package com.onlinelearningmanagementsystem.ControllerService;

import com.onlinelearningmanagementsystem.Dto.AdminProfileDto;
import com.onlinelearningmanagementsystem.Dto.InstructorProfileDto;
import com.onlinelearningmanagementsystem.Dto.StudentProfileDto;
import com.onlinelearningmanagementsystem.Entity.Role;
import com.onlinelearningmanagementsystem.Entity.User;
import com.onlinelearningmanagementsystem.EntityService.UserService;
import com.onlinelearningmanagementsystem.Enum.EducationLevel;
import com.onlinelearningmanagementsystem.Enum.RoleType;
import com.onlinelearningmanagementsystem.Exception.*;
import com.onlinelearningmanagementsystem.JwtService.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Array;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;



@Service
public class ProfileControllerService {
    private final String uploadDir = "C:/Users/Administrator/Desktop/Projects/OnlineLearningManagementSystemProject/UsersProfilePicture/";

    private static final Logger logger = LoggerFactory.getLogger(ProfileControllerService.class);

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User extractUser(String token) {



        String jwtToken = token.substring(7);
        String id = jwtUtil.extractUsername(jwtToken);
        return userService.findUserById(id);
    }
    private boolean hasRole(User user,RoleType roleType){
        boolean flag=false;
        for(Role role:user.getRoles()){
            if(role.getRole().equals(roleType)){
                flag=true;
                break;
            }
        }
        return flag;

    }





    private ResponseEntity<?> profileGetter(User user){

        if(hasRole(user,RoleType.STUDENT))
            return ResponseEntity.ok(new StudentProfileDto(user));
        else if(hasRole(user,RoleType.INSTRUCTOR))
            return ResponseEntity.ok(new InstructorProfileDto(user));
        else
            return ResponseEntity.ok(new AdminProfileDto(user));
    }
    private void updateProfileField(Map<String, Consumer<String>> updates,User user, String field, String value,List<String> ignoredFields) {



        if (updates.containsKey(field)) {
            updates.get(field).accept(value);
            user.getProfile().setUpdatedAt(LocalDateTime.now());
        } else if ("educationLevel".equals(field) && hasRole(user,RoleType.STUDENT)) {
            user.getProfile().getStudentProfile().setEducationLevel(EducationLevel.valueOf(value));
            user.getProfile().setUpdatedAt(LocalDateTime.now());

        } else if ("aboutMe".equals(field) && hasRole(user,RoleType.STUDENT)) {
            user.getProfile().getStudentProfile().setAboutMe(value);
            user.getProfile().setUpdatedAt(LocalDateTime.now());


        } else if ("qualification".equals(field) && hasRole(user,RoleType.INSTRUCTOR)) {
            user.getProfile().getInstructorProfile().setQualification(value);
            user.getProfile().setUpdatedAt(LocalDateTime.now());

        } else if ("teachingExperience".equals(field) && hasRole(user,RoleType.INSTRUCTOR)) {
            user.getProfile().getInstructorProfile().setTeachingExperience(Integer.parseInt(value));
            user.getProfile().setUpdatedAt(LocalDateTime.now());

        }
        else{
            ignoredFields.add(field);
        }


    }



    public ResponseEntity<?> getProfile(String token){
        User user=extractUser(token);
        return profileGetter(user);


    }
    public ResponseEntity<?>updateProfile(String token, Map<String,String> profileAttributes){

        List<String> ignoredFields = new ArrayList<>();
        User user=extractUser(token);
        Map<String, Consumer<String>> updates = Map.of(
                "firstName", x -> user.getProfile().setFirstName(x),
                "lastName", x -> user.getProfile().setLastName(x),
                "phoneNumber", x -> user.getProfile().setPhoneNumber(x),
                "password", v -> user.setPassword(passwordEncoder.encode(v))
        );
        profileAttributes.forEach((key, value) -> updateProfileField(updates,user,key,value,ignoredFields)
     );
        // Save changes only if at least one field was updated successfully
        if (!ignoredFields.isEmpty() && ignoredFields.size() == profileAttributes.size()) {
            throw new InvalidProfileUpdateException("All provided fields are invalid: " + ignoredFields);
        }

            userService.saveUser(user);
            logger.info("{} Successfully updated their profile", user.getAssignedId());
            return profileGetter(user);



        }
    public ResponseEntity<?> uploadProfilePicture( String token,  MultipartFile file) {

        User user = extractUser(token);
        if (file.isEmpty()) {
            logger.warn("Failed to upload profile picture for {} as file is invalid or empty.",  user.getAssignedId());
            throw new InvalidFileException("Please upload a valid file.");
        }
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            logger.warn("Invalid file type for {}. Only JPEG/PNG allowed.", user.getAssignedId());
            throw new InvalidFileException("Only JPEG or PNG images are allowed.");
        }


        String filename = user.getAssignedId()+"profile_picture";
        Path filePath = Paths.get(uploadDir + filename+".jpeg");


        try {
            Files.copy(file.getInputStream(),filePath,StandardCopyOption.REPLACE_EXISTING);
            user.getProfile().setProfilePicture(filename);  // Save only the filename, not full path
            userService.saveUser(user);

            logger.info("Successfully uploaded a new profile picture for {}",user.getAssignedId());
            return ResponseEntity.ok("Profile picture uploaded successfully. Access it at: http://localhost:8080/profile-picture/" + filename);

        } catch (Exception e) {

            logger.error("Error uploading profile picture for user {}", user.getAssignedId(),e);
            throw new FileUploadException("Error uploading profile picture.", e);
        }
    }
    public ResponseEntity<?> getProfilePicture( String token) {

        User user=extractUser(token);
        if (user.getProfile() == null || user.getProfile().getProfilePicture() == null) {
            logger.warn("Profile picture not found for user {}", user.getAssignedId());
            throw new ProfilePictureNotFoundException("No profile picture uploaded.");
        }



        try {

            Path imagePath = Paths.get(uploadDir + user.getProfile().getProfilePicture()+".jpeg");
            Resource resource = new UrlResource(imagePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                logger.error("Error cannot to get the profile picture for user {}",user.getAssignedId());
                throw new ProfilePictureNotFoundException("Profile picture not found for user " + user.getAssignedId());

            }


            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)  // Change to IMAGE_JPEG if needed
                    .body(resource);
        }  catch (IOException e) { // Handle specific file-related errors
            logger.error("Error accessing profile picture for user {}. Error: {}", user.getAssignedId(), e.getMessage());
            throw new ProfilePictureRetrievalException("Error retrieving profile picture for user " + user.getAssignedId(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching profile picture for user {}. Error: {}", user.getAssignedId(), e.getMessage());
            throw new ProfilePictureRetrievalException("Unexpected error retrieving profile picture for user " + user.getAssignedId(), e);
        }
    }



}
