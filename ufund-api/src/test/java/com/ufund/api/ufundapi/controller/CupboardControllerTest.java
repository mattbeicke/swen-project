package com.ufund.api.ufundapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import com.ufund.api.ufundapi.persistence.CompletedNeedDAO;
import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.persistence.UserDAO;
import com.ufund.api.ufundapi.model.CompletedNeed;
import com.ufund.api.ufundapi.model.Need;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Test the Cupboard Controller class
 * 
 * @author Matthew Beicke
 */
@Tag("Controller-tier")
class CupboardControllerTest {
    private CupboardController cupboardController;
    private CupboardDAO mockCupboardDAO;
    private CompletedNeedDAO mockCompletedNeedDAO;
    private UserDAO mockUserDAO;

    /**
     * Before each test, create a new CupboardController object and inject
     * a mock Need DAO
     */
    @BeforeEach
    void setupCupboardController() {
        mockCupboardDAO = mock(CupboardDAO.class);
        mockCompletedNeedDAO = mock(CompletedNeedDAO.class);
        mockUserDAO = mock(UserDAO.class);
        cupboardController = new CupboardController(mockCupboardDAO, mockCompletedNeedDAO, mockUserDAO);
    }

    @Test
    void testGetNeed() throws IOException { // getNeed may throw IOException
        // Setup
        Need need = new Need("Water Bottles", 99, "plastic, fiji if possible");
        // When the same id is passed in, our mock Need DAO will return the Need object
        when(mockCupboardDAO.getNeed(need.getId())).thenReturn(need);

        // Invoke
        ResponseEntity<Need> response = cupboardController.getNeed(need.getId());

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
        ResponseEntity<Need> response = cupboardController.getNeed(needId);

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
        ResponseEntity<Need> response = cupboardController.getNeed(needId);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetNeeds() throws IOException { // getNeeds may throw IOException
        // Setup
        Need[] needs = new Need[2];
        needs[0] = new Need("Pop culture reference 1", 99, "67");
        needs[1] = new Need("Pop culture reference 2", 100, "The rock");
        // When getNeeds is called return the needes created above
        when(mockCupboardDAO.getNeeds()).thenReturn(needs);

        // Invoke
        ResponseEntity<Need[]> response = cupboardController.getNeeds();

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(needs, response.getBody());
    }

    @Test
    void testGetNeedsHandleException() throws IOException { // getNeeds may throw IOException
        // Setup
        // When getNeeds is called on the Mock Need DAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).getNeeds();

        // Invoke
        ResponseEntity<Need[]> response = cupboardController.getNeeds();

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testSearchNeeds() throws IOException { // searchNeeds may throw IOException
        // Setup
        String searchString = "tay";
        Need[] needes = new Need[2];
        needes[0] = new Need("Taylor Swift", 99, "i know who this is");
        needes[1] = new Need("lil tay", 100, "never heard of this one");
        // When serachNeeds is called with the search string, return the two
        /// needes above
        when(mockCupboardDAO.searchNeeds(searchString)).thenReturn(needes);

        // Invoke
        ResponseEntity<Need[]> response = cupboardController.searchNeeds(searchString);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(needes, response.getBody());
    }

    @Test
    void testSearchNeedsHandleException() throws IOException { // searchNeeds may throw IOException
        // Setup
        String searchString = "an";
        // When createNeed is called on the Mock Need DAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).searchNeeds(searchString);

        // Invoke
        ResponseEntity<Need[]> response = cupboardController.searchNeeds(searchString);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testgetCompletedNeeds() throws IOException {
        CompletedNeed[] cn = new CompletedNeed[3];
        cn[0] = new CompletedNeed((new Need("First Example", 61, "Requires one thing to be correct")), 1, "Alice",
                1000);
        cn[1] = new CompletedNeed((new Need("Second Example", 62, "Requires many things to be correct")), 2, "Bob",
                1001);
        cn[2] = new CompletedNeed((new Need("Second Example, Continued", 63, "Requires everything to be correct")),
                3, "Charlie", 1002);

        when(mockCompletedNeedDAO.getRecentNeeds()).thenReturn(cn);

        ResponseEntity<CompletedNeed[]> response = cupboardController.getCompletedNeeds();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cn, response.getBody());
    }

    @Test
    void testgetCompletedNeedsHandleException() throws IOException {
        doThrow(new IOException()).when(mockCompletedNeedDAO).getRecentNeeds();

        ResponseEntity<CompletedNeed[]> response = cupboardController.getCompletedNeeds();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testgetCompletedNeedsPage() throws IOException {
        int page = 1;
        CompletedNeed[] cn = new CompletedNeed[3];
        cn[0] = new CompletedNeed((new Need("First Example", 61, "Requires one thing to be correct")), 1, "Alice",
                1000);
        cn[1] = new CompletedNeed((new Need("Second Example", 62, "Requires many things to be correct")), 2, "Bob",
                1001);
        cn[2] = new CompletedNeed((new Need("Second Example, Continued", 63, "Requires everything to be correct")),
                3, "Charles", 1002);

        when(mockCompletedNeedDAO.getRecentNeeds(30, (page - 1) * 30)).thenReturn(cn);

        ResponseEntity<CompletedNeed[]> response = cupboardController.getCompletedNeedsPage(page);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cn, response.getBody());
    }

    @Test
    void testgetCompletedNeedsPageBadRequest() throws IOException {
        int page = 0;

        ResponseEntity<CompletedNeed[]> response = cupboardController.getCompletedNeedsPage(page);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testgetCompletedNeedsPageHandleException() throws IOException {
        int page = 1;
        doThrow(new IOException()).when(mockCompletedNeedDAO).getRecentNeeds(30, (page - 1) * 30);

        ResponseEntity<CompletedNeed[]> response = cupboardController.getCompletedNeedsPage(page);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}