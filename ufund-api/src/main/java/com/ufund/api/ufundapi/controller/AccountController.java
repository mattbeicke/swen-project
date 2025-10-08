package com.ufund.api.ufundapi.controller;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufund.api.ufundapi.model.User;
import com.ufund.api.ufundapi.persistence.UserDAO;

/**
 * Handles the REST API requests for the Accounts resource
 * 
 * @author Anthony Ficalora
 */
@RestController
@RequestMapping("accounts")
public class AccountController {
    private static final Logger LOG = Logger.getLogger(CupboardController.class.getName());
    private UserDAO userDAO;

    /**
     * Creates a REST API controller to reponds to requests
     * 
     * @param userDAO The {@link UserDAO User Data Access Object} to perform CRUD
     *                operations
     */
    public AccountController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Tries to verify a login then create a login key
     * 
     * @param input contains the {@link User user's} username and password
     * 
     * @return ResponseEntity with created key and HTTP status of OK if successful.
     *         ResponseEntity with HTTP status of UNAUTHORIZED if username password
     *         combo was incorrect or nonexistant. ResponseEntity with HTTP status
     *         of INTERNAL_SERVER_ERROR otherwise.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> input) {
        try {
            String username = input.get("username");
            String password = input.get("password");

            if (!userDAO.verifyLogin(username, password))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            String key = userDAO.attemptLogin(username, password);
            return new ResponseEntity<>(key, HttpStatus.OK);

        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Tries to log a {@link User user} out
     * 
     * @param username the {@link User user's} username
     * 
     * @return Empty ResponseEntity and HTTP status of OK if successful.
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody String username, @RequestHeader Map<String, String> headers) {
        try {
            String key = headers.get("key");
            if (!userDAO.verifyKey(username, key)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            userDAO.attemptLogout(username);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(@RequestBody String username, @RequestHeader Map<String, String> headers) {
        try {
            String key = headers.get("key");
            if (!userDAO.verifyKey(username, key)) {
                return new ResponseEntity<>("Invalid API key.", HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>("API key is valid.", HttpStatus.OK);

        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/info")
    public ResponseEntity<User> getInfo(@RequestBody String username, @RequestHeader Map<String, String> headers) {
        try {
            String key = headers.get("key");
            if (!userDAO.verifyKey(username, key)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            User user = userDAO.getUserByUsername(username);
            User user_copy = new User(user.getId(), user.getUsername(), "");
            return new ResponseEntity<>(user_copy, HttpStatus.OK);

        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
