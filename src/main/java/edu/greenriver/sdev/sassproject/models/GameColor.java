package edu.greenriver.sdev.sassproject.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.*;

/**
 * Simplified color class to store an RGBA color
 * @author Patrick Lindsay
 * @version 1.0
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameColor {
    private int red;
    private int green;
    private int blue;
    private int transparency;

    /**
     * @param color Color value to emulate
     */
    public GameColor(Color color) {
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.transparency = color.getTransparency();
    }

    @Override
    public String toString() {
        return "rgba(" + red + ", " + green + ", " + blue + ", " + transparency + ")";
    }
}
