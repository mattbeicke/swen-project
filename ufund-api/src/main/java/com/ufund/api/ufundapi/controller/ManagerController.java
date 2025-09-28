package com.ufund.api.ufundapi.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.persistence.ManagerDAO;

/**
 * Handles the REST API requests for the Manager resource
 * <p>
 * {@literal @}RestController Spring annotation identifies this class as a REST
 * API method handler to the Spring framework
 * 
 * @author Ricardo Lopez
 */

@RestController
@RequestMapping("manager")
public class ManagerController {
    private static final Logger LOG = Logger.getLogger(ManagerController.class.getName());
    private CupboardDAO cupboardDAO;

    /**
     * Responds to the GET request for a {@linkplain Need need} for the given id
     * 
     * @param needID The id used to locate the {@link Need need}
     * 
     * @return ResponseEntity with {@link Need need} object and HTTP status of OK if
     *         found<br>
     *         ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/{needID}")
    public ResponseEntity<Need> viewDetails(@PathVariable int needID){

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
}