package com.ufund.api.ufundapi.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.User;
import java.io.File;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCrypt;

@Tag("Persistence-tier")
class UserFileDAOTest {
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
    void setupUserFileDAO() throws IOException {
        mockObjectMapper = mock(ObjectMapper.class);
        testUsers = new User[4];
        testUsers[0] = new User(61, "uname1", "pword1", "", "", 1, false);
        testUsers[2] = new User(63, "uname2", "pword3", "", "", 2, true);
        testUsers[1] = new User(62, "uname2, but more", "pword2", "", "", 3, false);
        testUsers[3] = new User(1, "admin", "adminpw", "", "", 0, false);

        // When the object mapper is supposed to read from the file
        // the mock object mapper will return the hero array above
        when(mockObjectMapper
                .readValue(new File("doesnt_matter.txt"), User[].class))
                .thenReturn(testUsers);
        userFileDAO = new UserFileDAO("doesnt_matter.txt", mockObjectMapper);
    }

    @Test
    void testAddToBasket() {
        User user = new User(1, "name", "pass", "", "", 0, false);
        ArrayList<Integer> basket = new ArrayList<>();
        basket.add(10);
        User newuser = assertDoesNotThrow(() -> userFileDAO.addToBasket(user, 10),
                "Unexpected exception thrown");

        assertEquals(newuser.getBasket(), basket);
    }

    @Test
    void testRemoveFromBasket() {
        User user = new User(1, "name", "pass", "", "", 0, false);
        user.addToBasket(10);
        ArrayList<Integer> basket = new ArrayList<>();
        User newuser = assertDoesNotThrow(() -> userFileDAO.removeFromBasket(user, 10),
                "Unexpected exception thrown");

        assertEquals(newuser.getBasket(), basket);
    }

    @Test
    void testViewBasket() {
        User user = new User(80, "gaming", "pass", "", "", 0, false);
        User result = assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Unexpected exception thrown");
        assertNotNull(result);
        result.addToBasket(10);
        List<Integer> b2 = assertDoesNotThrow(
                () -> userFileDAO.viewBasket(userFileDAO.getUser(result.getId())),
                "Unexpected exception thrown");

        ArrayList<Integer> basket = new ArrayList<>();

        basket.add(10);
        assertEquals(b2, basket);
    }

    @Test
    void testCheckout() {
        User user = new User(1, "name", "pass", "", "", 0, false);
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

        assertEquals(new ArrayList<>(), result.getBasket());
    }

    @Test
    void testGetUser() {
        User user = new User(13, "name", "pass", "", "", 0, false);
        User result = assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Unexpected exception thrown");
        assertNotNull(result);

        assertEquals(result, assertDoesNotThrow(() -> userFileDAO.getUser(result.getId()),
                "Unexpected exception thrown"));
    }

    @Test
    void testCreateUser() {
        User user = new User(15, "name", "pass", "", "", 0, false);
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
    void testCreateUserAlreadyExists() {
        User user = new User(15, "name", "pass", "", "", 0, false);
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "First create user failed (Unexpected exception)");
        User result = assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Unexpected exception thrown");
        assertNull(result);
    }

    @Test
    void testUpdateUser() {
        User user = new User(16, "name", "pass", "", "", 0, false);
        User result = assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Unexpected exception thrown");
        assertNotNull(result);

        String newUsername = "nam2";
        String newPassword = "pass2";

        User updated = new User(result.getId(), newUsername, newPassword, "", "", 0, false);

        User newResult = assertDoesNotThrow(() -> userFileDAO.updateUser(updated),
                "Unexpected exception thrown");

        assertNotNull(newResult);
        assertEquals(newUsername, newResult.getUsername());
        assertTrue(BCrypt.checkpw(newPassword, newResult.getPassword()));
    }

    @Test
    void testUpdateUserAlreadyExists() {
        User user = new User(15, "unique", "pass", "", "", 0, false);
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Initial create user failed (Unexpected exception)");
        User user2 = new User(15, "name2", "pass2", "", "", 0, false);
        User addedUser = assertDoesNotThrow(() -> userFileDAO.createUser(user2),
                "Initial create user failed (Unexpected exception)");

        User copy = new User(addedUser.getId(), "unique", "pass2", "", "", 0, false);
        User updated = assertDoesNotThrow(() -> userFileDAO.updateUser(copy),
                "Unexpected exception thrown");
        assertNull(updated);
    }

    @Test
    void testUpdateUserDoesNotExist() {
        User user = new User(15, "name", "pass", "", "", 0, false);
        // User with ID 15 does not exist
        User result = assertDoesNotThrow(() -> userFileDAO.updateUser(user),
                "Unexpected exception thrown");
        assertNull(result);
    }

    @Test
    void testDeleteUser() {
        User user = new User(17, "name", "pass", "", "", 0, false);
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

    @Test
    void testDeleteUserDoesNotExist() {
        // User with ID 17 does not exist
        boolean result = assertDoesNotThrow(() -> userFileDAO.deleteUser(17),
                "Unexpected exception thrown");
        assertFalse(result);
    }

    @Test
    void testAttemptLogin() {
        String username = "name";
        String password = "pass";

        User user = new User(15, username, password, "", "", 0, false);
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Create user failed (Unexpected exception)");

        String result = assertDoesNotThrow(() -> userFileDAO.attemptLogin(username, password),
                "Unexpected exception thrown");

        assertNotNull(result);
        assertEquals(32, result.length()); // Key size is length 32
    }

    @Test
    void testAttemptLoginInvalid() {
        // No user created here
        String username = "user";
        String password = "pass";

        // Wrong username
        String result = assertDoesNotThrow(() -> userFileDAO.attemptLogin(username, password),
                "Unexpected exception thrown");
        assertNull(result);

        User user = new User(15, username, password, "", "", 0, false);
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Create user failed (Unexpected exception)");

        // Wrong password
        String result2 = assertDoesNotThrow(() -> userFileDAO.attemptLogin(username, "an incorrect password"),
                "Unexpected exception thrown");
        assertNull(result2);
    }

    @Test
    void testAttemptLogout() {
        String username = "name";
        String password = "pass";
        User user = new User(15, username, password, "", "", 0, false);
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Create user failed (Unexpected exception)");

        assertDoesNotThrow(() -> userFileDAO.attemptLogin(username, password),
                "Login attempt failed (Unexpected exception)");

        assertDoesNotThrow(() -> userFileDAO.attemptLogout(username),
                "Unexpected exception thrown");

        boolean result = assertDoesNotThrow(() -> userFileDAO.verifyKey(username, "obviously failing key"),
                "Verify attempt failed (Unexpected exception)");

        assertFalse(result);

        // User does not exist
        assertDoesNotThrow(() -> userFileDAO.attemptLogout("Does not exist"),
                "Unexpected exception thrown");
    }

    @Test
    void testVerifyLogin() {
        String username = "name";
        String password = "pass";
        User user = new User(15, username, password, "", "", 0, false);
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Create user failed (Unexpected exception)");

        boolean result = assertDoesNotThrow(() -> userFileDAO.verifyLogin(username, password));
        assertTrue(result);
    }

    @Test
    void testVerifyLoginInvalid() {
        String username = "name";
        String password = "pass";
        User user = new User(15, username, password, "", "", 0, false);
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Create user failed (Unexpected exception)");

        boolean result = assertDoesNotThrow(() -> userFileDAO.verifyLogin("user does not exist", "irrelevant"));
        assertFalse(result);

        boolean result2 = assertDoesNotThrow(() -> userFileDAO.verifyLogin(username, "incorrect password"));
        assertFalse(result2);
    }

    @Test
    void testVerifyKeyByUsername() {
        String username = "name";
        String password = "pass";
        User user = new User(15, username, password, "", "", 0, false);
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Create user failed (Unexpected exception)");

        String key = assertDoesNotThrow(() -> userFileDAO.attemptLogin(username, password),
                "Login attempt failed (Unexpected exception)");

        boolean result = assertDoesNotThrow(() -> userFileDAO.verifyKey(username, key),
                "Unexpected exception thrown");

        assertTrue(result);

        boolean result2 = assertDoesNotThrow(() -> userFileDAO.verifyKey(username, "obviously failing key"),
                "Unexpected exception thrown");

        assertFalse(result2);
    }

    @Test
    void testVerifyKeyByID() {
        String username = "name";
        String password = "pass";
        User tempUser = new User(15, username, password, "", "", 0, false);
        User user = assertDoesNotThrow(() -> userFileDAO.createUser(tempUser),
                "Create user failed (Unexpected exception)");
        int userID = user.getId();

        String key = assertDoesNotThrow(() -> userFileDAO.attemptLogin(username, password),
                "Login attempt failed (Unexpected exception)");

        boolean result = assertDoesNotThrow(() -> userFileDAO.verifyKey(userID, key),
                "Unexpected exception thrown");

        assertTrue(result);

        boolean result2 = assertDoesNotThrow(() -> userFileDAO.verifyKey(userID, "obviously failing key"),
                "Unexpected exception thrown");

        assertFalse(result2);
    }

    @Test
    void testVerifyKeyFails() {
        String username = "name";
        String password = "pass";

        User user = new User(15, username, password, "", "", 0, false);
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Create user failed (Unexpected exception)");

        // User does not exist
        boolean result = assertDoesNotThrow(() -> userFileDAO.verifyKey("DNE", "irrelevant"),
                "Unexpected exception thrown");
        assertFalse(result);

        // User is not currently logged in
        boolean result2 = assertDoesNotThrow(() -> userFileDAO.verifyKey(username, "irrelevant"),
                "Unexpected exception thrown");
        assertFalse(result2);
    }

    @Test
    void testIsManager() {
        String username2 = "name";
        String password2 = "pass";

        User userTemp = new User(16, username2, password2, "", "", 0, false);
        User user = assertDoesNotThrow(() -> userFileDAO.createUser(userTemp),
                "Create user failed (Unexpected exception)");

        boolean result = assertDoesNotThrow(() -> userFileDAO.userIsManager(testUsers[3].getId()));
        assertTrue(result);

        boolean result2 = assertDoesNotThrow(() -> userFileDAO.userIsManager(user.getId()));
        assertFalse(result2);
    }

    @Test
    void testGetQuestion() {
        String username = "user";
        String password = "pass";
        String question = "quest";
        User user = new User(15, username, password, question, "", 0, false);

        String response = assertDoesNotThrow(() -> userFileDAO.getQuestion(user),
                "Create user failed (Unexpected exception)");
        assertEquals(question, response);
    }

    @Test
    void testVerifyAnswer() {
        String username = "user";
        String password = "pass";
        String answer = "hello";
        User user = new User(15, username, password, "", answer, 0, false);

        boolean response = assertDoesNotThrow(() -> userFileDAO.verifyAnswer(user, "hello"),
                "Create user failed (Unexpected exception)");
        assertTrue(response);
    }

    @Test
    void testGetAllUsers() {
        User[] users = userFileDAO.getUsers();

        assertEquals(3, users.length);
        for (int i = 0; i < 3; ++i) {
            assertEquals(users[i], testUsers[i]);
        }
    }

    @Test
    void testSearchUsers() {
        User[] users = userFileDAO.searchUsers("ame2");

        assertEquals(2, users.length);
        assertEquals(users[0], testUsers[1]);
        assertEquals(users[1], testUsers[2]);
    }

    @Test
    void testVerifyKeyOvertime() {
        String username = "uname";
        String password = "pword";
        User user = new User(0, username, password, "", "", 0, false);

        assertDoesNotThrow(() -> userFileDAO.createUser(user));
        String key = assertDoesNotThrow(() -> userFileDAO.attemptLogin(username, password));

        Instant fakeNow = Instant.now().plusSeconds(3601);

        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class)) {
            mockedInstant.when(Instant::now).thenReturn(fakeNow);

            boolean result = assertDoesNotThrow(() -> userFileDAO.verifyKey(username, key));
            assertFalse(result);
        }
    }

    @Test
    void testGetMaxUsers() {
        int max = userFileDAO.getMaxUsers();

        assertEquals(testUsers.length - 1, max);
    }

    @Test
    void testGetTopNUsers() {
        User[] output = userFileDAO.getTopNUsers(userFileDAO.getMaxUsers());

        assertEquals(testUsers[1], output[0]);
        assertEquals(testUsers[2], output[1]);
        assertEquals(testUsers[0], output[2]);
    }

    @Test
    void testIsBanned() {
        assertTrue(userFileDAO.isBanned(testUsers[2]));
        assertFalse(userFileDAO.isBanned(testUsers[0]));
    }

    @Test
    void testtoggleBan() {
        assertDoesNotThrow(() -> userFileDAO.toggleBan(testUsers[0]),
                "Create user failed (Unexpected exception)");

        assertTrue(userFileDAO.isBanned(testUsers[0]));
    }
}
