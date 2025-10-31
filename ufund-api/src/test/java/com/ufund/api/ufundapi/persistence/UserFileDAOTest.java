package com.ufund.api.ufundapi.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.User;
import java.io.File;

import org.springframework.security.crypto.bcrypt.BCrypt;

@Tag("Persistence-tier")
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
        testUsers[2] = new User(63, "uname3", "pword3");
        testUsers[1] = new User(62, "uname2", "pword2");

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
    public void testViewBasket() throws IOException{
        User user = new User(80, "gaming", "pass");
        User result = assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Unexpected exception thrown");
        assertNotNull(result);
        result.addToBasket(10);
        ArrayList<Integer> b2 = assertDoesNotThrow(() -> userFileDAO.viewBasket(userFileDAO.getUser(result.getId())),
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

        assertEquals(result, assertDoesNotThrow(() -> userFileDAO.getUser(result.getId()),
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
    public void testCreateUserAlreadyExists() {
        User user = new User(15, "name", "pass");
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "First create user failed (Unexpected exception)");
        User result = assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Unexpected exception thrown");
		assertNull(result);
    }

    @Test
    public void testUpdateUser() {
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
        assertTrue(BCrypt.checkpw(newPassword, new_result.getPassword()));
    }

    @Test
    public void testUpdateUserAlreadyExists() {
        User user = new User(15, "unique", "pass");
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Initial create user failed (Unexpected exception)");
        User user2 = new User(15, "name2", "pass2");
        User added_user = assertDoesNotThrow(() -> userFileDAO.createUser(user2),
                "Initial create user failed (Unexpected exception)");
        
        User copy = new User(added_user.getId(), "unique", "pass2");
        User updated = assertDoesNotThrow(() -> userFileDAO.updateUser(copy),
        "Unexpected exception thrown");
        assertNull(updated);
    }

    @Test
    public void testUpdateUserDoesNotExist() {
        User user = new User(15, "name", "pass");
		// User with ID 15 does not exist
        User result = assertDoesNotThrow(() -> userFileDAO.updateUser(user),
                "Unexpected exception thrown");
		assertNull(result);
    }

    @Test
    public void testDeleteUser() {
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

    @Test
    public void testDeleteUserDoesNotExist() {
		// User with ID 17 does not exist
        boolean result = assertDoesNotThrow(() -> userFileDAO.deleteUser(17),
                "Unexpected exception thrown");
        assertFalse(result);
	}

    @Test
    public void testAttemptLogin() {
        String username = "name";
        String password = "pass";

        User user = new User(15, username, password);
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Create user failed (Unexpected exception)");

        String result = assertDoesNotThrow(() -> userFileDAO.attemptLogin(username, password),
                "Unexpected exception thrown");
        
        assertNotNull(result);
        assertEquals(32, result.length()); // Key size is length 32
    }

    @Test
    public void testAttemptLoginInvalid() {
        // No user created here
        String username = "user";
        String password = "pass";

		// Wrong username
        String result = assertDoesNotThrow(() -> userFileDAO.attemptLogin(username, password),
                "Unexpected exception thrown");
        assertNull(result);

        User user = new User(15, username, password);
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Create user failed (Unexpected exception)");

		// Wrong password
        String result2 = assertDoesNotThrow(() -> userFileDAO.attemptLogin(username, "an incorrect password"),
                "Unexpected exception thrown");
        assertNull(result2);
    }


    @Test
    public void testAttemptLogout() {
        String username = "name";
        String password = "pass";
        User user = new User(15, username, password);
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
	public void testVerifyLogin() {
        String username = "name";
        String password = "pass";
        User user = new User(15, username, password);
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Create user failed (Unexpected exception)");

		boolean result = assertDoesNotThrow(() -> userFileDAO.verifyLogin(username, password));
		assertTrue(result);
	}

	@Test
	public void testVerifyLoginInvalid() {
        String username = "name";
        String password = "pass";
        User user = new User(15, username, password);
        assertDoesNotThrow(() -> userFileDAO.createUser(user),
                "Create user failed (Unexpected exception)");
			
		boolean result = assertDoesNotThrow(() -> userFileDAO.verifyLogin("user does not exist", "irrelevant"));
		assertFalse(result);
			
		boolean result2 = assertDoesNotThrow(() -> userFileDAO.verifyLogin(username, "incorrect password"));
		assertFalse(result2);
	}

    @Test
    public void testVerifyKeyByUsername() {
        String username = "name";
        String password = "pass";
        User user = new User(15, username, password);
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
    public void testVerifyKeyByID() {
        String username = "name";
        String password = "pass";
        User temp_user = new User(15, username, password);
        User user = assertDoesNotThrow(() -> userFileDAO.createUser(temp_user),
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
    public void testVerifyKeyFails() {
        String username = "name";
        String password = "pass";

        User user = new User(15, username, password);
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
	public void testIsManager() {
        String username = "admin";
        String password = "pass";
        String username2 = "name";
        String password2 = "pass";

        User adminuser_temp = new User(15, username, password);
        User adminuser = assertDoesNotThrow(() -> userFileDAO.createUser(adminuser_temp),
                "Create user failed (Unexpected exception)");
        User user_temp = new User(16, username2, password2);
        User user = assertDoesNotThrow(() -> userFileDAO.createUser(user_temp),
                "Create user failed (Unexpected exception)");
		
		boolean result = assertDoesNotThrow(() -> userFileDAO.userIsManager(adminuser.getId()));
		assertTrue(result);
		
		boolean result2 = assertDoesNotThrow(() -> userFileDAO.userIsManager(user.getId()));
		assertFalse(result2);
	}

        @Test
        public void testGetQuestion(){
                String username = "user";
                String password = "pass";
                String question = "quest";
                User user = new User(15, username,password,question, "");
                
                String response = assertDoesNotThrow(() -> userFileDAO.getQuestion(user),
                "Create user failed (Unexpected exception)");
                assertEquals(question, response);
        }

        @Test
        public void testVerifyAnswer(){
                String username = "user";
                String password = "pass";
                String answer = "hello";
                User user = new User(15, username,password,"", answer);
                
                boolean response = assertDoesNotThrow(() -> userFileDAO.verifyAnswer(user,"hello"),
                "Create user failed (Unexpected exception)");
                assertEquals(true, response);
        }
}
