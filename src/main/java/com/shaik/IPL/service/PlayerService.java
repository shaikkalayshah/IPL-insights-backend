
package com.shaik.IPL.service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shaik.IPL.model.Player;
import com.shaik.IPL.repo.PlayerRepository;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    // Normalize text for duplicate checking
    private String normalize(String value) {
        return value == null
                ? ""
                : value.trim().toLowerCase(Locale.ROOT);
    }

    // Remove unnecessary spaces
    private void preparePlayer(Player player) {
        if (player.getPlayerName() != null) {
            player.setPlayerName(player.getPlayerName().trim());
        }

        if (player.getTeam() != null) {
            player.setTeam(player.getTeam().trim());
        }

        if (player.getRole() != null) {
            player.setRole(player.getRole().trim());
        }
    }

    // Check whether the player, team and season already exist
    private void checkDuplicate(Player player) {
        boolean exists =
                playerRepository
                    .existsByPlayerNameIgnoreCaseAndTeamIgnoreCaseAndSeason(
                            player.getPlayerName(),
                            player.getTeam(),
                            player.getSeason());

        if (exists) {
            throw new IllegalArgumentException(
                    "Duplicate player record: "
                    + player.getPlayerName()
                    + " - " + player.getTeam()
                    + " - Season " + player.getSeason());
        }
    }

    // Add one player
    public Player addPlayer(Player player) {
        preparePlayer(player);
        checkDuplicate(player);
        return playerRepository.save(player);
    }

    // Add multiple players
    @Transactional
    public List<Player> addMultiplePlayers(List<Player> players) {

        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException(
                    "Player list cannot be empty");
        }

        Set<String> incomingRecords = new HashSet<>();

        for (Player player : players) {

            if (player == null) {
                throw new IllegalArgumentException(
                        "Player list cannot contain null entries");
            }

            preparePlayer(player);

            String key = normalize(player.getPlayerName())
                    + "|" + normalize(player.getTeam())
                    + "|" + player.getSeason();

            if (!incomingRecords.add(key)) {
                throw new IllegalArgumentException(
                        "Duplicate player in upload: "
                        + player.getPlayerName()
                        + " - " + player.getTeam()
                        + " - Season " + player.getSeason());
            }

            checkDuplicate(player);
        }

        return playerRepository.saveAll(players);
    }

    // Get all players (existing endpoint)
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    // Get paginated, sorted and filtered players
    public Page<Player> getPlayersPage(
            int page,
            int size,
            String sortBy,
            String direction,
            String search,
            String team,
            String role) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        Set<String> allowedSortFields = Set.of(
                "id", "playerName", "team", "role",
                "matches", "runs", "average",
                "strikeRate", "fifties", "hundreds",
                "wickets", "economy", "season");

        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortBy);
        }

        Sort.Direction sortDirection;

        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Direction must be asc or desc");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy));

        Specification<Player> spec =
                (root, query, cb) -> cb.conjunction();

        // Search player name, team and role
        if (search != null && !search.isBlank()) {
            String pattern = "%"
                    + search.trim().toLowerCase(Locale.ROOT)
                    + "%";

            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(
                                    cb.lower(root.get("playerName")),
                                    pattern),
                            cb.like(
                                    cb.lower(root.get("team")),
                                    pattern),
                            cb.like(
                                    cb.lower(root.get("role")),
                                    pattern)
                    ));
        }

        // Filter by team
        if (team != null && !team.isBlank()) {
            String selectedTeam =
                    team.trim().toLowerCase(Locale.ROOT);

            spec = spec.and((root, query, cb) ->
                    cb.equal(
                            cb.lower(root.get("team")),
                            selectedTeam));
        }

        // Filter by role
        if (role != null && !role.isBlank()) {
            String selectedRole =
                    role.trim().toLowerCase(Locale.ROOT);

            spec = spec.and((root, query, cb) ->
                    cb.equal(
                            cb.lower(root.get("role")),
                            selectedRole));
        }

        return playerRepository.findAll(spec, pageable);
    }

    // Get player by ID
    public Optional<Player> getPlayerById(int id) {
        return playerRepository.findById(id);
    }

    // Update player
    public Player updatePlayer(int id, Player updatedPlayer) {

        Player existingPlayer = playerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Player not found with ID: " + id));

        preparePlayer(updatedPlayer);

        boolean duplicate =
                playerRepository
                    .existsByPlayerNameIgnoreCaseAndTeamIgnoreCaseAndSeasonAndIdNot(
                            updatedPlayer.getPlayerName(),
                            updatedPlayer.getTeam(),
                            updatedPlayer.getSeason(),
                            id);

        if (duplicate) {
            throw new IllegalArgumentException(
                    "Another record already exists for this player, "
                    + "team and season");
        }

        existingPlayer.setPlayerName(updatedPlayer.getPlayerName());
        existingPlayer.setTeam(updatedPlayer.getTeam());
        existingPlayer.setRole(updatedPlayer.getRole());
        existingPlayer.setMatches(updatedPlayer.getMatches());
        existingPlayer.setRuns(updatedPlayer.getRuns());
        existingPlayer.setAverage(updatedPlayer.getAverage());
        existingPlayer.setStrikeRate(updatedPlayer.getStrikeRate());
        existingPlayer.setFifties(updatedPlayer.getFifties());
        existingPlayer.setHundreds(updatedPlayer.getHundreds());
        existingPlayer.setWickets(updatedPlayer.getWickets());
        existingPlayer.setEconomy(updatedPlayer.getEconomy());
        existingPlayer.setSeason(updatedPlayer.getSeason());

        return playerRepository.save(existingPlayer);
    }

    // Delete player
    public void deletePlayer(int id) {

        if (!playerRepository.existsById(id)) {
            throw new RuntimeException(
                    "Player not found with ID: " + id);
        }

        playerRepository.deleteById(id);
    }
}
