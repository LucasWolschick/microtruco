package microtruco.users.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import microtruco.users.entities.User;
import microtruco.users.repositories.UserRepository;
import microtruco.users.services.JwtService;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository repository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/auth")
    public HashMap<String, String> createUser(@Param("username") String username) {
        var user = new User();
        user.setUsername(username);
        user = repository.save(user);

        // return json object with user id and token
        // construct json
        var map = new HashMap<String, String>();
        map.put("id", user.getId().toString());
        map.put("token", jwtService.generateToken(user));
        return map;
    }

    @GetMapping("/{id}/validate/{token}")
    public boolean validateToken(@PathVariable("id") Long id, @PathVariable("token") String token) {
        var user = repository.findById(id).orElse(null);
        if (user == null) {
            return false;
        }
        try {
            return jwtService.isTokenValid(token, user);
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return repository.findById(id).orElseThrow();
    }
}
