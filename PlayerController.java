
package com.shaik.IPL.contoller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.shaik.IPL.model.Player;
import com.shaik.IPL.service.PlayerService;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // Add one player
    @PostMapping
    public Player addPlayer(@Valid @RequestBody Player player) {
        return playerService.addPlayer(player);
    }

    // Add multiple players
    @PostMapping("/bulk")
    public List<Player> addMultiplePlayers(
            @RequestBody List<@Valid Player> players) {
        return playerService.addMultiplePlayers(players);
    }

    // Get all players (existing endpoint)
    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    // Get paginated, sorted and filtered players
    @GetMapping("/page")
    public Page<Player> getPlayersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "playerName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String team,
            @RequestParam(required = false) String role) {

        return playerService.getPlayersPage(
                page, size, sortBy, direction,
                search, team, role);
    }

    // Get player by ID
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(
            @PathVariable int id) {

        return playerService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update player
    @PutMapping("/{id}")
    public Player updatePlayer(
            @PathVariable int id,
            @Valid @RequestBody Player player) {

        return playerService.updatePlayer(id, player);
    }

    // Delete player
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(
            @PathVariable int id) {

        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }

    // Handle field validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": "
                        + error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "status", 400,
                        "error", "Validation failed",
                        "messages", errors));
    }

    // Handle duplicate and invalid requests
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequests(
            IllegalArgumentException ex) {

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "status", 400,
                        "error", "Invalid request",
                        "message", ex.getMessage()));
    }

    // Handle missing player IDs and unexpected runtime errors
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeErrors(
            RuntimeException ex) {

        if (ex.getMessage() != null
                && ex.getMessage().startsWith("Player not found")) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", 404,
                            "error", "Not found",
                            "message", ex.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status", 500,
                        "error", "Internal server error"));
    }
}
