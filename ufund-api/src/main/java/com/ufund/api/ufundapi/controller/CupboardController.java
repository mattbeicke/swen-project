package com.ufund.api.ufundapi.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;
import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.persistence.UserDAO;
import com.ufund.api.ufundapi.model.CompletedNeed;
import com.ufund.api.ufundapi.persistence.CompletedNeedDAO;

/**
 * Handles the REST API requests for the Cupboard resource
 * 
 * @author Anthony Ficalora
 */

@RestController
@RequestMapping("cupboard")
public class CupboardController {
    private static final Logger LOG = Logger.getLogger(CupboardController.class.getName());
    private final CupboardDAO cupboardDAO;
    private final CompletedNeedDAO completedNeedDAO;
    private final UserDAO userDAO;

    private static final int PAGE_SIZE = 30;

    /**
     * Creates a REST API controller to reponds to requests
     * 
     * @param cupboardDAO      The {@link CupboardDAO Cupboard Data Access Object}
     *                         to
     *                         perform CRUD operations
     * @param completedNeedDAO The {@link CompletedNeedDAO Completed Need Data
     *                         Access Object} to
     *                         perform CRUD operations
     * @param userDAO          The {@link UserDAO User Data Access Object} to
     *                         perform CRUD operations
     */
    public CupboardController(CupboardDAO cupboardDAO, CompletedNeedDAO completedNeedDAO, UserDAO userDAO) {
        this.cupboardDAO = cupboardDAO;
        this.completedNeedDAO = completedNeedDAO;
        this.userDAO = userDAO;
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
        LOG.info(() -> "GET /cupboard/" + id);
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
        LOG.info(() -> "GET /cupboard/?name=" + name);
        try {
            return new ResponseEntity<>(cupboardDAO.searchNeeds(name), HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the display name of a contributor. This is only used to display the
     * latest version of a name.
     * 
     * @param comp The CompletedNeed object to be updated.
     * @throws IOException
     */
    private void updateDisplayName(CompletedNeed comp) throws IOException {
        User contributor = userDAO.getUser(comp.getContributorID());
        if (contributor == null) {
            comp.setContributorUsername("Deleted Account");
        } else if (false) { // To be implemented soon: Security option to hide your account
            comp.setContributorUsername("Private Account");
        } else {
            comp.setContributorUsername(contributor.getUsername());
        }
    }

    /**
     * Responds to the GET request for all {@link CompletedNeed completed needs}
     *
     * @return ResponseEntity with array of {@link CompletedNeed completed need}
     *         objects (may be empty)
     *         and HTTP status of OK. ResponseEntity with HTTP status of
     *         INTERNAL_SERVER_ERROR otherwise.
     */
    @GetMapping("/completed")
    public ResponseEntity<CompletedNeed[]> getCompletedNeeds() {
        LOG.info("GET /completed");

        try {
            CompletedNeed[] completedNeeds = completedNeedDAO.getRecentNeeds();
            for (CompletedNeed comp : completedNeeds) {
                updateDisplayName(comp);
            } // update each of the usernames to their current ones
            return new ResponseEntity<>(completedNeeds, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to the GET request for one page of {@link CompletedNeed completed
     * needs}
     *
     * @return ResponseEntity with array of {@link CompletedNeed completed need}
     *         objects (may be empty)
     *         and HTTP status of OK. If requesting an invalid page, returns an HTTP
     *         status of BAD_REQUEST.
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise.
     */
    @GetMapping("/completed/{page}")
    public ResponseEntity<CompletedNeed[]> getCompletedNeedsPage(@PathVariable int page) {
        LOG.info(() -> "GET /completed/" + page);
        if (page <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            // [0, PAGE_SIZE) most recent on page 1
            CompletedNeed[] completedNeeds = completedNeedDAO.getRecentNeeds(PAGE_SIZE, (page - 1) * PAGE_SIZE);
            for (CompletedNeed comp : completedNeeds) {
                updateDisplayName(comp);
            } // update each of the usernames to their current ones
            return new ResponseEntity<>(completedNeeds, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/completed/numbers")
    public ResponseEntity<int[]> getNumbers() {
        try {
            return new ResponseEntity<>(completedNeedDAO.getNumbers(), HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
