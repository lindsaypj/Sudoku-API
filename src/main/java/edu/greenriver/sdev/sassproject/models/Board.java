package edu.greenriver.sdev.sassproject.models;

import edu.greenriver.sdev.sassproject.models.enums.BoardSize;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The Board stores and maintains the numbers on the game board.
 * Evaluates for win condition when potentially met.
 * @author Patrick Lindsay
 * @version 1.0
 */
public class Board {
    private final BoardSize size;
    private final int[] initialData;
    private int[] gameData;
    private SudokuGraph verifier;
    private HashSet<Conflict> conflicts;

    /**
     * Constructor for a random new board of given size.
     * @param size BoardSize enum containing the dimensions of the board
     */
    public Board(BoardSize size) {
        this(size, generateInitialBoard(size));
    }

    /**
     * Constructor for creating a board object given a solvable sudoku array of ints.
     * @param size BoardSize enum containing the dimensions of the board
     * @param initialData Incomplete sudoku board data
     */
    public Board(BoardSize size, int[] initialData) {
        this.size = size;
        this.initialData = initialData;
        this.gameData = Arrays.copyOf(initialData, size.getCellCount());
        this.verifier = new SudokuGraph(size);
        this.conflicts = new HashSet<>();
    }

    /**
     * Method used to generate a new sudoku board of the given size
     * @param size size of board to generate
     * @return an incomplete solvable sudoku board of the specified size as an array
     */
    public static int[] generateInitialBoard(BoardSize size) {
        return new int[0];
    }


    ////   GETTERS   ////

    /**
     * Method to get the data for all cells on the board
     * @return Array of integers representing game data
     */
    public int[] getGameData() {
        return this.gameData;
    }

    /**
     * Method to get the dimensions of the board
     * @return an enum containing the number of rows and columns on the board
     */
    public BoardSize getSize() {
        return this.size;
    }

    /**
     * Method to get a set of all the cells which have a conflict
     * @return set of cells which have conflicts
     */
    public Set<Integer> getConflictingCells() {
        HashSet<Integer> conflictCells = new HashSet<>();
        for (Conflict conflict : conflicts) {
            conflictCells.add(conflict.firstIndex);
            conflictCells.add(conflict.secondIndex);
        }
        return conflictCells;
    }


    ////   SETTERS   ////

    /**
     * Method to update a cell on the board. Called when the player types a number
     * in a cell on the board
     * @param location integer representing cell location on the board and in game-data array
     * @param value value to store in the cell
     */
    public void setCell(int location, int value) {
        // Validate value
        if (value < 0 || value > size.getDimensions()) {
            // Value is out of bounds
            value = 0;
        }
        // Validate location
        if (location < 0 || location >= size.getCellCount()) {
            throw new IllegalArgumentException("Location not on board");
        }

        // Check if the cell is in the initial set (not modifiable)
        if (initialData[location] != 0) {
            gameData[location] = value;

            if (value != 0) {
                // Check for conflicts (if value != 0)
                evaluateCellConflicts(location);
            }
        }
    }

    ////   OTHER   ////

    /**
     * Method to check each cell on the board for conflicts.
     * EXPENSIVE.
     */
    public void evaluateForConflicts() {
        // Loop over the cells on the board
        for (int location = 0; location < size.getCellCount(); location++) {
            // Check all possible conflicts for each cell
            evaluateCellConflicts(location);
        }
    }

    private void evaluateCellConflicts(int location) {
        for (int conflictIndex : verifier.getConflictingCells(location)) {
            if (gameData[location] == gameData[conflictIndex]) {
                // Conflict Found
                conflicts.add(new Conflict(location, conflictIndex));
            } else {
                // Check for Conflict to clear
                Conflict testConflict = new Conflict(location, conflictIndex);
                if (conflicts.contains(testConflict)) {
                    conflicts.remove((testConflict));
                }
            }
        }
    }

    /**
     * Method to check if the board has been completed successfully
     * @return true if the Sudoku board is valid, false otherwise
     */
    public boolean checkForWin() {
        // Check for conflicts
        evaluateForConflicts();

        // Evaluate win conditions
        boolean noConflicts = conflicts.isEmpty();
        boolean validBoard = gameData.length == size.getCellCount();
        boolean allCellsFilled = Arrays.stream(gameData).noneMatch(cellValue ->
                (cellValue <= 0) || (cellValue > size.getDimensions()));

        return noConflicts && validBoard && allCellsFilled;
    }

    @Override
    public boolean equals(Object otherBoard) {
        if (this == otherBoard) {
            return true;
        }
        if (otherBoard == null || getClass() != otherBoard.getClass()) {
            return false;
        }
        Board board = (Board) otherBoard;
        return size == board.size && Arrays.equals(initialData, board.initialData);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size);
        result = 31 * result + Arrays.hashCode(initialData);
        return result;
    }

    // Private class to track conflicts between pairs of cells
    private static class Conflict {
        private int firstIndex;
        private int secondIndex;

        public Conflict(int firstIndex, int secondIndex) {
            this.firstIndex = firstIndex;
            this.secondIndex = secondIndex;
        }

        @Override
        public boolean equals(Object otherConflict) {
            if (this == otherConflict) {
                return true;
            }
            if (otherConflict == null || getClass() != otherConflict.getClass()) {
                return false;
            }
            Conflict conflict = (Conflict) otherConflict;
            return (firstIndex == conflict.firstIndex && secondIndex == conflict.secondIndex) ||
                    (firstIndex == conflict.secondIndex && secondIndex == conflict.firstIndex);
        }

        @Override
        public int hashCode() {
            // XOR the hash of each so that they result in the same code (order independent)
            return Objects.hash(firstIndex, secondIndex) ^ Objects.hash(secondIndex, firstIndex);
        }

        @Override
        public String toString() {
            return "Conflict{" + firstIndex + ", " + secondIndex + '}';
        }
    }

    @Override
    public String toString() {
        return "Board";
    }
}
