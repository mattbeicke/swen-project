package com.ufund.api.ufundapi.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private CupboardDAO cupboardDAO;
    private CompletedNeedDAO completedNeedDAO;
    private UserDAO userDAO;

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
     * @param development_mode true if developer tasks should be enabled, false if
     *                         it should return a FORBIDDEN error code instead
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
            return new ResponseEntity<Need[]>(cupboardDAO.searchNeeds(name), HttpStatus.OK);
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
     *         Only returns HTTP status of FORBIDDEN when not in development mode.
     */
    @PostMapping("")
    public ResponseEntity<Need> createNeed(@RequestBody Need need) {
        LOG.info("POST /cupboard " + need);

        try {
            Need new_need = cupboardDAO.createNeed(need);
            if (new_need == null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<Need>(need, HttpStatus.CREATED);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the {@link Need need} with the provided {@link Need need} object, if
     * it exists
     * 
     * @param need The {@link Need need} to update
     * 
     * @return ResponseEntity with updated {@link Need need} object and HTTP status
     *         of OK if updated. ResponseEntity with HTTP status of NOT_FOUND if not
     *         found. ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR
     *         otherwise.
     *         Only returns HTTP status of FORBIDDEN when not in development mode.
     */
    @PutMapping("")
    public ResponseEntity<Need> updateNeed(@RequestBody Need need) {
        LOG.info("PUT /cupboard " + need);

        try {
            Need update = cupboardDAO.updateNeed(need);
            if (update == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(update, HttpStatus.OK);
        } catch (IOException e) {
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
     *         Only returns HTTP status of FORBIDDEN when not in development mode.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Need> deleteNeeds(@PathVariable int id) {
        LOG.info("DELETE /cupboard/" + id);

        try {
            boolean deleted = cupboardDAO.deleteNeed(id);
            if (!deleted) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
                User contributor = userDAO.getUser(comp.getContributorID());
                if (contributor == null) {
                    comp.setContributorUsername("Deleted Account");
                } else if (false) { // TODO: Security option to hide your account
                    comp.setContributorUsername("Private Account");
                } else {
                    comp.setContributorUsername(contributor.getUsername());
                }
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
        LOG.info("GET /completed/" + page);
        if (page <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            // [0, PAGE_SIZE) most recent on page 1
            CompletedNeed[] completedNeeds = completedNeedDAO.getRecentNeeds(PAGE_SIZE, (page - 1) * PAGE_SIZE);
            for (CompletedNeed comp : completedNeeds) {
                User contributor = userDAO.getUser(comp.getContributorID());
                if (contributor == null) {
                    comp.setContributorUsername("Deleted Account");
                } else if (false) { // TODO: Security option to hide your account
                    comp.setContributorUsername("Private Account");
                } else {
                    comp.setContributorUsername(contributor.getUsername());
                }
            } // update each of the usernames to their current ones
            return new ResponseEntity<>(completedNeeds, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
