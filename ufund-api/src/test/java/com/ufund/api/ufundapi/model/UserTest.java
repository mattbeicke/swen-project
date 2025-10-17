package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.springframework.security.crypto.bcrypt.BCrypt;


@Tag("Model-tier")
public class UserTest {
    @Test
    public void testCreation() {
        String name = "John Doe";
        String password = "hunter2";
        int id = 1001;

        User user = new User(id, name, password);

        assertEquals(name, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(id, user.getId());
        assertEquals(0, user.getBasket().size());
    
    }
    
    @Test
    public void testGeneration() {
        String name = "John Doe";
        String password = "hunter2";
        int id = 1001;

        User user = User.generateUser(id, name, password);

        assertEquals(name, user.getUsername());
        assertTrue(BCrypt.checkpw(password, user.getPassword()));
        assertEquals(id, user.getId());
        assertEquals(0, user.getBasket().size());
    }

    @Test
    public void testUpdateUser() {
        String name = "John Doe";
        String password = "hunter2";
        int id = 1001;

        String new_name = "Jane Doe";
        String new_password = "*******";

        User user = new User(id, name, password); // Password's being overwritten here
        
        user.updateUser(new_name, new_password);

        assertEquals(new_name, user.getUsername());
        assertTrue(BCrypt.checkpw(new_password, user.getPassword()));
    }

    @Test
    public void testUpdateUserWithNulls() {
        String name = "John Doe";
        String password = "hunter2";
        int id = 1001;
        User user = User.generateUser(id, name, password);

        String new_name = "Jane Doe";
        String new_password = "*******";

        String newest_name = "Janset Doe";
        String newest_password = "Something here";

        user.updateUser(null, null); // Change neither

        assertEquals(name, user.getUsername());
        assertTrue(BCrypt.checkpw(password, user.getPassword()));

        user.updateUser(new_name, null); // Change only username

        assertEquals(new_name, user.getUsername());

        user.updateUser(null, new_password); // Change only password

        assertTrue(BCrypt.checkpw(new_password, user.getPassword()));        

        user.updateUser(newest_name, newest_password); // Change both

        assertEquals(newest_name, user.getUsername());
        assertTrue(BCrypt.checkpw(newest_password, user.getPassword()));
    }

    @Test
    public void testAddToBasket() {
        String name = "John Doe";
        String password = "hunter2";
        int id = 1001;
        User user = new User(id, name, password);
        user.addToBasket(30);
        assertEquals(1, user.getBasket().size());
        assertEquals(30, user.getBasket().getLast());
        user.addToBasket(32);
        user.addToBasket(33);
        assertEquals(3, user.getBasket().size());
        assertEquals(33, user.getBasket().getLast());
    }

    @Test
    public void testAddDuplicates() {
        String name = "John Doe";
        String password = "hunter2";
        int id = 1001;
        User user = new User(id, name, password);
        for(int i = 0; i < 10; i++)
            user.addToBasket(30);
        assertEquals(1, user.getBasket().size());
    }
    
    @Test
    public void testRemoveFromBasket() {
        String name = "John Doe";
        String password = "hunter2";
        int id = 1001;
        User user = new User(id, name, password);
        user.addToBasket(30);
        user.addToBasket(32);
        user.addToBasket(33);
        user.removeFromBasket(32);
        assertEquals(2, user.getBasket().size());
        assertEquals(33, user.getBasket().get(1));
    }
    
    @Test
    public void testInBasket() {
        String name = "John Doe";
        String password = "hunter2";
        int id = 1001;
        User user = new User(id, name, password);
        user.addToBasket(30);
        assertTrue(user.inBasket(30));
        assertFalse(user.inBasket(123456));
        user.removeFromBasket(30);
        assertFalse(user.inBasket(30));
    }

    @Test
    public void testToString() {
        String name = "John Doe";
        String password = "hunter2";
        int id = 1001;
        User user = new User(id, name, password);

        String expected = "User [id=1001, username=John Doe]";
        assertEquals(expected, user.toString());

    }
}