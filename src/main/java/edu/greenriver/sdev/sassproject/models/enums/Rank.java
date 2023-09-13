package edu.greenriver.sdev.sassproject.models.enums;

/**
 * Rank represents the level of a user. Rank is based on the number of
 * games won by the user.
 * @author Patrick Lindsay
 * @version 1.0
 */
public enum Rank {
    // List of Ranks
    STRATUS("Stratus", 0, 1),
    CUMULUS("Cumulus", 10, 2),
    CIRRUS("Cirrus", 30, 3),
    ALTO("Alto", 60, 4),
    NIMBUS("Nimbus", 100, 4);

    // Ordering of Ranks (Constant)
    private static final Rank[] RANK_ORDER = {
        Rank.STRATUS, Rank.CUMULUS, Rank.CIRRUS, Rank.ALTO, Rank.NIMBUS
    };

    // Rank Fields
    private final String rankName;
    private final int minGamesWon;
    private final int nextRank;

    // Constructor
    Rank(String rankName, int minGamesWon, int nextRank) {
        this.rankName = rankName;
        this.minGamesWon = minGamesWon;
        this.nextRank = nextRank;
    }

    /**
     * @return Display name of this rank
     */
    public String getName() {
        return rankName;
    }

    /**
     * Method to get the rank that comes after this rank
     * @return the rank that comes after this rank in the ordering
     */
    public Rank getNextRank() {
        return RANK_ORDER[this.nextRank];
    }

    /**
     * Method to get the minimum number of games to win for this rank
     * @return minimum number of games to win to earn this rank
     */
    public int getMinGamesWon() {
        return minGamesWon;
    }

    /**
     * @param wins Total number of games won by user
     * @return true if the number of wins is >= minimum for this rank
     */
    public boolean checkRankConditions(int wins) {
        return wins >= this.minGamesWon;
    }

    @Override
    public String toString() {
        return "Rank{" +
                "minGamesWon=" + minGamesWon +
                '}';
    }
}
