package edu.greenriver.sdev.sassproject.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;

/**
 * This object stores the primary styles which dictates the look of a game.
 * Style choices are specific to a user and will be applied across different games.
 * @author Patrick Lindsay
 * @version 1.0
 */
@AllArgsConstructor
@Data
public class GameStyle {
    private GameColor pageBackgroundColor;
    private GameColor cellBackgroundColor;
    private GameColor infoTextColor;
    private GameColor cellTextColor;
    private GameColor boardBorderColor;
    private String infoFont;
    private String boardFont;

    /**
     * Default constructor for GameStyles. Initializes game styles to match
     * traditional Sudoku.
     */
    public GameStyle() {
        this.pageBackgroundColor = new GameColor(new Color(248,249,250));
        this.cellBackgroundColor = new GameColor(Color.WHITE);
        this.infoTextColor = new GameColor(Color.BLACK);
        this.cellTextColor = new GameColor(Color.BLACK);
        this.boardBorderColor = new GameColor(Color.BLACK);
        this.infoFont = "Roboto, sans-serif";
        this.boardFont = "Roboto, sans-serif";
    }
}
