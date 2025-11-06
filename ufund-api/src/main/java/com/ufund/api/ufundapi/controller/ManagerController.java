package com.ufund.api.ufundapi.controller;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;
import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.persistence.UserDAO;

/**
 * Handles the REST API requests for the Manager resource
 * 
 * @author Ricardo Lopez
 */
@RestController
@RequestMapping("manager")
public class ManagerController {
    private static final Logger LOG = Logger.getLogger(ManagerController.class.getName());
    private CupboardDAO cupboardDAO;
    private UserDAO userDAO;

    /**
     * Creates a REST API controller to reponds to requests
     * 
     * @param cupboardDAO The {@link CupboardDAO Cupboard Data Access Object} to
     *                    perform CRUD operations
     */
    public ManagerController(CupboardDAO cupboardDAO, UserDAO userDAO) {
        this.cupboardDAO = cupboardDAO;
        this.userDAO = userDAO;
    }

    /**
     * Responds to the GET request for a {@link Need need} for the given id
     * 
     * @param needID The id used to locate the {@link Need need}
     * 
     * @return ResponseEntity with {@link Need need} object and HTTP status of OK if
     *         found. ResponseEntity with HTTP status of NOT_FOUND if not found.
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise.
     */
    @GetMapping("/view/{needID}")
    public ResponseEntity<Need> viewDetails(@PathVariable int needID) {
        LOG.info(() -> "GET /view/" + needID);
        try {
            Need need = cupboardDAO.getNeed(needID);
            if (need != null)
                return new ResponseEntity<>(need, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a {@link Need need} with the provided {@link Need need} object
     * 
     * @param need    The {@link Need need} to create
     * @param headers Map of all headers, must include key
     * 
     * @return ResponseEntity with created {@link Need need} object and HTTP status
     *         of CREATED. ResponseEntity with HTTP status of CONFLICT if
     *         {@link Need need} object already exists. ResponseEntity with HTTP
     *         status
     *         of BAD_REQUEST if the need is invalid. ResponseEntity with HTTP
     *         status of INTERNAL_SERVER_ERROR otherwise.
     */
    @PostMapping("/add")
    public ResponseEntity<Need> addNeed(@RequestBody Need need, @RequestHeader Map<String, String> headers) {
        LOG.info(() -> "POST /add" + need.getId());
        try {
            String key = headers.get("key");
            if (!userDAO.verifyKey(User.MANAGER_USERNAME, key)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (need.getName().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Need newNeed = cupboardDAO.createNeed(need);
            if (newNeed != null) {
                return new ResponseEntity<>(newNeed, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to the GET request for all {@link Need needs}
     * 
     * @return ResponseEntity with array of {@link Need need} objects (may be empty)
     *         and HTTP status of OK. ResponseEntity with HTTP status of
     *         INTERNAL_SERVER_ERROR otherwise.
     */
    @GetMapping("/browse")
    public ResponseEntity<Need[]> browseNeeds() {
        LOG.info(() -> "GET /browse");
        try {
            Need[] needs = cupboardDAO.getNeeds();
            return new ResponseEntity<>(needs, HttpStatus.OK);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a {@link Need need} with the given id
     * 
     * @param id      The id of the {@link Need need} to deleted
     * @param headers Map of all headers, must include key
     * 
     * @return ResponseEntity HTTP status of OK if deleted. ResponseEntity with HTTP
     *         status of NOT_FOUND if not found. ResponseEntity with HTTP status of
     *         INTERNAL_SERVER_ERROR otherwise.
     */
    @PostMapping("/delete/{id}")
    public ResponseEntity<User> deleteNeed(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        LOG.info(() -> "POST /delete/" + id);
        try {
            String key = headers.get("key");
            if (!userDAO.verifyKey(User.MANAGER_USERNAME, key)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            boolean del = cupboardDAO.deleteNeed(id);
            if (del) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Edit the {@link Need need} with the provided {@link Need need} object, if
     * it exists
     * 
     * @param need    The {@link Need need} to edit
     * @param headers Map of all headers, must include key
     * 
     * @return ResponseEntity with edited {@link Need need} object and HTTP status
     *         of OK if edited. ResponseEntity with HTTP status of NOT_FOUND if not
     *         found. ResponseEntity with HTTP status of BAD_REQUEST if the need
     *         is invalid. ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR
     *         otherwise.
     */
    @PutMapping("/edit")
    public ResponseEntity<Need> editNeed(@RequestBody Need need, @RequestHeader Map<String, String> headers) {
        LOG.info(() -> "POST /edit/" + need.getId());
        try {
            String key = headers.get("key");
            if (!userDAO.verifyKey(User.MANAGER_USERNAME, key)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (need.getName().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Need need2 = cupboardDAO.updateNeed(need);
            if (need2 != null) {
                return new ResponseEntity<>(need2, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/toggle")
    public ResponseEntity<User> toggle(@RequestBody String username, @RequestHeader Map<String, String> headers) {
        try {
            String key = headers.get("key");
            if (!userDAO.verifyKey(User.MANAGER_USERNAME, key)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            User user = userDAO.getUserByUsername(username);
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            userDAO.toggleBan(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}