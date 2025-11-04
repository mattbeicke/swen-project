package com.ufund.api.ufundapi.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ufund.api.ufundapi.model.Manager;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;
import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.persistence.UserDAO;

/**
 * Handles the REST API requests for the User resource
 * 
 * @author Matthew Beicke
 */

@RestController
@RequestMapping("user")
public class UserController {
    private static final Logger LOG = Logger.getLogger(UserController.class.getName());
    private UserDAO userDAO;
    private CupboardDAO cupboardDAO;

    /**
     * Creates a REST API controller to reponds to requests
     * 
     * @param userDAO     The {@link userDAO User Data Access Object} to
     *                    perform CRUD operations
     * @param cupboardDAO The {@link CupboardDAO Cupboard Data Access Object} to
     *                    perform CRUD operations
     */
    public UserController(UserDAO userDAO, CupboardDAO cupboardDAO) {
        this.userDAO = userDAO;
        this.cupboardDAO = cupboardDAO;
    }

    /**
     * Updates a {@link User user} to add a need to their basket
     * 
     * @param data    Map containing data {needID: int, userID: int}
     * @param headers Map of all headers, must include key
     * 
     * @return ResponseEntity with updated {@link User user} object and HTTP status
     *         of OK. ResponseEntity with HTTP status of NOT_FOUND if {@link User
     *         user} object does not exist or {@link Need need} does not exist.
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise.
     */
    @PostMapping("basket/add")
    public ResponseEntity<User> addNeedToBasket(@RequestBody Map<String, Integer> data,
            @RequestHeader Map<String, String> headers) {
        LOG.info("POST /user/basket/add");

        try {
            int needID = data.get("needID");
            int userID = data.get("userID");
            User user = userDAO.getUser(userID);
            if (user == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            String key = headers.get("key");
            if (!userDAO.verifyKey(userID, key)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (userDAO.userIsManager(userID)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            if (cupboardDAO.getNeed(needID) != null) {
                User newuser = userDAO.addToBasket(user, needID);
                if (newuser == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                ArrayList<Integer> basket = newuser.getBasket();
                for (int need : basket) {
                    if (cupboardDAO.getNeed(need) == null) {
                        newuser.removeFromBasket(need);
                    }
                }
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates a {@link User user} to remove a {@link Need need} from their basket
     * 
     * @param data    Map containing data {needID: int, userID: int}
     * @param headers Map of all headers, must include key
     * 
     * @return ResponseEntity with updated {@link User user} object and HTTP status
     *         of OK. ResponseEntity with HTTP status of NOT_FOUND if {@link User
     *         user} object does not exist or {@link Need need} does not exist.
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise.
     */
    @PostMapping("basket/remove")
    public ResponseEntity<User> removeNeedFromBasket(@RequestBody Map<String, Integer> data,
            @RequestHeader Map<String, String> headers) {
        LOG.info("POST /user/basket/remove");

        try {
            int needID = data.get("needID");
            int userID = data.get("userID");

            User user = userDAO.getUser(userID);
            if (user == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            String key = headers.get("key");

            if (!userDAO.verifyKey(userID, key)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (userDAO.userIsManager(userID)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            if (cupboardDAO.getNeed(needID) != null) {

                User newuser = userDAO.removeFromBasket(user, needID);
                if (newuser == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                ArrayList<Integer> basket = newuser.getBasket();
                for (int need : basket) {
                    if (cupboardDAO.getNeed(need) == null) {
                        newuser.removeFromBasket(need);
                    }
                }
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Checks out a {@link User user}'s basket
     * 
     * @param id      The ID of the user to checkout
     * @param headers Map of all headers, must include key
     * 
     * @return ResponseEntity with updated {@link User user} object and HTTP status
     *         of OK. ResponseEntity with HTTP status of NOT_FOUND if {@link User
     *         user} object does not exist. ResponseEntity with HTTP status of
     *         INTERNAL_SERVER_ERROR otherwise.
     */
    @PostMapping("basket/checkout/{id}")
    public ResponseEntity<User> checkout(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        LOG.info("POST /user/basket/checkout " + id);

        try {
            User user = userDAO.getUser(id);

            if (user == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            String key = headers.get("key");
            if (!userDAO.verifyKey(id, key)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (userDAO.userIsManager(id)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            ArrayList<Integer> basket = user.getBasket();
            for (int need : basket) {
                cupboardDAO.deleteNeed(need);
            }
            if (userDAO.checkout(user)) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        LOG.info("GET /view/" + id);
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
     * Shows the list of all {@link Need needs} in a {@link User user's} basket
     * 
     * @param id      The ID of the user with needs to view
     * @param headers Map of all headers, must include key
     * 
     * @return ResponseEntity with list of {@link Need need} objects and HTTP status
     *         of OK. ResponseEntity with HTTP status of NOT_FOUND if {@link User
     *         user} object does not exist ResponseEntity with HTTP status of
     *         INTERNAL_SERVER_ERROR otherwise.
     */
    @GetMapping("/basket/{id}")
    public ResponseEntity<ArrayList<Need>> viewBasket(@PathVariable int id,
            @RequestHeader Map<String, String> headers) {
        LOG.info("GET /user/basket/" + id);

        try {
            User user = userDAO.getUser(id);
            ArrayList<Integer> oldBasket = userDAO.viewBasket(user);
            if (oldBasket == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            String key = headers.get("key");
            if (!userDAO.verifyKey(id, key)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (userDAO.userIsManager(id)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            ArrayList<Need> retBasket = new ArrayList<>();
            for (int i = 0; i < oldBasket.size(); i++) {
                if (cupboardDAO.getNeed(oldBasket.get(i)) == null) {
                    userDAO.removeFromBasket(user, oldBasket.get(i));
                } else {
                    retBasket.add(cupboardDAO.getNeed(oldBasket.get(i)));
                }
            }

            return new ResponseEntity<>(retBasket, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a {@link User user} with the provided {@link User user} object
     * 
     * @param user The {@link User user} to create
     * 
     * @return ResponseEntity with created {@link User user} object and HTTP status
     *         of CREATED. ResponseEntity with HTTP status of CONFLICT if
     *         {@link User user} object already exists. ResponseEntity with HTTP
     *         status of BAD_REQUEST if the user is invalid (i.e has an empty
     *         password.)
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise.
     */
    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        LOG.info("POST /user " + user);

        try {
            if (user.getPassword().isEmpty() || user.getUsername().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            User newuser = userDAO.createUser(user);
            if (newuser == null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(newuser, HttpStatus.CREATED);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the {@link User user} with the provided {@link User user} object, if
     * it exists
     * 
     * @param user    The {@link User user} to update
     * @param headers Map of all headers, must include key
     * 
     * @return ResponseEntity with updated {@link User user} object and HTTP status
     *         of OK if updated. ResponseEntity with HTTP status of NOT_FOUND if not
     *         found. ResponseEntity with HTTP status of UNAUTHORIZED
     *         if not logged in as the right user. ResponseEntity with HTTP
     *         status of BAD_REQUEST if the user is invalid (i.e has an empty
     *         password.)
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise.
     */
    @PutMapping("")
    public ResponseEntity<User> updateUser(@RequestBody User user,
            @RequestHeader Map<String, String> headers) {
        LOG.info("PUT /user " + user);

        try {
            if (userDAO.getUser(user.getId()) == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            String key = headers.get("key");
            if (!userDAO.verifyKey(user.getId(), key)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            User update = userDAO.updateUser(user);
            if (update == null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(update, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a {@link User user} with the given id
     * 
     * @param id      The id of the {@link User user} to deleted
     * @param headers Map of all headers, must include key
     * 
     * @return ResponseEntity HTTP status: OK if deleted. ResponseEntity with HTTP
     *         status of NOT_FOUND if not found. ResponseEntity with HTTP status of
     *         UNAUTHORIZED if not logged in as the right user. ResponseEntity of
     *         FORBIDDEN if
     *         deletion can not happen (i.e user is a Manager.)
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id,
            @RequestHeader Map<String, String> headers) {
        LOG.info("DELETE /user/" + id);

        try {
            if (userDAO.getUser(id) == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            String key = headers.get("key");
            if (!userDAO.verifyKey(id, key)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (userDAO.userIsManager(id)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            boolean deleted = userDAO.deleteUser(id);
            if (!deleted) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<User[]> getUsers(@RequestHeader Map<String, String> headers) {
        try {
            User user = userDAO.getUserByUsername(Manager.MANAGER_USERNAME);
            String key = headers.get("key");
            if (!userDAO.verifyKey(user.getId(), key)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            User[] users = userDAO.getUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    public ResponseEntity<User[]> searchUsers(@RequestParam String username,
            @RequestHeader Map<String, String> headers) {
        try {
            User user = userDAO.getUserByUsername(Manager.MANAGER_USERNAME);
            String key = headers.get("key");
            if (!userDAO.verifyKey(user.getId(), key)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            User[] users = userDAO.searchUsers(username);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
