package edu.greenriver.sdev.sassproject.services;

import edu.greenriver.sdev.sassproject.models.User;
import edu.greenriver.sdev.sassproject.models.enums.BoardSize;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;

/**
 * This service provides user objects and allows a client to
 * add users, get users, update users, and remove users
 * @author Patrick Lindsay
 * @version 1.0
 */
@Service
public class UserService {

    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int TOKEN_GENERATION_INTERVAL = 3000000;

    private HashMap<String, User> users = new HashMap<>();
    {
        users.put("example", new User("Example"));
        users.get("example").generateToken(getCurrentTime());
    }
    private HashMap<String, String> userCredentials = new HashMap<>();
    {
        userCredentials.put("example", "password");
    }

    /**
     * Method to get a specific user by username
     * @param username the unique name that identifies a user
     * @param password the password used by the user
     * @return User object containing username, rank, games won, preferences, and settings
     */
    public User getUserByName(String username, String password) {
        // Check if user exists
        if (users.get(username.toLowerCase()) == null) {
            return null;
        }
        User requestedUser = users.get(username.toLowerCase());

        // Check Credentials (LOGIN ATTEMPT)
        if (userCredentials.get(username.toLowerCase()).equals(password)) {
            // Regenerate token (
            requestedUser.generateToken(getCurrentTime());
            return requestedUser;
        }
        // Check for token instead of password (GET USER ATTEMPT)
        else {
            if (requestedUser.getToken().equals(password)) {
                // Check if token is current
                if (isTokenExpired(requestedUser)) {
                    // TOKEN IS TOO OLD (LOGOUT/DENY)
                    return null;
                }
                requestedUser.generateToken(getCurrentTime());
                return requestedUser;
            }
            return null;
        }
    }

    /**
     * Method to check if a user exists
     * @param username String unique identifier for a user
     * @return true if username is assigned to a User object
     */
    public boolean userExists(String username) {
        return users.containsKey(username.toLowerCase());
    }

    /**
     * @return An array of all user objects currently stored
     */
    public User[] getAllUsers() {
        return users.values().toArray(new User[0]);
    }

    /**
     * @param newUser a new user object containing user data
     * @return true if the new user object is added, otherwise false
     */
    public Boolean addUser(User newUser) {
        // Prevent overwriting existing users
        if (users.containsKey(newUser.getUsername().toLowerCase())) {
            return false;
        }
        if (userCredentials.containsKey(newUser.getUsername().toLowerCase())) {
            return false;
        }

        // Store Credentials
        userCredentials.put(newUser.getUsername().toLowerCase(), newUser.getToken());

        // Clear token (password) and generate token
        newUser.generateToken(getCurrentTime());

        // Create user
        users.put(newUser.getUsername().toLowerCase(), newUser);
        return true;
    }

    /**
     * @param testUser User object to validate
     * @param newUserFlag Boolean indicating whether the user is new
     * @return true if the user object is valid, false otherwise
     */
    public Boolean validateUser(User testUser, boolean newUserFlag) {
        String username = testUser.getUsername().toLowerCase();

        // Validate username
        if (username.length() <= MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
            return false;
        }
        if (newUserFlag && users.containsKey(username)) {
            return false;
        }

        // Validate Timestamp (if not new user)
        if (!newUserFlag) {
            if (testUser.getLastGen() != users.get(username).getLastGen()) {
                // Check if password was passed (user not logged in)
                if (!testUser.getToken().equals(userCredentials.get(username))) {
                    return false;
                }
            }
        }

        // Validate games won
        if (!validateGamesWon(testUser)) {
            return false;
        }

        // Validate rank
        return testUser.getUserRank().getMinGamesWon() <= testUser.getTotalGamesWon();
    }

    private boolean validateGamesWon(User testUser) {
        // Validate games won
        int total = testUser.getTotalGamesWon();
        int testTotal = 0;
        if (total < 0) {
            return false;
        }
        try {
            for (BoardSize size : BoardSize.values()) {
                int count = testUser.getGamesWonBySize(size);
                if (count < 0) {
                    return false;
                }
                testTotal += count;
            }
        }
        catch (Exception e) {
            return false;
        }
        return testTotal == total;
    }

    private boolean isTokenExpired(User user) {
        long lastGen = user.getLastGen();
        long timeSinceLastGen = System.currentTimeMillis() - lastGen;
        // Check if token within generation interval
        return timeSinceLastGen > TOKEN_GENERATION_INTERVAL;
    }

    /**
     * Method to update an existing user
     * @param updatedUser user object containing updated data
     * @return the updated user if successful, null otherwise
     */
    public User updateUser(User updatedUser) {
        if (!validateUserModification(updatedUser)) {
            return null;
        }

        // Regenerate token
        updatedUser.generateToken(getCurrentTime());
        // Update user data
        users.put(updatedUser.getUsername().toLowerCase(), updatedUser);
        return users.get(updatedUser.getUsername().toLowerCase());
    }

    /**
     * @param user User object to be deleted
     * @return true if user was found and removed, false otherwise
     */
    public User deleteUser(User user) {
        if (validateUserModification(user)) {
            // Only delete if the passed user matches the user stored
            if (user.equals(users.get(user.getUsername().toLowerCase()))) {
                userCredentials.remove(user.getUsername().toLowerCase());
                return users.remove(user.getUsername().toLowerCase());
            }
        }
        return null;
    }

    private boolean validateUserModification(User user) {
        String username = user.getUsername().toLowerCase();
        try {
            // Check that user exists
            if (!users.containsKey(username)) {
                return false;
            }

            // Check if password was passed (if not, validate Token instead)
            if (!userCredentials.get(username).equals(user.getToken())) {
                // Check that token is not expired
                if (isTokenExpired(users.get(username))) {
                    return false;
                }
                // Check that token matches current token
                if (!user.getToken().equals(users.get(username).getToken())) {
                    return false;
                }
            }
        } catch (Exception ignored) {
            return false;
        }
        // Validate updated user
        return validateUser(user, false);
    }

    // Method to determine the current hour
    private int getCurrentTime() {
        return (int)(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "UserService";
    }
}
