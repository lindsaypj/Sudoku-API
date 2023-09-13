package edu.greenriver.sdev.sassproject.models;

import lombok.AllArgsConstructor;

/**
 * Manages the user selected settings.
 * @author Patrick Lindsay
 * @version 1.0
 */
@AllArgsConstructor
public class GameRules {
    private boolean showConflicts; // Incorrect cell values will be highlighted or ignored (T / F)

    /**
     * Constructor to Initialize Default Game settings
     */
    public GameRules() {
        this.showConflicts = false;
    }

    /**
     * Method to set the allowIncorrect Setting
     * @param showConflicts Boolean indicating whether incorrect answers should be highlighted
     */
    public void setShowConflicts(boolean showConflicts) {
        this.showConflicts = showConflicts;
    }

    /**
     * @return Boolean indicating whether incorrect answers should be highlighted
     */
    public boolean getShowConflicts() {
        return this.showConflicts;
    }

    @Override
    public String toString() {
        return "GameRules{" +
                "showConflicts=" + showConflicts +
                '}';
    }
}
