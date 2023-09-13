package edu.greenriver.sdev.sassproject.models.enums;

import java.util.*;

/**
 * Enumeration to encapsulate and abstract the different game board dimensions
 * @author Patrick Lindsay
 * @version 1.0
 */
public enum BoardSize {
    B4x4(4, 16),
    B9x9(9, 81),
    B16x16(16, 256);

    private final int dimensions;
    private final int root;
    private final int cellCount;
    private final HashMap<Integer, HashSet<Integer>> groups; // [group][index]

    //  0  0  0  1  1  1  2  2  2   0  0  1  1
    //  0  0  0  1  1  1  2  2  2   0  0  1  1
    //  0  0  0  1  1  1  2  2  2   2  2  3  3
    //  3  3  3  4  4  4  5  5  5   2  2  3  3
    //  3  3  3  4  4  4  5  5  5
    //  3  3  3  4  4  4  5  5  5   ((Row / root) * root) + (Col / root) = GROUP#
    //  6  6  6  7  7  7  8  8  8
    //  6  6  6  7  7  7  8  8  8
    //  6  6  6  7  7  7  8  8  8

    BoardSize(int dimensions, int cellCount) {
        this.dimensions = dimensions;
        this.root = (int) Math.sqrt(dimensions);
        this.cellCount = cellCount;
        this.groups = new HashMap<>();

        // add sets for each group
        for (int i = 0; i < dimensions; i++) {
            this.groups.put(i, new HashSet<>());
        }

        for (int i = 0; i < cellCount; i++) {
            int group = findGroup(i);

            // Add index to the associated group
            groups.get(group).add(i);
        }

    }

    // Method to find the group associated with a given index
    private int findGroup(int cellIndex) {
        int row = cellIndex / dimensions;
        int col = cellIndex % dimensions;
        return ((row / root) * root) + (col / root);
    }

    /**
     * @return number of rows on the board
     */
    public int getDimensions() {
        return this.dimensions;
    }

    /**
     * Method to get a list of the cell indices that are in the same group as the specified index.
     * @param cellIndex index location of cell
     * @return list of cells in the group associated with given cell index
     */
    public Set<Integer> getGroupIndices(int cellIndex) {
        int groupNumber = findGroup(cellIndex);

        if (groups.containsKey(groupNumber)) {
            return groups.get(groupNumber);
        }
        return new HashSet<>();
    }

    /**
     * @return number of cells on a board of this size
     */
    public int getCellCount() { return this.cellCount; }

    @Override
    public String toString() {
        return "BoardSize{" + dimensions + " x " + dimensions + "}";
    }
}
