package com.onlinelearningmanagementsystem.WebSocket;

import com.onlinelearningmanagementsystem.EntityService.CourseService;
import com.onlinelearningmanagementsystem.EntityService.EnrolledCourseService;
import com.onlinelearningmanagementsystem.EntityService.UserService;
import com.onlinelearningmanagementsystem.JwtService.JwtUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;


public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private  final JwtUtil jwtUtil;
    private final UserService userService;
    private final CourseService courseService;
    private final EnrolledCourseService enrolledCourseService;



    public WebSocketAuthInterceptor(JwtUtil jwtUtil,UserService userService,CourseService courseService,EnrolledCourseService enrolledCourseService) {
        this.jwtUtil = jwtUtil;
        this.userService=userService;
        this.courseService=courseService;
        this.enrolledCourseService=enrolledCourseService;
    }



    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String query = request.getURI().getQuery();
        if (query != null && query.startsWith("token=")) {
            String token = query.substring(6); // Remove "token="
            if (jwtUtil.validateToken(token)) {
                attributes.put("userId", userService.findUserById(jwtUtil.extractUsername(token)).getAssignedId());
                return true;  // Proceed with the handshake if token is valid
            }
        }
        return false; // Reject connection if token is invalid


       // Reject connection if token is invalid
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Do nothing
    }
}
