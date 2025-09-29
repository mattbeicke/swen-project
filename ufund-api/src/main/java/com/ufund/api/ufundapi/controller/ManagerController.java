package com.ufund.api.ufundapi.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufund.api.ufundapi.model.Manager;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.persistence.CupboardDAO;

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

    /**
     * Creates a REST API controller to reponds to requests
     * 
     * @param cupboardDAO The {@link CupboardDAO Cupboard Data Access Object} to
     *                    perform CRUD operations
     */
    public ManagerController(CupboardDAO cupboardDAO) {
        this.cupboardDAO = cupboardDAO;
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
        LOG.info("GET /view/" + needID);
        try {
            Need need = cupboardDAO.getNeed(needID);
            if (need != null)
                return new ResponseEntity<Need>(need, HttpStatus.OK);
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
     * @param need The {@link Need need} to create
     * 
     * @return ResponseEntity with created {@link Need need} object and HTTP status
     *         of CREATED. ResponseEntity with HTTP status of CONFLICT if
     *         {@link Need need} object already exists. ResponseEntity with HTTP
     *         status of INTERNAL_SERVER_ERROR otherwise.
     */
    @PostMapping("/add/")
    public ResponseEntity<Need> addNeed(@RequestBody Need need) {
        LOG.info("POST /add" + need.getId());
        try {
            Need newNeed = cupboardDAO.createNeed(need);
            if (newNeed != null) {
                return new ResponseEntity<Need>(newNeed, HttpStatus.OK);
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
        LOG.info("GET /browse");
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
     * @param id The id of the {@link Need need} to deleted
     * 
     * @return ResponseEntity HTTP status of OK if deleted. ResponseEntity with HTTP
     *         status of NOT_FOUND if not found. ResponseEntity with HTTP status of
     *         INTERNAL_SERVER_ERROR otherwise.
     */
    @PostMapping("/delete/{id}")
    public ResponseEntity<Manager> deleteNeed(@PathVariable int id) {
        LOG.info("POST /delete/" + id);
        try {
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
     * @param need The {@link Need need} to edit
     * 
     * @return ResponseEntity with edited {@link Need need} object and HTTP status
     *         of OK if edited. ResponseEntity with HTTP status of NOT_FOUND if not
     *         found. ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR
     *         otherwise.
     */
    @PostMapping("/edit/{id}")
    public ResponseEntity<Need> editNeed(@PathVariable Need need) {
        LOG.info("POST /edit/" + need.getId());
        try {
            Need need2 = cupboardDAO.updateNeed(need);
            if (need2 != null) {
                return new ResponseEntity<Need>(need2, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}