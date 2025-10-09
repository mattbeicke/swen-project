package com.ufund.api.ufundapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ufund.api.ufundapi.model.User;
import com.ufund.api.ufundapi.persistence.UserDAO;

/**
 * Test the Manager Controller class
 * 
 * @author Ricardo Lopez
 */
@Tag("Controller-tier")
public class AccountControllerTest {
    private AccountController accountController;
    private UserDAO mockUserDAO;

    /**
     * Before each test, create a new CupboardController object and inject
     * a mock Need DAO
     */
    @BeforeEach
    public void setupAccountController() {
        mockUserDAO = mock(UserDAO.class);
        accountController = new AccountController(mockUserDAO);
    }

    @Test
    public void testValidLogin() throws IOException {
        String username = "user1";
        String password = "password";
        when(mockUserDAO.verifyLogin(username, password)).thenReturn(true);
        HashMap<String, String> details = new HashMap<>();
        details.put("username", username);
        details.put("password", password);

        ResponseEntity<String> response = accountController.login(details);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testInvalidLogin() throws IOException {
        String username = "user1";
        String password = "password";
        when(mockUserDAO.verifyLogin(username, password)).thenReturn(false);
        HashMap<String, String> details = new HashMap<>();
        details.put("username", username);
        details.put("password", password);

        ResponseEntity<String> response = accountController.login(details);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testLoginHandleException() throws IOException {
        String username = "user1";
        String password = "password";
        doThrow(new IOException()).when(mockUserDAO).verifyLogin(username, password);
        HashMap<String, String> details = new HashMap<>();
        details.put("username", username);
        details.put("password", password);

        ResponseEntity<String> response = accountController.login(details);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testValidLogout() throws IOException {
        String username = "user1";
        String key = "key";
        when(mockUserDAO.verifyKey(username, key)).thenReturn(true);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("key", key);

        ResponseEntity<Void> response = accountController.logout(username, headers);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testValidLogoutNoAuth() throws IOException {
        String username = "user1";
        String key = "key";
        when(mockUserDAO.verifyKey(username, key)).thenReturn(false);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("key", key);

        ResponseEntity<Void> response = accountController.logout(username, headers);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testLogoutHandleException() throws IOException {
        String username = "user1";
        doThrow(new IOException()).when(mockUserDAO).attemptLogout(username);
        String key = "irrelevant";
        when(mockUserDAO.verifyKey(username, key)).thenReturn(true);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("key", key);

        ResponseEntity<Void> response = accountController.logout(username, headers);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testTestValidKey() throws IOException {
        String username = "user1";
        String key = "key";
        when(mockUserDAO.verifyKey(username, key)).thenReturn(true);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("key", key);

        ResponseEntity<String> response = accountController.test(username, headers);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testTestInvalidKey() throws IOException {
        String username = "user1";
        String key = "key";
        when(mockUserDAO.verifyKey(username, key)).thenReturn(false);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("key", key);

        ResponseEntity<String> response = accountController.test(username, headers);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testTestHandleException() throws IOException {
        String username = "user1";
        String key = "key";
        doThrow(new IOException()).when(mockUserDAO).verifyKey(username, key);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("key", key);

        ResponseEntity<String> response = accountController.test(username, headers);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testGetInfo() throws IOException {
        String username = "user1";
        String key = "key";
        int id = 1;
        when(mockUserDAO.verifyKey(username, key)).thenReturn(true);
        when(mockUserDAO.getUserByUsername(username)).thenReturn(new User(id, username, ""));
        HashMap<String, String> headers = new HashMap<>();
        headers.put("key", key);

        ResponseEntity<User> response = accountController.getInfo(username, headers);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        assertEquals(username, response.getBody().getUsername());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetInfoNoAuth() throws IOException {
        // This also happens when the given user does not exist.
        String username = "user1";
        String key = "key";
        when(mockUserDAO.verifyKey(username, key)).thenReturn(false);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("key", key);

        ResponseEntity<User> response = accountController.getInfo(username, headers);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetInfoHandleException() throws IOException {
        String username = "user1";
        String key = "key";
        doThrow(new IOException()).when(mockUserDAO).getUserByUsername(username);
        when(mockUserDAO.verifyKey(username, key)).thenReturn(true);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("key", key);

        ResponseEntity<User> response = accountController.getInfo(username, headers);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
