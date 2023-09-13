package edu.greenriver.sdev.sassproject.models;

import lombok.Data;

/**
 * Class to store the games won by a user by game.
 * @author Patrick Lindsay
 * @version 1.0
 */
@Data
public class GamesWon {
    private int wins4x4;
    private int wins9x9;
    private int wins16x16;

    /**
     * Default constructor
     */
    public GamesWon() {
        this.wins4x4 = 0;
        this.wins9x9 = 0;
        this.wins16x16 = 0;
    }

    public int getWins4x4() {
        return wins4x4;
    }

    public void setWins4x4(int wins4x4) {
        this.wins4x4 = wins4x4;
    }

    public int getWins9x9() {
        return wins9x9;
    }

    public void setWins9x9(int wins9x9) {
        this.wins9x9 = wins9x9;
    }

    public int getWins16x16() {
        return wins16x16;
    }

    public void setWins16x16(int wins16x16) {
        this.wins16x16 = wins16x16;
    }
}
