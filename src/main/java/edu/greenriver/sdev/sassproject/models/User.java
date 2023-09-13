package edu.greenriver.sdev.sassproject.models;

import edu.greenriver.sdev.sassproject.models.enums.BoardSize;
import edu.greenriver.sdev.sassproject.models.enums.Rank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * User represents a player and tracks their preferences and progress across games.
 * @author Patrick Lindsay
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor  
public class User {
    public static final int TOKEN_PAD_SIZE = 64;
    public static final int HEX_BASE = 16;
    private String username;
    private String token;
    private long lastGen;
    private GamesWon gamesWon;
    private int totalGamesWon;
    private Rank userRank;
    private GameStyle preferences;
    private GameRules settings;

    /**
     * Constructor for a new user. Initializes user rank and preferences.
     * @param username unique identifier for a player
     */
    public User(String username) {
        this.username = username;
        this.token = "";
        this.lastGen = 0;
        this.gamesWon = new GamesWon();
        this.totalGamesWon = 0;
        this.userRank = Rank.STRATUS;
        this.preferences = new GameStyle();
        this.settings = new GameRules();
    }

    ////   GETTERS   ////

    /**
     * @return Unique username of this user
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return Unique token to identify current logged-in session
     */
    public String getToken() { return token; }

    /**
     * @return Timestamp marking the last time the token was generated.
     */
    public long getLastGen() { return lastGen; }

    /**
     * @return GamesWon object containing wins by game.
     */
    public GamesWon getGamesWon() {
        return gamesWon;
    }

    /**
     * @param size BoardSize of the game you want to get win count of
     * @return the number of <size> games won
     */
    public int getGamesWonBySize(BoardSize size) {
        return switch (size) {
            case B4x4 -> gamesWon.getWins4x4();
            case B9x9 -> gamesWon.getWins9x9();
            case B16x16 -> gamesWon.getWins16x16();
        };
    }

    /**
     * Method to ge the total number of games (of any type) won
     * @return the total number of games won by the user
     */
    public int getTotalGamesWon() {
        return totalGamesWon;
    }

    /**
     * @return Rank of the user, identifying their level
     */
    public Rank getUserRank() {
        return userRank;
    }

    /**
     * @return GameStyle object containing the user's style preferences
     */
    public GameStyle getPreferences() {
        return preferences;
    }

    /**
     * @return GameRules object containing the User's selected settings
     */
    public GameRules getSettings() {
        return settings;
    }


    ////   SETTERS   ////

    /**
     * Included for Jackson to parse timestamp. DO NOT USE.
     * Last generation time is set when the token is generated.
     * @param lastGen timestamp of last generated token
     */
    public void setLastGen(long lastGen) {
        this.lastGen = lastGen;
    }

    /**
     * @param gamesWon GamesWon object containing wins by game.
     */
    public void setGamesWon(GamesWon gamesWon) {
        this.gamesWon = gamesWon;
    }

    /**
     * @param size boardsize indicating gamemode to update
     * @param wins number of wins to set for size
     */
    public void setGamesWonBySize(BoardSize size, int wins) {
        switch (size) {
            case B4x4 -> gamesWon.setWins4x4(wins);
            case B9x9 -> gamesWon.setWins9x9(wins);
            case B16x16 -> gamesWon.setWins16x16(wins);
        };
    }

    /**
     * @param totalGamesWon total count of games won of any board size
     */
    public void setTotalGamesWon(int totalGamesWon) {
        this.totalGamesWon = totalGamesWon;
    }

    /**
     * @param userRank Rank of the user identifying their progress
     */
    public void setUserRank(Rank userRank) {
        this.userRank = userRank;
    }

    /**
     * @param preferences Style preferences of the user
     */
    public void setPreferences(GameStyle preferences) {
        this.preferences = preferences;
    }

    /**
     * @param settings Selected Setting options of the user
     */
    public void setSettings(GameRules settings) {
        this.settings = settings;
    }

    /**
     * Method to rank up the user if they meet the conditions.
     * Should be called when the user's rank conditions change (# of wins)
     */
    public void attemptRanking() {
        Rank nextRank = this.userRank.getNextRank();
        if (nextRank.checkRankConditions(this.totalGamesWon)) {
            this.userRank = nextRank;
        }
    }


    ////   GENERATION   ////

    /**
     * Method to generate a unique token that expires after 50 min.
     * Stores the token after generation.
     * @param timeInterval current interval to be used in identifying the token
     */
    public void generateToken(int timeInterval) {
        // Method SOURCE: https://www.geeksforgeeks.org/sha-256-hash-in-java/
        try {
            MessageDigest hash = MessageDigest.getInstance("SHA-256");
            // Make token unique to user
            String input = username + timeInterval;

            // Generate token
            byte[] newToken = hash.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert token to String and return
            BigInteger number = new BigInteger(1, newToken);

            // Convert message digest into hex value
            StringBuilder hexString = new StringBuilder(number.toString(HEX_BASE));

            // Pad with leading zeros
            while (hexString.length() < TOKEN_PAD_SIZE)
            {
                hexString.insert(0, '0');
            }

            this.token = hexString.toString();
            this.lastGen = System.currentTimeMillis();
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("Error Generating Token: " + e);
        }
    }


    ////   COMPARISON METHODS   ////

    @Override
    public boolean equals(Object checkUser) {
        if (this == checkUser) {
            return true;
        }
        if (checkUser == null || getClass() != checkUser.getClass()) {
            return false;
        }
        User user = (User) checkUser;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }
}
