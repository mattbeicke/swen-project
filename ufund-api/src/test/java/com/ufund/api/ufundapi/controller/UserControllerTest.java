package com.ufund.api.ufundapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;

import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.persistence.UserDAO;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
     * Before each test, create a new CupboardController object and inject
     * a mock Need DAO
     */
    @BeforeEach
    public void setupUserController() {
        mockUserDAO = mock(UserDAO.class);
        mockCupboardDAO = mock(CupboardDAO.class);
        userController = new UserController(mockUserDAO, mockCupboardDAO);
    }

    @Test
    public void testCreateUser() throws IOException { // createNeed may throw IOException
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
    public void testCreateUserFailed() throws IOException { // createNeed may throw IOException
        // Setup
        User user = new User(16, "uname", "pword");
        // when createNeed is called, return false simulating failed
        // creation and save
        when(mockUserDAO.createUser(user)).thenReturn(null);

        // Invoke
        ResponseEntity<User> response = userController.createUser(user);

        // Analyze
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testCreateUserHandleException() throws IOException { // createNeed may throw IOException
        // Setup
        User user = new User(16, "uname", "pword");

        // When createNeed is called on the Mock Need DAO, throw an IOException
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
        // when deleteNeeds is called return true, simulating successful deletion
        when(mockUserDAO.deleteUser(userId)).thenReturn(true);

        // Invoke
        ResponseEntity<User> response = userController.deleteUser(userId);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteUserdNotFound() throws IOException { // deleteUser may throw IOException
        // Setup
        int userId = 99;
        // when deleteNeeds is called return false, simulating failed deletion
        when(mockUserDAO.deleteUser(userId)).thenReturn(false);

        // Invoke
        ResponseEntity<User> response = userController.deleteUser(userId);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteUserHandleException() throws IOException { // deleteUser may throw IOException
        // Setup
        int userId = 99;
        // When deleteNeeds is called on the Mock Need DAO, throw an IOException
        doThrow(new IOException()).when(mockUserDAO).deleteUser(userId);

        // Invoke
        ResponseEntity<User> response = userController.deleteUser(userId);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
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
    public void testGetNeedNotFound() throws Exception { // createNeed may throw IOException
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
    public void testGetNeedHandleException() throws Exception { // createNeed may throw IOException
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
        User user = new User(99, "uname", "pword");
        // when updateUser is called, return true simulating successful
        // update and save
        when(mockUserDAO.updateUser(user)).thenReturn(user);
        ResponseEntity<User> response = userController.updateUser(user);
        user.updateUser("Soup", null);

        // Invoke
        response = userController.updateUser(user);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testUpdateUserFailed() throws IOException { // updateUser may throw IOException
        // Setup
        User user = new User(99, "uname", "pword");
        // when updateUser is called, return true simulating successful
        // update and save
        when(mockUserDAO.updateUser(user)).thenReturn(null);

        // Invoke
        ResponseEntity<User> response = userController.updateUser(user);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateNeedHandleException() throws IOException { // updateUser may throw IOException
        // Setup
        User user = new User(99, "uname", "pword");
        // When updateUser is called on the Mock Need DAO, throw an IOException
        doThrow(new IOException()).when(mockUserDAO).updateUser(user);

        // Invoke
        ResponseEntity<User> response = userController.updateUser(user);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testAddNeedToBasket() throws IOException { // updateUser may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        User user2 = new User(99, "uname", "pword");
        user2.addToBasket(needId);

        // when updateUser is called, return true simulating successful
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(new Need("name", needId, "desc"));
        when(mockUserDAO.addToBasket(user, needId)).thenReturn(user2);

        // Invoke
        ResponseEntity<User> response = userController.addNeedToBasket(user, needId);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testAddNeedToBasketFailed() throws IOException { // updateUser may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        User user2 = new User(99, "uname", "pword");
        user2.addToBasket(needId);

        // when updateUser is called, return true simulating successful
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(null);
        when(mockUserDAO.addToBasket(user, needId)).thenReturn(user2);

        // Invoke
        ResponseEntity<User> response = userController.addNeedToBasket(user, needId);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddNeedToBasketHandleException() throws IOException { // updateUser may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        User user2 = new User(99, "uname", "pword");
        user2.addToBasket(needId);

        // when updateUser is called, return true simulating successful
        // update and save
        doThrow(new IOException()).when(mockUserDAO).addToBasket(user, needId);

        when(mockCupboardDAO.getNeed(needId)).thenReturn(new Need("name", needId, "desc"));

        // Invoke
        ResponseEntity<User> response = userController.addNeedToBasket(user, needId);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testRemoveNeedFromBasket() throws IOException { // updateUser may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);
        User user2 = new User(99, "uname", "pword");

        // when updateUser is called, return true simulating successful
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(new Need("name", needId, "desc"));
        when(mockUserDAO.removeFromBasket(user, needId)).thenReturn(user2);

        // Invoke
        ResponseEntity<User> response = userController.removeNeedFromBasket(user, needId);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testRemoveNeedFromBasketFailed() throws IOException { // updateUser may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);
        User user2 = new User(99, "uname", "pword");

        // when updateUser is called, return true simulating successful
        // update and save
        when(mockCupboardDAO.getNeed(needId)).thenReturn(null);
        when(mockUserDAO.removeFromBasket(user, needId)).thenReturn(user2);

        // Invoke
        ResponseEntity<User> response = userController.removeNeedFromBasket(user, needId);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveNeedFromBasketHandleException() throws IOException { // updateUser may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        User user2 = new User(99, "uname", "pword");
        user2.addToBasket(needId);

        // when updateUser is called, return true simulating successful
        // update and save
        doThrow(new IOException()).when(mockUserDAO).removeFromBasket(user, needId);

        when(mockCupboardDAO.getNeed(needId)).thenReturn(new Need("name", needId, "desc"));

        // Invoke
        ResponseEntity<User> response = userController.removeNeedFromBasket(user, needId);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testCheckout() throws IOException { // updateUser may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);

        // when updateUser is called, return true simulating successful
        // update and save
        when(mockUserDAO.checkout(user)).thenReturn(true);

        // Invoke
        ResponseEntity<User> response = userController.checkout(user);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testCheckoutFailed() throws IOException { // updateUser may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);

        // when updateUser is called, return true simulating successful
        // update and save
        when(mockUserDAO.checkout(user)).thenReturn(false);

        // Invoke
        ResponseEntity<User> response = userController.checkout(user);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCheckoutHandleException() throws IOException { // updateUser may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");
        user.addToBasket(needId);

        // when updateUser is called, return true simulating successful
        // update and save
        doThrow(new IOException()).when(mockUserDAO).checkout(user);

        // Invoke
        ResponseEntity<User> response = userController.checkout(user);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


    @Test
    public void testViewBasket() throws IOException { // updateUser may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");

        // when updateUser is called, return true simulating successful
        // update and save
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.viewBasket(user)).thenReturn(new ArrayList<>());

        // Invoke
        ResponseEntity<ArrayList<Integer>> response = userController.viewBasket(user.getId());

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new ArrayList<>(), response.getBody());
    }

    @Test
    public void testViewBasketFailed() throws IOException { // updateUser may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");

        // when updateUser is called, return true simulating successful
        // update and save
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.viewBasket(user)).thenReturn(null);

        // Invoke
        ResponseEntity<ArrayList<Integer>> response = userController.viewBasket(user.getId());

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testViewBasketHandleException() throws IOException { // updateUser may throw IOException
        // Setup
        int needId = 10;
        User user = new User(99, "uname", "pword");

        // when updateUser is called, return true simulating successful
        // update and save
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        doThrow(new IOException()).when(mockUserDAO).viewBasket(user);

        // Invoke
        ResponseEntity<ArrayList<Integer>> response = userController.viewBasket(user.getId());

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}