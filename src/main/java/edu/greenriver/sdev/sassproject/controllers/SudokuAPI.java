package edu.greenriver.sdev.sassproject.controllers;

import edu.greenriver.sdev.sassproject.models.User;
import edu.greenriver.sdev.sassproject.models.enums.BoardSize;
import edu.greenriver.sdev.sassproject.services.SudokuService;
import edu.greenriver.sdev.sassproject.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * API to handle sudoku board and user mappings
 * @author Patrick Lindsay
 * @version 1.0
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("sudoku")
public class SudokuAPI {

    private SudokuService sudokuService;
    private UserService userService;

    /**
     * Constructor for Sudoku API
     * @param sudokuService Service for handling sudoku game data
     * @param userService Service for handling user data
     */
    public SudokuAPI(SudokuService sudokuService, UserService userService) {
        this.sudokuService = sudokuService;
        this.userService = userService;
    }

    /**
     * Method to get a solvable board of the specified size.
     * @param boardSize width/height of the board in the format WxH
     * @return HTTP Response containing the board as an int array and status.
     * Board is null if not found.
     */
    @GetMapping("boards/{boardSize}")
    public ResponseEntity<int[]> getBoard(@PathVariable String boardSize) {
        return switch (boardSize) {
            case "4x4" -> new ResponseEntity<>(sudokuService.getSolvableBoard(BoardSize.B4x4), HttpStatus.OK);
            case "9x9" -> new ResponseEntity<>(sudokuService.getSolvableBoard(BoardSize.B9x9), HttpStatus.OK);
            case "16x16" -> new ResponseEntity<>(sudokuService.getSolvableBoard(BoardSize.B16x16), HttpStatus.OK);
            default -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        };
    }

    /**
     * Mapping to add a solved board to the collection.
     * Board must be solved and valid.
     * @param boardSize width/height of the board in the format WxH
     * @param board array of integers representing a sudoku board
     * @return HTTP Response containing error message
     */
    @PostMapping("boards/{boardSize}/solved")
    public ResponseEntity<Set<Integer>> addNewBoard(@PathVariable String boardSize, @RequestBody int[] board) {
        // Determine board size
        BoardSize size = getSize(boardSize);

        // Validate that the size exists
        if (boardSize == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        // Validate board matches size and is valid
        boolean validBoard = sudokuService.validateBoard(size, board);
        // Verify board is complete
        boolean verifiedBoard = sudokuService.verifyBoard(size, board);
        // Attempt to add to collection
        boolean added = sudokuService.addCompleteBoard(size, board);

        // Send response
        if (validBoard && verifiedBoard) {
            return new ResponseEntity<>(null, HttpStatus.CREATED);
        }

        // Get conflicts if relevant
        Set<Integer> conflicts = null;
        if (validBoard) {
            conflicts = sudokuService.getConflictingCells(size, board);
        }

        return new ResponseEntity<>(conflicts, HttpStatus.BAD_REQUEST);
    }

    /**
     * Mapping to get all users.
     * @return an array of all users
     */
    @GetMapping("users")
    public ResponseEntity<User[]> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    /**
     * Mapping to get a specified user via the path.
     * @param username unique name used to identify a user
     * @param token User password (login) or token (logged in request)
     * @return HTTP response containing User if found, 404 if user doesn't exist
     */
    @GetMapping("users/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username, @RequestParam String token) {
        // Check if user exists
        if (!userService.userExists(username)) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        // Get user (only if token/password is valid)
        User requestedUser = userService.getUserByName(username, token);

        // Invalid request (Token/password not valid)
        if (requestedUser == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // Return found user
        return new ResponseEntity<>(requestedUser, HttpStatus.OK);
    }

    /**
     * Mapping to create a new user.
     * @param username Unique identifier for a new User
     * @param newUser User object containing user data
     * @return if username is unused, HTTP response containing created user,
     * otherwise 400 BAD REQUEST
     */
    @PostMapping("users/{username}")
    public ResponseEntity<User> addUser(@PathVariable String username, @RequestBody User newUser) {
        // If User object was passed, validate
        if (newUser != null && userService.validateUser(newUser, true)) {
            // Attempt to create the user
            if (userService.addUser(newUser)) {
                User requestedUser = userService.getUserByName(username, newUser.getToken());
                return new ResponseEntity<>(requestedUser, HttpStatus.CREATED);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    /**
     * Mapping to update an existing user.
     * @param username Unique identifier associated with the user to update
     * @param updatedUser User object containing changes
     * @return updated user object if user exists
     */
    @PutMapping("users/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username, @RequestBody User updatedUser) {
        User response;
        // Verify that the username matches the user object passed
        if (username.equalsIgnoreCase(updatedUser.getUsername())) {
            response = userService.updateUser(updatedUser);
        }
        else {
            response = null;
        }

        // If user does not exist (or invalid), send 400
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        // Return response with updated user
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Mapping to delete a passed user. Username must be passed in the path and
     * mush match the passed user object and stored user object to be deleted.
     * @param username Unique identifier for a user
     * @param deleteUser User object to be deleted
     * @return The deleted user object if successful, otherwise 400 or 404
     */
    @DeleteMapping("users/{username}")
    public ResponseEntity<User> deleteUser(@PathVariable String username, @RequestBody User deleteUser) {
        // Verify that the username and user.name match
        if (!username.equals(deleteUser.getUsername())) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // Verify that user exists
        if (!userService.userExists(username)) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        // Attempt to delete
        // Token must match existing token
        // Token must not have expired
        // User properties must be valid
        User deletedUser = userService.deleteUser(deleteUser);

        // If user did not match that stored
        if (deletedUser == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(deletedUser, HttpStatus.OK);
    }

    private BoardSize getSize(String size) {
        return switch (size) {
            case "4x4" -> BoardSize.B4x4;
            case "9x9" -> BoardSize.B9x9;
            case "16x16" -> BoardSize.B16x16;
            // Size not recognized
            default -> null;
        };
    }

    @Override
    public String toString() {
        return "SudokuAPI{}";
    }
}
