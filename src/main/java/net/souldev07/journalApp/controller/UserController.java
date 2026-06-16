package net.souldev07.journalApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.souldev07.journalApp.api.response.WeatherResponse;
import net.souldev07.journalApp.entity.User;
import net.souldev07.journalApp.service.UserService;
import net.souldev07.journalApp.service.WeatherService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public ResponseEntity<String> greeting() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String greetingMessage = "Hi " + username + "!";

        WeatherResponse weatherData = weatherService.getWeather("Mumbai");
        if (weatherData != null)
            greetingMessage += " Weather feels like " + weatherData.getMain().getFeelsLike() + "\u00b0C";

        return new ResponseEntity<>(greetingMessage, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User userInDB = userService.findByUsername(username);
        if (userInDB == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        userInDB.setUsername(user.getUsername());
        userInDB.setPassword(user.getPassword());
        userInDB.setEmail(user.getEmail());
        userInDB.setSentimentAnalysis(user.isSentimentAnalysis());

        userService.saveUser(userInDB);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        userService.deleteByUsername(username);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}