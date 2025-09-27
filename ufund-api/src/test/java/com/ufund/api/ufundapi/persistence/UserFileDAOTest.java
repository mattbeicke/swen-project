package com.ufund.api.ufundapi.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.User;
import java.io.File;

public class UserFileDAOTest {
    UserFileDAO userFileDAO;
    User[] testUsers;
    ObjectMapper mockObjectMapper;

    /**
     * Before each test, we will create and inject a Mock Object Mapper to
     * isolate the tests from the underlying file
     * 
     * @throws IOException
     */
    @BeforeEach
    public void setupUserFileDAO() throws IOException {
        mockObjectMapper = mock(ObjectMapper.class);
        testUsers = new User[3];
        testUsers[0] = new User(61, "uname1", "pword1");
        testUsers[1] = new User(62, "uname2", "pword2");
        testUsers[2] = new User(63, "uname3", "pword3");

        // When the object mapper is supposed to read from the file
        // the mock object mapper will return the hero array above
        when(mockObjectMapper
                .readValue(new File("doesnt_matter.txt"), User[].class))
                .thenReturn(testUsers);
        userFileDAO = new UserFileDAO("doesnt_matter.txt", mockObjectMapper);
    }

    @Test
    public void testAddToBasket() {
        User user = new User(1, "name", "pass");
        ArrayList<Integer> basket = new ArrayList<>();
        basket.add(10);
        User newuser = assertDoesNotThrow(() -> userFileDAO.addToBasket(user, 10),
                "Unexpected exception thrown");

        assertEquals(newuser.getBasket(), basket);
    }

    @Test
    public void testRemoveFromBasket() {
        User user = new User(1, "name", "pass");
        user.addToBasket(10);
        ArrayList<Integer> basket = new ArrayList<>();
        User newuser = assertDoesNotThrow(() -> userFileDAO.removeFromBasket(user, 10),
                "Unexpected exception thrown");

        assertEquals(newuser.getBasket(), basket);
    }

    @Test
    public void testViewBasket() {
        User user = new User(80, "name", "pass");
        User result = assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Unexpected exception thrown");
        assertNotNull(result);
        result.addToBasket(10);

        ArrayList<Integer> b2 = assertDoesNotThrow(() -> userFileDAO.viewBasket(userFileDAO.getUser(80)),
                "Unexpected exception thrown");

        ArrayList<Integer> basket = new ArrayList<>();

        basket.add(10);
        assertEquals(b2, basket);
    }

    @Test
    public void testCheckout() {
        User user = new User(1, "name", "pass");
        User result = assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Unexpected exception thrown");
        assertNotNull(result);
        result.addToBasket(0);
        result.addToBasket(2);
        result.addToBasket(3);
        result.addToBasket(4);
        result.addToBasket(1);
        result.addToBasket(5);
        result.addToBasket(6);

        assertNotEquals(new ArrayList<>(), result.getBasket());

        assertDoesNotThrow(() -> userFileDAO.checkout(result),
                "Unexpected exception thrown");

        assertEquals(result.getBasket(), new ArrayList<>());
    }

    @Test
    public void testGetUser() {
        User user = new User(13, "name", "pass");
        User result = assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Unexpected exception thrown");
        assertNotNull(result);

        assertEquals(result, assertDoesNotThrow(() -> userFileDAO.getUser(13),
                "Unexpected exception thrown"));
    }

    @Test
    public void testCreateUser() {
        User user = new User(15, "name", "pass");
        User result = assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Unexpected exception thrown");
        assertNotNull(result);

        User created = assertDoesNotThrow(() -> userFileDAO.getUser(result.getId()),
                "Unexpected exception thrown");
        assertEquals(created.getId(), result.getId());
        assertEquals(created.getUsername(), result.getUsername());
        assertEquals(created.getPassword(), result.getPassword());
    }

    @Test
    public void testUpdateNeed() {
        User user = new User(16, "name", "pass");
        User result = assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Unexpected exception thrown");
        assertNotNull(result);

        String newUsername = "nam2";
        String newPassword = "pass2";

        User updated = new User(result.getId(), newUsername, newPassword);

        User new_result = assertDoesNotThrow(() -> userFileDAO.updateUser(updated),
                "Unexpected exception thrown");

        assertNotNull(new_result);
        assertEquals(new_result.getUsername(), newUsername);
        assertEquals(new_result.getPassword(), newPassword);
    }

    @Test
    public void testDeleteNeed() {
        User user = new User(17, "name", "pass");
        User result = assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Unexpected exception thrown");
        assertNotNull(result);

        User created = assertDoesNotThrow(() -> userFileDAO.getUser(result.getId()),
                "Unexpected exception thrown");

        int ID = created.getId();
        assertDoesNotThrow(() -> userFileDAO.deleteUser(ID),
                "Unexpected exception thrown");
        assertNull(assertDoesNotThrow(() -> userFileDAO.getUser(ID),
                "Unexpected exception thrown"));
    }
}
