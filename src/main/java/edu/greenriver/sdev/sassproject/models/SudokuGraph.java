package edu.greenriver.sdev.sassproject.models;

import edu.greenriver.sdev.sassproject.models.enums.BoardSize;

import java.util.*;

/**
 * Graph data structure to store game data and quickly verify board completion.
 * Undirected (edges go both ways)
 * Unweighted
 * @author Patrick Lindsay
 * @version 1.0
 */
public class SudokuGraph {
    private Map<Integer, HashSet<Integer>> adjacencyLists = new HashMap<>();

    /**
     * Constructor to initialize a Sudoku game data object.
     * Graph is constructed by adding each cell on the board to the graph, referenced by an index.
     * Edges are then added between each cell and every other cell that is in the same row,
     * column, or group.
     * @param size Sudoku board width/height
     */
    public SudokuGraph(BoardSize size) {
        final int dimension = size.getDimensions();

        // Add all vertices to graph (cells on the board)
        for (int cellLocation = 0; cellLocation < size.getCellCount(); cellLocation++) {
            adjacencyLists.put(cellLocation, null);
        }

        // Add edges connecting cells in the same row or column
        for (int cellLocation = 0; cellLocation < size.getCellCount(); cellLocation++) {
            int rowFirstIndex = (dimension * (cellLocation / dimension));
            int column = cellLocation % dimension;

            // Same row (Find start of current row, traverse row)
            for (int i = rowFirstIndex; i < (rowFirstIndex + dimension); i++) {
                if (i != cellLocation) {
                    addEdge(cellLocation, i);
                }
            }

            // Same column (find start of current column, traverse column)
            for (int i = column; i < size.getCellCount(); i += dimension) {
                if (i != cellLocation) {
                    addEdge(cellLocation, i);
                }
            }

            // Same group
            Set<Integer> groupCells = new HashSet<>(size.getGroupIndices(cellLocation));
            for (int groupCellIndex : groupCells) {
                if (groupCellIndex != cellLocation) { // Exclude current cell (self loop)
                    addEdge(cellLocation, groupCellIndex);
                }
            }
        }
    }

    // Method to add an undirected edge to the graph
    private void addEdge(int first, int second) {
        addDirectedEdge(first, second);
        addDirectedEdge(second, first);
    }
    private void addDirectedEdge(int first, int second) {
        // Check if adjacent set is initialized
        if (adjacencyLists.get(first) != null) {
            adjacencyLists.get(first).add(second);
        }
        else {
            // Initialize adjacent set
            HashSet<Integer> newSet = new HashSet<>();
            newSet.add(second);
            adjacencyLists.put(first, newSet);
        }
    }

    /**
     * Method to get a set of cell locations that potentially conflict with given cell.
     * @param cellLocation indexed location of a cell
     * @return a set of indices that potentially conflict with the given cell location
     */
    public Set<Integer> getConflictingCells(int cellLocation) {
        return adjacencyLists.get(cellLocation);
    }

    @Override
    public String toString() {
        return "SudokuGraph{}";
    }
}
