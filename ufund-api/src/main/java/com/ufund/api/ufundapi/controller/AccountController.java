package com.ufund.api.ufundapi.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.html.HTML;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;
import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.persistence.UserDAO;

@RestController
@RequestMapping("accounts")
public class AccountController {
    private static final Logger LOG = Logger.getLogger(CupboardController.class.getName());
    private UserDAO userDAO;

    public AccountController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    private String generateKey() {
        Random rand = new Random();
        int myRandomNumber = rand.nextInt(0x10) + 0x10;
        return Integer.toHexString(myRandomNumber);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody String username, @RequestBody String password) {
        try {
            boolean validLogin = userDAO.verifyLogin(username, password);
            if(!validLogin) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            String new_key = generateKey();
            return new ResponseEntity<>(new_key, HttpStatus.OK);

        } catch(IOException e) {
            LOG.log(Level.SEVERE,e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
