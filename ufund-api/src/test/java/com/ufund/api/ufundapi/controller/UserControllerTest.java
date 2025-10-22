package com.ufund.api.ufundapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.persistence.UserDAO;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Test the User Controller class
 * 
 * @author Matthew Beicke
 */
@Tag("Controller-tier")
public class UserControllerTest {
    private UserController userController;
    private UserDAO mockUserDAO;
    private CupboardDAO mockCupboardDAO;

    /**
     * Before each test, create new UserController and CupboardController objects
     * and inject
     * a mock User DAO
     */
    @BeforeEach
    public void setupUserController() {
        mockUserDAO = mock(UserDAO.class);
        mockCupboardDAO = mock(CupboardDAO.class);
        userController = new UserController(mockUserDAO, mockCupboardDAO);
    }

    @Test
    public void testCreateUser() throws IOException { // createUser may throw IOException
        // Setup
        User user = new User(16, "uname", "pword");
        // when createUser is called, return true simulating successful
        // creation and save
        when(mockUserDAO.createUser(user)).thenReturn(user);

        // Invoke
        ResponseEntity<User> response = userController.createUser(user);

        // Analyze
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testCreateUserBadRequest1() throws IOException { // createUser may throw IOException
        // Setup
        User user = new User(16, "uname", "");
        // when createUser is called, return true simulating successful
        // creation and save
        when(mockUserDAO.createUser(user)).thenReturn(user);

        // Invoke
        ResponseEntity<User> response = userController.createUser(user);

        // Analyze
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateUserBadRequest2() throws IOException { // createUser may throw IOException
        // Setup
        User user = new User(16, "", "pword");
        // when createUser is called, return true simulating successful
        // creation and save
        when(mockUserDAO.createUser(user)).thenReturn(user);

        // Invoke
        ResponseEntity<User> response = userController.createUser(user);

        // Analyze
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateUserFailed() throws IOException { // createUser may throw IOException
        // Setup
        User user = new User(16, "uname", "pword");
        // when createUser is called, return false simulating failed
        // creation and save
        when(mockUserDAO.createUser(user)).thenReturn(null);

        // Invoke
        ResponseEntity<User> response = userController.createUser(user);

        // Analyze
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testCreateUserHandleException() throws IOException { // createUser may throw IOException
        // Setup
        User user = new User(16, "uname", "pword");

        // When createUser is called on the Mock User DAO, throw an IOException
        doThrow(new IOException()).when(mockUserDAO).createUser(user);

        // Invoke
        ResponseEntity<User> response = userController.createUser(user);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testDeleteUser() throws IOException { // deleteUser may throw IOException
        // Setup
        int userId = 99;
        // when deleteUser is called return true, simulating successful deletion
        when(mockUserDAO.deleteUser(userId)).thenReturn(true);
        when(mockUserDAO.verifyKey(userId, "valid")).thenReturn(true);
        when(mockUserDAO.getUser(userId)).thenReturn(new User(-1, "", ""));

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");

        // Invoke
        ResponseEntity<Void> response = userController.deleteUser(userId, header);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteUserNotFound1() throws IOException { // deleteUser may throw IOException
        // Setup
        int userId = 99;
        // when deleteUser is called return false, simulating failed deletion
        when(mockUserDAO.deleteUser(userId)).thenReturn(false);
        when(mockUserDAO.getUser(userId)).thenReturn(null);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "unnecessary");

        // Invoke
        ResponseEntity<Void> response = userController.deleteUser(userId, header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteUserNotFound2() throws IOException { // deleteUser may throw IOException
        // Setup
        User user = new User(99, "uname", "pword");
        // when deleteUser is called return false, simulating failed deletion
        when(mockUserDAO.deleteUser(99)).thenReturn(false);
        when(mockUserDAO.getUser(99)).thenReturn(user);
        when(mockUserDAO.verifyKey(99, "unnecessary")).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "unnecessary");

        // Invoke
        ResponseEntity<Void> response = userController.deleteUser(99, header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteUserForbidden() throws IOException { // deleteUser may throw IOException
        // Setup
        User user = new User(99, "uname", "pword");
        // when deleteUser is called return false, simulating failed deletion
        when(mockUserDAO.deleteUser(99)).thenReturn(false);
        when(mockUserDAO.getUser(99)).thenReturn(user);
        when(mockUserDAO.verifyKey(99, "unnecessary")).thenReturn(true);
        when(mockUserDAO.userIsManager(99)).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "unnecessary");

        // Invoke
        ResponseEntity<Void> response = userController.deleteUser(99, header);

        // Analyze
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testDeleteUserHandleException() throws IOException { // deleteUser may throw IOException
        // Setup
        int userId = 99;
        // When deleteUser is called on the Mock User DAO, throw an IOException
        doThrow(new IOException()).when(mockUserDAO).deleteUser(userId);
        when(mockUserDAO.getUser(userId)).thenReturn(new User(-1, "", ""));
        when(mockUserDAO.verifyKey(userId, "valid")).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");

        // Invoke
        ResponseEntity<Void> response = userController.deleteUser(userId, header);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testDeleteUserNoAuth() throws IOException { // deleteUser may throw IOException
        // Setup
        int userId = 99;
        // when deleteUser is called return true, simulating successful deletion
        when(mockUserDAO.deleteUser(userId)).thenReturn(true);
        when(mockUserDAO.getUser(userId)).thenReturn(new User(-1, "", ""));
        when(mockUserDAO.verifyKey(userId, "invalid")).thenReturn(false);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "invalid");

        // Invoke
        ResponseEntity<Void> response = userController.deleteUser(userId, header);

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetNeed() throws IOException { // getNeed may throw IOException
        // Setup
        Need need = new Need("Water Bottles", 99, "plastic, fiji if possible");
        // When the same id is passed in, our mock Need DAO will return the Need object
        when(mockCupboardDAO.getNeed(need.getId())).thenReturn(need);

        // Invoke
        ResponseEntity<Need> response = userController.getNeed(need.getId());

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(need, response.getBody());
    }

    @Test
    public void testGetNeedNotFound() throws Exception { // getNeed may throw IOException
        // Setup
        int needId = 99;
        // When the same id is passed in, our mock Need DAO will return null, simulating
        // no need found
        when(mockCupboardDAO.getNeed(needId)).thenReturn(null);

        // Invoke
        ResponseEntity<Need> response = userController.getNeed(needId);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetNeedHandleException() throws Exception { // getNeed may throw IOException
        // Setup
        int needId = 99;
        // When getNeed is called on the Mock Need DAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).getNeed(needId);

        // Invoke
        ResponseEntity<Need> response = userController.getNeed(needId);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testUpdateUser() throws IOException { // updateUser may throw IOException
        // Setup
        int userId = 99;
        User user = new User(userId, "uname", "pword");
        // when updateUser is called, return true simulating successful
        // update and save
        when(mockUserDAO.updateUser(user)).thenReturn(user);
        when(mockUserDAO.getUser(userId)).thenReturn(new User(-1, "", ""));
        when(mockUserDAO.verifyKey(99, "valid")).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");

        ResponseEntity<User> response = userController.updateUser(user, header);
        user.updateUser("Soup", null);

        // Invoke
        response = userController.updateUser(user, header);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testUpdateUserConflict() throws IOException { // updateUser may throw IOException
        // Setup
        int userId = 99;
        User user = new User(userId, "uname", "pword");
        // when updateUser is called, return true simulating successful
        // update and save
        when(mockUserDAO.updateUser(user)).thenReturn(null);
        when(mockUserDAO.getUser(userId)).thenReturn(new User(-1, "", ""));
        when(mockUserDAO.verifyKey(99, "valid")).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");

        ResponseEntity<User> response = userController.updateUser(user, header);
        user.updateUser("Soup", null);

        // Invoke
        response = userController.updateUser(user, header);

        // Analyze
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testUpdateUserFailed() throws IOException { // updateUser may throw IOException
        // Setup
        int userId = 99;
        User user = new User(userId, "uname", "pword");
        // when updateUser is called, return null simulating non existant User
        // update and save
        when(mockUserDAO.updateUser(user)).thenReturn(null);
        when(mockUserDAO.getUser(userId)).thenReturn(null);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "unnecessary");

        // Invoke
        ResponseEntity<User> response = userController.updateUser(user, header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateNeedHandleException() throws IOException { // updateUser may throw IOException
        // Setup
        int userId = 99;
        User user = new User(userId, "uname", "pword");
        // When updateUser is called on the Mock User DAO, throw an IOException
        doThrow(new IOException()).when(mockUserDAO).updateUser(user);
        when(mockUserDAO.getUser(userId)).thenReturn(new User(-1, "", ""));
        when(mockUserDAO.verifyKey(userId, "valid")).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");

        // Invoke
        ResponseEntity<User> response = userController.updateUser(user, header);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testUpdateUserNoAuth() throws IOException { // updateUser may throw IOException
        // Setup
        int userId = 99;
        User user = new User(userId, "uname", "pword");
        // when updateUser is called, return true simulating successful
        // update and save
        when(mockUserDAO.updateUser(user)).thenReturn(user);
        when(mockUserDAO.getUser(userId)).thenReturn(new User(-1, "", ""));
        when(mockUserDAO.verifyKey(userId, "invalid")).thenReturn(false);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "invalid");

        ResponseEntity<User> response = userController.updateUser(user, header);
        user.updateUser("Soup", null);

        // Invoke
        response = userController.updateUser(user, header);

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testAddNeedToBasket() throws IOException { // addNeedToBasket may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        User user2 = new User(99, "uname2", "pword");
        user2.addToBasket(needId);

        // when addNeedToBasket is called, return a Need object simulating successful
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(new Need("name", needId, "desc"));
        when(mockUserDAO.addToBasket(user, needId)).thenReturn(user2);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, Integer> map = new HashMap<>();
        map.put("userID", user.getId());
        map.put("needID", needId);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<User> response = userController.addNeedToBasket(map, header);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testAddNeedToBasketForbidden() throws IOException { // addNeedToBasket may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        User user2 = new User(99, "uname2", "pword");
        user2.addToBasket(needId);

        // when addNeedToBasket is called, return a Need object simulating successful
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(new Need("name", needId, "desc"));
        when(mockUserDAO.addToBasket(user, needId)).thenReturn(user2);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);
        when(mockUserDAO.userIsManager(99)).thenReturn(true);

        // Invoke
        HashMap<String, Integer> map = new HashMap<>();
        map.put("userID", user.getId());
        map.put("needID", needId);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<User> response = userController.addNeedToBasket(map, header);

        // Analyze
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testAddNeedToBasketNotFound1() throws IOException { // addNeedToBasket may throw IOException
        // Setup
        int needId = 10;
        int needId2 = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId2);

        // when addNeedToBasket is called, return a Need object simulating successful
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(new Need("name", needId, "desc"));
        when(mockUserDAO.addToBasket(user, needId)).thenReturn(null);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);
        when(mockUserDAO.userIsManager(99)).thenReturn(false);

        // Invoke
        HashMap<String, Integer> map = new HashMap<>();
        map.put("userID", user.getId());
        map.put("needID", needId);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<User> response = userController.addNeedToBasket(map, header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddNeedToBasketNotFound2() throws IOException { // addNeedToBasket may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        User user2 = new User(99, "uname2", "pword");
        user2.addToBasket(needId);

        // when addNeedToBasket is called, return a Need object simulating successful
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(null);
        when(mockUserDAO.addToBasket(user, needId)).thenReturn(user2);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);
        when(mockUserDAO.userIsManager(99)).thenReturn(false);

        // Invoke
        HashMap<String, Integer> map = new HashMap<>();
        map.put("userID", user.getId());
        map.put("needID", needId);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<User> response = userController.addNeedToBasket(map, header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddNeedToBasketNoAuth() throws IOException { // addNeedToBasket may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        User user2 = new User(99, "uname2", "pword");
        user2.addToBasket(needId);

        // when addNeedToBasket is called, return a Need object simulating successful
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(new Need("name", needId, "desc"));
        when(mockUserDAO.addToBasket(user, needId)).thenReturn(user2);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);

        // Invoke
        HashMap<String, Integer> map = new HashMap<>();
        map.put("userID", user.getId());
        map.put("needID", needId);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "INVALID KEY");
        ResponseEntity<User> response = userController.addNeedToBasket(map, header);

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testAddNeedToBasketFailed() throws IOException { // addNeedToBasket may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        User user2 = new User(99, "uname2", "pword");
        user2.addToBasket(needId);

        // when addNeedToBasket is called, return null simulating failure
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(null);
        when(mockUserDAO.addToBasket(user, needId)).thenReturn(user2);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, Integer> map = new HashMap<>();
        map.put("userID", user.getId());
        map.put("needID", needId);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<User> response = userController.addNeedToBasket(map, header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddNeedToBasketHandleException() throws IOException { // addNeedToBasket may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        User user2 = new User(99, "uname2", "pword");
        user2.addToBasket(needId);

        // when addNeedToBasket is called on the Mock User DAO, throw an IOException
        // update and save
        doThrow(new IOException()).when(mockUserDAO).addToBasket(user, needId);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);

        when(mockCupboardDAO.getNeed(needId)).thenReturn(new Need("name", needId, "desc"));
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, Integer> map = new HashMap<>();
        map.put("userID", user.getId());
        map.put("needID", needId);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<User> response = userController.addNeedToBasket(map, header);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testRemoveNeedFromBasket() throws IOException { // removeNeedFromBasket may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);
        User user2 = new User(99, "uname2", "pword");

        // when removeNeedFromBasket is called, return a Need object simulating success
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(new Need("name", needId, "desc"));
        when(mockUserDAO.removeFromBasket(user, needId)).thenReturn(user2);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, Integer> map = new HashMap<>();
        map.put("userID", user.getId());
        map.put("needID", needId);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<User> response = userController.removeNeedFromBasket(map, header);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testRemoveNeedFromBasketForbidden() throws IOException { // removeNeedFromBasket may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);
        User user2 = new User(99, "uname2", "pword");

        // when removeNeedFromBasket is called, return a Need object simulating success
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(new Need("name", needId, "desc"));
        when(mockUserDAO.removeFromBasket(user, needId)).thenReturn(user2);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);
        when(mockUserDAO.userIsManager(99)).thenReturn(true);

        // Invoke
        HashMap<String, Integer> map = new HashMap<>();
        map.put("userID", user.getId());
        map.put("needID", needId);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<User> response = userController.removeNeedFromBasket(map, header);

        // Analyze
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testRemoveNeedFromBasketNotFound1() throws IOException { // removeNeedFromBasket may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);
        User user2 = new User(99, "uname2", "pword");

        // when removeNeedFromBasket is called, return a Need object simulating success
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(null);
        when(mockUserDAO.removeFromBasket(user, needId)).thenReturn(user2);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);
        when(mockUserDAO.userIsManager(99)).thenReturn(false);

        // Invoke
        HashMap<String, Integer> map = new HashMap<>();
        map.put("userID", user.getId());
        map.put("needID", needId);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<User> response = userController.removeNeedFromBasket(map, header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveNeedFromBasketNotFound2() throws IOException { // removeNeedFromBasket may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);

        // when removeNeedFromBasket is called, return a Need object simulating success
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(new Need("name", needId, "desc"));
        when(mockUserDAO.removeFromBasket(user, needId)).thenReturn(null);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);
        when(mockUserDAO.userIsManager(99)).thenReturn(false);

        // Invoke
        HashMap<String, Integer> map = new HashMap<>();
        map.put("userID", user.getId());
        map.put("needID", needId);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<User> response = userController.removeNeedFromBasket(map, header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveNeedFromBasketNoAuth() throws IOException { // removeNeedFromBasket may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);
        User user2 = new User(99, "uname2", "pword");

        // when removeNeedFromBasket is called, return a Need object simulating success
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(new Need("name", needId, "desc"));
        when(mockUserDAO.removeFromBasket(user, needId)).thenReturn(user2);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);

        // Invoke
        HashMap<String, Integer> map = new HashMap<>();
        map.put("userID", user.getId());
        map.put("needID", needId);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "INVALID KEY");
        ResponseEntity<User> response = userController.removeNeedFromBasket(map, header);

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testRemoveNeedFromBasketFailed() throws IOException { // removeNeedFromBasket may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);
        User user2 = new User(99, "uname2", "pword");

        // when removeNeedFromBasket is called, return null simulating failure
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(null);
        when(mockUserDAO.removeFromBasket(user, needId)).thenReturn(user2);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, Integer> map = new HashMap<>();
        map.put("userID", user.getId());
        map.put("needID", needId);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<User> response = userController.removeNeedFromBasket(map, header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveNeedFromBasketHandleException() throws IOException { // removeNeedFromBasket may throw
                                                                               // IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        User user2 = new User(99, "uname2", "pword");
        user2.addToBasket(needId);

        // when removeNeedFromBasket is called on the Mock User DAO, throw an
        // IOException
        // update and save
        doThrow(new IOException()).when(mockUserDAO).removeFromBasket(user, needId);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);

        when(mockCupboardDAO.getNeed(needId)).thenReturn(new Need("name", needId, "desc"));

        // Invoke
        HashMap<String, Integer> map = new HashMap<>();
        map.put("userID", user.getId());
        map.put("needID", needId);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<User> response = userController.removeNeedFromBasket(map, header);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testCheckout() throws IOException { // checkout may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);

        // when checkout is called, return true simulating successful
        // update and save
        when(mockUserDAO.checkout(user)).thenReturn(true);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        // Invoke
        ResponseEntity<User> response = userController.checkout(user.getId(), header);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testCheckoutForbidden() throws IOException { // checkout may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);

        // when checkout is called, return true simulating successful
        // update and save
        when(mockUserDAO.checkout(user)).thenReturn(true);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);
        when(mockUserDAO.userIsManager(99)).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        // Invoke
        ResponseEntity<User> response = userController.checkout(user.getId(), header);

        // Analyze
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testCheckoutNotFound() throws IOException { // checkout may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);

        // when checkout is called, return true simulating successful
        // update and save
        when(mockUserDAO.checkout(user)).thenReturn(false);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);
        when(mockUserDAO.userIsManager(99)).thenReturn(false);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        // Invoke
        ResponseEntity<User> response = userController.checkout(user.getId(), header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCheckoutNoAuth() throws IOException { // checkout may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);

        // when checkout is called, return true simulating successful
        // update and save
        when(mockUserDAO.checkout(user)).thenReturn(true);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "INVALID KEY");
        // Invoke
        ResponseEntity<User> response = userController.checkout(user.getId(), header);

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testCheckoutFailed() throws IOException { // checkout may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);

        // when checkout is called, return false simulating failure
        // update and save
        when(mockUserDAO.checkout(user)).thenReturn(false);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        // Invoke
        ResponseEntity<User> response = userController.checkout(user.getId(), header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCheckoutHandleException() throws IOException { // checkout may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);

        // when checkout is called on the Mock User DAO, throw an IOException
        // update and save
        doThrow(new IOException()).when(mockUserDAO).checkout(user);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        // Invoke
        ResponseEntity<User> response = userController.checkout(user.getId(), header);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testViewBasket() throws IOException { // viewBasket may throw IOException
        // Setup
        User user = new User(99, "uname", "pword");

        // when viewBasket is called, return an ArrayList simulating success
        // update and save
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.viewBasket(user)).thenReturn(new ArrayList<>());
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        // Invoke
        ResponseEntity<ArrayList<Need>> response = userController.viewBasket(user.getId(), header);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new ArrayList<>(), response.getBody());
    }

    @Test
    public void testViewBasketForbidden() throws IOException { // viewBasket may throw IOException
        // Setup
        User user = new User(99, "uname", "pword");

        // when viewBasket is called, return an ArrayList simulating success
        // update and save
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.viewBasket(user)).thenReturn(new ArrayList<>());
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);
        when(mockUserDAO.userIsManager(user.getId())).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        // Invoke
        ResponseEntity<ArrayList<Need>> response = userController.viewBasket(user.getId(), header);

        // Analyze
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testViewBasketNoAuth() throws IOException { // viewBasket may throw IOException
        // Setup
        User user = new User(99, "uname", "pword");

        // when viewBasket is called, return an ArrayList simulating success
        // update and save
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.viewBasket(user)).thenReturn(new ArrayList<>());

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "INVALID KEY");
        // Invoke
        ResponseEntity<ArrayList<Need>> response = userController.viewBasket(user.getId(), header);

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testViewBasketFailed() throws IOException { // viewBasket may throw IOException
        // Setup
        User user = new User(99, "uname", "pword");

        // when viewBasket is called, return null simulating failure (empty basket)
        // update and save
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.viewBasket(user)).thenReturn(null);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        // Invoke
        ResponseEntity<ArrayList<Need>> response = userController.viewBasket(user.getId(), header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testViewBasketHandleException() throws IOException { // viewBasket may throw IOException
        // Setup
        User user = new User(99, "uname", "pword");

        // when viewBasket is called on the Mock User DAO, throw an IOException
        // update and save
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.verifyKey(user.getId(), "valid")).thenReturn(true);
        doThrow(new IOException()).when(mockUserDAO).viewBasket(user);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        // Invoke
        ResponseEntity<ArrayList<Need>> response = userController.viewBasket(user.getId(), header);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}