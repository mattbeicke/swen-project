package com.ufund.api.ufundapi.model;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Class code for a {@link User user} object
 * 
 * @author Matthew Beicke
 */
public class User {
    @JsonProperty("id")
    private int id; // User's unique id
    @JsonProperty("username")
    private String username; // User's unique username
    @JsonProperty("password")
    private String password; // User's password
    @JsonProperty("basket")
    private ArrayList<Integer> basket; // User's need basket
    @JsonProperty("securityQuestion")
    private String securityQuestion; // User's security question
    @JsonProperty("securityAnswer")
    private String securityAnswer; // Answer to the security question

    static final String STRING_FORMAT = "User [id=%d, username=%s]";

    /**
     * Super mega constructor for a {@link User user}
     * 
     * @param id               id of user
     * @param username         username of user
     * @param password         password of user
     * @param securityQuestion security question of user
     * @param securityAnswer   answer to security question of user
     */
    public User(int id, String username, String password, String securityQuestion, String securityAnswer) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        basket = new ArrayList<>();
    }

    /**
     * Constructor for a {@link User user} for the load() function, preventing
     * reencrypting
     * 
     * @param id       UserID
     * @param username User's username
     * @param password User's password
     */
    public static User generateUser(int id, String username, String password, String securityQuestion,
            String securityAnswer) {
        return new User(id, username, BCrypt.hashpw(password, BCrypt.gensalt()), securityQuestion, securityAnswer);
    }

    /**
     * Updates a user's username and/or password
     * 
     * @param username new username
     * @param password new password
     */
    public void updateUser(String username, String password) {
        if (username != null) { // Only update what is new
            this.username = username;
        }
        if (password != null) {
            this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        }
    }

    /**
     * Returns the user's username
     * 
     * @return user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the user's encrypted password
     * 
     * @return user's encrypted password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the user's id
     * 
     * @return user's id
     */
    public int getId() {
        return id;
    }

    /**
     * sets a users id
     * 
     * @param id id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the user's basket
     * 
     * @return which is an {@link ArrayList ArrayList} of {@link Need Need} objects
     */
    public ArrayList<Integer> getBasket() {
        return basket;
    }

    /**
     * Adds to a user's basket
     * 
     * @param needId id of the {@link Need need} to add
     */
    public void addToBasket(int needId) {
        if (!inBasket(needId)) {
            basket.add(needId);
        }
    }

    /**
     * "Checksout" a user's basket (removes everything in it)
     * 
     * @return true if there were things in the basket, false if not
     */
    public boolean checkout() {
        if (basket.isEmpty()) {
            return false;
        } else {
            basket.clear();
            return true;
        }
    }

    /**
     * Removes a need from a user's basket
     * 
     * @param needId id of {@link Need need} to remove
     */
    public void removeFromBasket(int needId) {
        basket.remove((Integer) needId);
    }

    /**
     * Checks if a need is in a user's basket
     * 
     * @param needId id of {@link Need need} to check
     * @return true if the {@link Need need} with id needId is in the user's basket,
     *         false otherwise
     */
    public boolean inBasket(int needId) {
        return basket.contains(needId);
    }

    /**
     * checks if a user is manager
     * 
     * @return true if a manager, false if not
     */
    public boolean isManager() {
        return username.equals(Manager.MANAGER_USERNAME);
    }

    /**
     * gets the users security question
     * 
     * @return the users security question
     */
    public String getSecurityQuestion() {
        return securityQuestion;
    }

    /**
     * gets the answer to the security question
     * 
     * @return answer to the users security question
     */
    public String getSecurityAnswer() {
        return securityAnswer;
    }

    /**
     * checks if the supplied answer to the security question is correct
     * 
     * @param answer answer to check
     * @return true if answer is correct, false if not
     */
    public boolean verifyAnswer(String answer) {
        return securityAnswer.equals(answer);
    }

    /**
     * Standard toString method, uses custom format
     * 
     * @return A string containing user data
     */
    @Override
    public String toString() {
        return String.format(STRING_FORMAT, id, username);
    }
}
