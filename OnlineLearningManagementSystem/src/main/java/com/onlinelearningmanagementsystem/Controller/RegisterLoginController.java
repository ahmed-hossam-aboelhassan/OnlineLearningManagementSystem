package com.onlinelearningmanagementsystem.Controller;

import com.onlinelearningmanagementsystem.ControllerService.RegisterLoginControllerService;
import com.onlinelearningmanagementsystem.Dto.AuthRequest;
import com.onlinelearningmanagementsystem.Dto.RegisterRequest;
import com.onlinelearningmanagementsystem.Entity.User;
import com.onlinelearningmanagementsystem.Enum.RoleType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/auth")
public class RegisterLoginController {
    @Autowired
    private RegisterLoginControllerService registerLoginControllerService;


    @PostMapping("/register")
    public ResponseEntity<?> registerAsStudent(@Valid @RequestBody RegisterRequest newUser){
      return  registerLoginControllerService.registerUser(newUser);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest){
        return  registerLoginControllerService.login(authRequest);

    }

}
