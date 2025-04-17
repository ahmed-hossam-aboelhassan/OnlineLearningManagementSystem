package com.onlinelearningmanagementsystem.Security;

import com.onlinelearningmanagementsystem.EntityService.*;
import com.onlinelearningmanagementsystem.JwtService.JwtUtil;
import com.onlinelearningmanagementsystem.WebSocket.MyWebSocketHandler;
import com.onlinelearningmanagementsystem.WebSocket.WebSocketAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private EnrolledCourseService enrolledCourseService;
    @Autowired
    private MessageNotificationService messageNotificationService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(userService,messageNotificationService), "/ws")
                .setAllowedOrigins("*")  // Allow all origins for simplicity
                .addInterceptors(new WebSocketAuthInterceptor(jwtUtil ,userService,courseService,enrolledCourseService)) // Add the custom authentication interceptor
                .setAllowedOrigins("*");  // Allow all origins for simplicity
    }
}


