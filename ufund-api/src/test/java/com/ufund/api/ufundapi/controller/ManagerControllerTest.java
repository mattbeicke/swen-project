package com.ufund.api.ufundapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import com.ufund.api.ufundapi.model.Manager;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;
import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.persistence.UserDAO;

/**
 * Test the Manager Controller class
 * 
 * @author Ricardo Lopez
 */
@Tag("Controller-tier")
class ManagerControllerTest {
    private ManagerController managerController;
    private CupboardDAO mockCupboardDAO;
    private UserDAO mockUserDAO;

    /**
     * Before each test, create a new CupboardController object and inject
     * a mock Need DAO
     */
    @BeforeEach
    void setupCupboardController() {
        mockCupboardDAO = mock(CupboardDAO.class);
        mockUserDAO = mock(UserDAO.class);
        managerController = new ManagerController(mockCupboardDAO, mockUserDAO);

    }

    @Test
    void testGetNeed() throws IOException { // getNeed may throw IOException
        // Setup
        Need need = new Need("Water Bottles", 99, "plastic, fiji if possible");
        // When the same id is passed in, our mock Need DAO will return the Need object
        when(mockCupboardDAO.getNeed(need.getId())).thenReturn(need);

        // Invoke
        ResponseEntity<Need> response = managerController.viewDetails(need.getId());

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(need, response.getBody());
    }

    @Test
    void testGetNeedNotFound() throws Exception { // createNeed may throw IOException
        // Setup
        int needId = 99;
        // When the same id is passed in, our mock Need DAO will return null, simulating
        // no need found
        when(mockCupboardDAO.getNeed(needId)).thenReturn(null);

        // Invoke
        ResponseEntity<Need> response = managerController.viewDetails(needId);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetNeedHandleException() throws Exception { // createNeed may throw IOException
        // Setup
        int needId = 99;
        // When getNeed is called on the Mock Need DAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).getNeed(needId);

        // Invoke
        ResponseEntity<Need> response = managerController.viewDetails(needId);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testAddNeed() throws IOException { // createNeed may throw IOException
        // Setup
        Need need = new Need("Cookies", 99, "chocolate chip");
        User user = new User(69, "admin", "pword", "", "", false);
        // when createNeed is called, return true simulating successful
        // creation and save
        when(mockCupboardDAO.createNeed(need)).thenReturn(need);
        when(mockUserDAO.verifyKey(user.getUsername(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<Need> response = managerController.addNeed(need, header);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(need, response.getBody());
    }

    @Test
    void testAddNeedInvalid() throws IOException { // createNeed may throw IOException
        // Setup
        Need need = new Need("", 99, "chocolate chip");
        User user = new User(69, "admin", "pword", "", "", false);
        // when createNeed is called, return true simulating successful
        // creation and save
        when(mockCupboardDAO.createNeed(need)).thenReturn(need);
        when(mockUserDAO.verifyKey(user.getUsername(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<Need> response = managerController.addNeed(need, header);

        // Analyze
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testAddNeedFailed() throws IOException { // createNeed may throw IOException
        // Setup
        Need need = new Need("Plates", 99, "paper");
        User user = new User(69, "admin", "pword", "", "", false);
        // when createNeed is called, return false simulating failed
        // creation and save
        when(mockCupboardDAO.createNeed(need)).thenReturn(null);
        when(mockUserDAO.verifyKey(user.getUsername(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<Need> response = managerController.addNeed(need, header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAddNeedHandleException() throws IOException { // createNeed may throw IOException
        // Setup
        Need need = new Need("Bread", 99, "Whole grain loaf");
        User user = new User(69, "admin", "pword", "", "", false);
        // When createNeed is called on the Mock Need DAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).createNeed(need);
        when(mockUserDAO.verifyKey(user.getUsername(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<Need> response = managerController.addNeed(need, header);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testAddNeedNoAuth() throws IOException { // createNeed may throw IOException
        // Setup
        Need need = new Need("Cookies", 99, "chocolate chip");
        User user = new User(69, "admin", "pword", "", "", false);
        // when createNeed is called, return true simulating successful
        // creation and save
        when(mockCupboardDAO.createNeed(need)).thenReturn(need);
        when(mockUserDAO.verifyKey(user.getUsername(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "INVALID KEY");
        ResponseEntity<Need> response = managerController.addNeed(need, header);

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testBrowseNeeds() throws IOException { // getNeeds may throw IOException
        // Setup
        Need[] needs = new Need[2];
        needs[0] = new Need("Pop culture reference 1", 99, "67");
        needs[1] = new Need("Pop culture reference 2", 100, "The rock");
        // When getNeeds is called return the needes created above
        when(mockCupboardDAO.getNeeds()).thenReturn(needs);

        // Invoke
        ResponseEntity<Need[]> response = managerController.browseNeeds();

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(needs, response.getBody());
    }

    @Test
    void testBrowseNeedsHandleException() throws IOException { // getNeeds may throw IOException
        // Setup
        // When getNeeds is called on the Mock Need DAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).getNeeds();

        // Invoke
        ResponseEntity<Need[]> response = managerController.browseNeeds();

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testDeleteNeed() throws IOException { // deleteNeeds may throw IOException
        // Setup
        User user = new User(69, "admin", "pword", "", "", false);
        int needId = 99;
        // when deleteNeeds is called return true, simulating successful deletion
        when(mockCupboardDAO.deleteNeed(needId)).thenReturn(true);
        when(mockUserDAO.verifyKey(user.getUsername(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<Manager> response = managerController.deleteNeed(needId, header);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteNeedNotFound() throws IOException { // deleteNeeds may throw IOException
        // Setup
        User user = new User(69, "admin", "pword", "", "", false);
        int needId = 99;
        // when deleteNeeds is called return false, simulating failed deletion
        when(mockCupboardDAO.deleteNeed(needId)).thenReturn(false);
        when(mockUserDAO.verifyKey(user.getUsername(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<Manager> response = managerController.deleteNeed(needId, header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteNeedHandleException() throws IOException { // deleteNeeds may throw IOException
        // Setup
        User user = new User(69, "admin", "pword", "", "", false);
        int needId = 99;
        // When deleteNeeds is called on the Mock Need DAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).deleteNeed(needId);
        when(mockUserDAO.verifyKey(user.getUsername(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<Manager> response = managerController.deleteNeed(needId, header);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testDeleteNeedNoAuth() throws IOException { // deleteNeeds may throw IOException
        // Setup
        User user = new User(69, "admin", "pword", "", "", false);
        int needId = 99;
        // when deleteNeeds is called return true, simulating successful deletion
        when(mockCupboardDAO.deleteNeed(needId)).thenReturn(true);
        when(mockUserDAO.verifyKey(user.getUsername(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "INVALID KEY");
        ResponseEntity<Manager> response = managerController.deleteNeed(needId, header);

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testEditNeed() throws IOException { // updateNeed may throw IOException
        // Setup
        User user = new User(69, "admin", "pword", "", "", false);
        Need need = new Need("Slop", 99, "canned please!");
        // when updateNeed is called, return true simulating successful
        // update and save
        when(mockCupboardDAO.updateNeed(need)).thenReturn(need);
        when(mockUserDAO.verifyKey(user.getUsername(), "valid")).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<Need> response = managerController.editNeed(need, header);
        need.updateNeed("Soup", null);

        // Invoke
        response = managerController.editNeed(need, header);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(need, response.getBody());
    }

    @Test
    void testEditNeedInvalid() throws IOException { // updateNeed may throw IOException
        // Setup
        User user = new User(69, "admin", "pword", "", "", false);
        Need need = new Need("", 99, "canned please!");
        // when updateNeed is called, return true simulating successful
        // update and save
        when(mockCupboardDAO.updateNeed(need)).thenReturn(need);
        when(mockUserDAO.verifyKey(user.getUsername(), "valid")).thenReturn(true);

        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<Need> response = managerController.editNeed(need, header);
        need.updateNeed("", null);

        // Invoke
        response = managerController.editNeed(need, header);

        // Analyze
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testEditNeedFailed() throws IOException { // updateNeed may throw IOException
        // Setup
        User user = new User(69, "admin", "pword", "", "", false);
        Need need = new Need("Cheerios", 99, "Family sized");
        // when updateNeed is called, return true simulating successful
        // update and save
        when(mockCupboardDAO.updateNeed(need)).thenReturn(null);
        when(mockUserDAO.verifyKey(user.getUsername(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<Need> response = managerController.editNeed(need, header);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testEditNeedHandleException() throws IOException { // updateNeed may throw IOException
        // Setup
        User user = new User(69, "admin", "pword", "", "", false);
        Need need = new Need("Sadness", 99, "More Sadness");
        // When updateNeed is called on the Mock Need DAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).updateNeed(need);
        when(mockUserDAO.verifyKey(user.getUsername(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");
        ResponseEntity<Need> response = managerController.editNeed(need, header);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testEditNeedNoAuth() throws IOException { // deleteNeeds may throw IOException
        // Setup
        User user = new User(69, "admin", "pword", "", "", false);
        Need need = new Need("Sadness", 99, "More Sadness");
        // When updateNeed is called on the Mock Need DAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).updateNeed(need);
        when(mockUserDAO.verifyKey(user.getUsername(), "valid")).thenReturn(true);

        // Invoke
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "INVALID KEY");
        ResponseEntity<Need> response = managerController.editNeed(need, header);

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testToggle() throws IOException {
        String username = "uname";
        boolean banned = false;
        User user = new User(0, username, "", "", "", banned);
        User user2 = new User(0, username, "", "", "", !banned);

        when(mockUserDAO.verifyKey(Manager.MANAGER_USERNAME, "valid")).thenReturn(true);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");

        when(mockUserDAO.getUserByUsername(username)).thenReturn(user);

        ResponseEntity<User> response = managerController.toggle(username, header);

        assertEquals(!banned, user2.getBanned());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testToggleNoAuth() throws IOException {
        String username = "uname";

        when(mockUserDAO.verifyKey(Manager.MANAGER_USERNAME, "valid")).thenReturn(false);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");

        ResponseEntity<User> response = managerController.toggle(username, header);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testToggleNotFound() throws IOException {
        String username = "uname";

        when(mockUserDAO.verifyKey(Manager.MANAGER_USERNAME, "valid")).thenReturn(true);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");

        when(mockUserDAO.getUserByUsername(username)).thenReturn(null);

        ResponseEntity<User> response = managerController.toggle(username, header);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testToggleHandleException() throws IOException {
        String username = "uname";
        boolean banned = false;
        User user = new User(0, username, "", "", "", banned);

        when(mockUserDAO.verifyKey(Manager.MANAGER_USERNAME, "valid")).thenReturn(true);
        HashMap<String, String> header = new HashMap<>();
        header.put("key", "valid");

        when(mockUserDAO.getUserByUsername(username)).thenReturn(user);

        doThrow(new IOException()).when(mockUserDAO).toggleBan(user);

        ResponseEntity<User> response = managerController.toggle(username, header);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
