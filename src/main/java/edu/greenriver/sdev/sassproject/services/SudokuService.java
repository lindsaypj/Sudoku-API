package edu.greenriver.sdev.sassproject.services;

import edu.greenriver.sdev.sassproject.models.Board;
import edu.greenriver.sdev.sassproject.models.enums.BoardSize;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This service provides sudoku boards, validates solutions, and checks for
 * conflicts on a provided board.
 * @author Patrick Lindsay
 * @version 1.0
 */
@Service
public class SudokuService {

    private static final int[] BOARD_4x4 = new int[]
            {
                    1, 2, 3, 4,
                    3, 4, 1, 2,
                    2, 1, 4, 3,
                    4, 3, 2, 1
            };
    private static final int[] BOARD_9x9 = new int[]
            {
                    3, 0, 0, 8, 0, 1, 0, 0, 2,
                    2, 0, 1, 0, 3, 0, 6, 0, 4,
                    0, 0, 0, 2, 0, 4, 0, 0, 0,
                    8, 0, 9, 0, 0, 0, 1, 0, 6,
                    0, 6, 0, 0, 0, 0, 0, 5, 0,
                    7, 0, 2, 0, 0, 0, 4, 0, 9,
                    0, 0, 0, 5, 0, 9, 0, 0, 0,
                    9, 0, 4, 0, 8, 0, 7, 0, 5,
                    6, 0, 0, 1, 0, 7, 0, 0, 3
            };
    private static final int[] BOARD_16x16 = new int[]
            {
                    1,  0,  0,  2,  3,  4,  0,  0, 12,  0,  6,  0,  0,  0,  7,  0,
                    0,  0,  8,  0,  0,  0,  7,  0,  0,  3,  0,  0,  9, 10,  6, 11,
                    0, 12,  0,  0, 10,  0,  0,  1,  0, 13,  0, 11,  0,  0, 14,  0,
                    3,  0,  0, 15,  2,  0,  0, 14,  0,  0,  0,  9,  0,  0, 12,  0,
                    13,  0,  0,  0,  8,  0,  0, 10,  0, 12,  2,  0,  1, 15,  0,  0,
                    0, 11,  7,  6,  0,  0,  0, 16,  0,  0,  0, 15,  0,  0,  5, 13,
                    0,  0,  0, 10,  0,  5, 15,  0,  0,  4,  0,  8,  0,  0, 11,  0,
                    16,  0,  0,  5,  9, 12,  0,  0,  1,  0,  0,  0,  0,  0,  8,  0,
                    0,  2,  0,  0,  0,  0,  0, 13,  0,  0, 12,  5,  8,  0,  0,  3,
                    0, 13,  0,  0, 15,  0,  3,  0,  0, 14,  8,  0, 16,  0,  0,  0,
                    5,  8,  0,  0,  1,  0,  0,  0,  2,  0,  0,  0, 13,  9, 15,  0,
                    0,  0, 12,  4,  0,  6, 16,  0, 13,  0,  0,  7,  0,  0,  0,  5,
                    0,  3,  0,  0, 12,  0,  0,  0,  6,  0,  0,  4, 11,  0,  0, 16,
                    0,  7,  0,  0, 16,  0,  5,  0, 14,  0,  0,  1,  0,  0,  2,  0,
                    11,  1, 15,  9,  0,  0, 13,  0,  0,  2,  0,  0,  0, 14,  0,  0,
                    0, 14,  0,  0,  0, 11,  0,  2,  0,  0, 13,  3,  5,  0,  0, 12,
            };

    private HashMap<BoardSize, HashSet<Board>> solvableBoards = new HashMap<>();
    {
        // Initialize sets for each size
        HashSet<Board> solvable4x4 = new HashSet<>();
        HashSet<Board> solvable9x9 = new HashSet<>();
        HashSet<Board> solvable16x16 = new HashSet<>();

        // Add 4x4 solvable Boards
        solvable4x4.add(new Board(BoardSize.B4x4, new int[]{1, 2, 0, 4, 0, 0, 1, 0, 2, 0, 0, 0, 4, 3, 0, 0}));
        solvable4x4.add(new Board(BoardSize.B4x4, new int[]{0, 3, 1, 4, 0, 1, 0, 0, 3, 0, 4, 0, 0, 4, 0, 2}));
        solvable4x4.add(new Board(BoardSize.B4x4, new int[]{0, 2, 3, 0, 3, 4, 0, 2, 0, 0, 0, 1, 2, 0, 0, 3}));

        solvableBoards.put(BoardSize.B4x4, solvable4x4);
        solvableBoards.put(BoardSize.B9x9, new HashSet<>());
        solvableBoards.put(BoardSize.B16x16, new HashSet<>());
    }

    private HashMap<BoardSize, HashSet<Board>> completeBoards = new HashMap<>(); {
        completeBoards.put(BoardSize.B4x4, new HashSet<>());
        completeBoards.put(BoardSize.B9x9, new HashSet<>());
        completeBoards.put(BoardSize.B16x16, new HashSet<>());
    }

    ////   GET REQUESTS   ////

    /**
     * @param size Board size (4x4, 9x9, 16x16, etc.)
     * @return Incomplete sudoku board of
     */
    public int[] getSolvableBoard(BoardSize size) {
        int randomIndex = (int) (Math.random() * solvableBoards.get(size).size());
        switch (size) {
            case B4x4 -> {
                return ((Board)(solvableBoards.get(size).toArray()[randomIndex])).getGameData();
            }
            case B9x9 -> {
                return BOARD_9x9;
            }
            case B16x16 -> {
                return BOARD_16x16;
            }
            default -> {
                return new int[0];
            }
        }
    }

    /**
     * Method to get a randomly generated board of given size.
     * @param size size of sudoku board to generate
     * @return solvable sudoku board as an array of integers
     */
    public int[] generateBoard(BoardSize size) {
        return Board.generateInitialBoard(size);
    }

    /**
     * Method to add a new solved sudoku board to the list of solved boards.
     * @param size BoardSize enum containing the dimensions of the board
     * @param newBoard solved sudoku board of given size in array form
     * @return true if the board was successfully added, false otherwise
     */
    public boolean addCompleteBoard(BoardSize size, int[] newBoard) {
        return completeBoards.get(size).add(new Board(size, newBoard));
    }

    /**
     * Method to verify that a sudoku board is solved, with no conflicts
     * @param size enum representing the dimensions of the board
     * @param board array of integers representing a sudoku board
     * @return true if the board is complete and correct, false otherwise
     */
    public boolean verifyBoard(BoardSize size, int[] board) {
        Board testBoard = new Board(size, board);
        return testBoard.checkForWin();
    }

    /**
     * Method to evaluate a board for conflicting cells.
     * Returns a set of all cells that have a conflict with at least one other cell.
     * @param size enum representing the dimensions of the board
     * @param board array of integers representing a sudoku board
     * @return Set of cell locations that contain conflicts
     */
    public Set<Integer> getConflictingCells(BoardSize size, int[] board) {
        Board testBoard = new Board(size, board);
        testBoard.evaluateForConflicts();
        return testBoard.getConflictingCells();
    }

    /**
     * Method to validate a passed sudoku board (array)
     * @param size enum representing the dimensions of the board
     * @param board array of integers representing a sudoku board
     * @return true if the given board is of the given size and
     * contains only valid values, false otherwise
     */
    public boolean validateBoard(BoardSize size, int[] board) {
        // Verify that each cell value is within the given board size range
        for (int value : board) {
            if (value < 0 || value > size.getDimensions()) {
                return false;
            }
        }
        // Verify that the board contains the correct number of cells for given size
        return size.getCellCount() == board.length;
    }

    @Override
    public String toString() {
        return "SudokuService";
    }
}
