package com.ufund.api.ufundapi.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
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
import com.ufund.api.ufundapi.persistence.CupboardDAO;

/**
 * Handles the REST API requests for the Cupboard resource
 * 
 * @author Anthony Ficalora
 */

@RestController
@RequestMapping("cupboard")
public class CupboardController {
    private static final Logger LOG = Logger.getLogger(CupboardController.class.getName());
    private CupboardDAO cupboardDAO;

    /**
     * Creates a REST API controller to reponds to requests
     * 
     * @param cupboardDAO The {@link CupboardDAO Cupboard Data Access Object} to
     *                    perform CRUD operations
     */
    public CupboardController(CupboardDAO cupboardDAO) {
        this.cupboardDAO = cupboardDAO;
    }

    /**
     * Responds to the GET request for a {@link Need need} for the given id
     * 
     * @param id The id used to locate the {@link Need need}
     * 
     * @return ResponseEntity with {@link Need need} object and HTTP status of OK if
     *         found. ResponseEntity with HTTP status of NOT_FOUND if not found.
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Need> getNeed(@PathVariable int id) {
        LOG.info("GET /cupboard/" + id);
        try {
            Need need = cupboardDAO.getNeed(id);
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
     * Responds to the GET request for all {@link Need needs}
     * 
     * @return ResponseEntity with array of {@link Need need} objects (may be empty)
     *         and HTTP status of OK. ResponseEntity with HTTP status of
     *         INTERNAL_SERVER_ERROR otherwise.
     */
    @GetMapping("")
    public ResponseEntity<Need[]> getNeeds() {
        LOG.info("GET /cupboard");
        try {
            return new ResponseEntity<>(cupboardDAO.getNeeds(), HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to the GET request for all {@link Need needs} whose name contains
     * the text in name
     * 
     * @param name The name parameter which contains the text used to find the
     *             {@link Need needes}
     * 
     * @return ResponseEntity with array of {@link Need need} objects (may be empty)
     *         and HTTP status of OK. ResponseEntity with HTTP status of
     *         INTERNAL_SERVER_ERROR otherwise.
     */
    @GetMapping("/")
    public ResponseEntity<Need[]> searchNeeds(@RequestParam String name) {
        LOG.info("GET /cupboard/?name=" + name);
        try {
            return new ResponseEntity<>(cupboardDAO.searchNeeds(name), HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
