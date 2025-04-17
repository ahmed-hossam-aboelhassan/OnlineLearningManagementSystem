package com.onlinelearningmanagementsystem.Controller;

import com.onlinelearningmanagementsystem.ControllerService.ProfileControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    ProfileControllerService profileControllerService;

    @GetMapping
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization")String token){
        return profileControllerService.getProfile(token);

    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization")String token,@RequestBody Map<String,String> profileAttributes){
        return profileControllerService.updateProfile(token,profileAttributes);
    }

    @GetMapping("/picture")
    public ResponseEntity<?> getProfilePicture(@RequestHeader("Authorization")String token){
        return profileControllerService.getProfilePicture(token);

    }
    @PutMapping("/picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestHeader("Authorization")String token,@RequestParam("file") MultipartFile file){
        return profileControllerService.uploadProfilePicture(token,file);
    }
}
