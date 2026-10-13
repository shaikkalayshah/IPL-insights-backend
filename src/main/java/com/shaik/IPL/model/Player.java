
package com.shaik.IPL.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Player name is required")
    @Size(max = 100, message = "Player name cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String playerName;

    @NotBlank(message = "Team is required")
    @Size(max = 100, message = "Team cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String team;

    @NotBlank(message = "Role is required")
    @Size(max = 50, message = "Role cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String role;

    @PositiveOrZero(message = "Matches cannot be negative")
    private Integer matches;

    @PositiveOrZero(message = "Runs cannot be negative")
    private Integer runs;

    @PositiveOrZero(message = "Average cannot be negative")
    @DecimalMax(value = "1000.0", message = "Average is too large")
    private Double average;

    @PositiveOrZero(message = "Strike rate cannot be negative")
    @DecimalMax(value = "1000.0", message = "Strike rate is too large")
    private Double strikeRate;

    @PositiveOrZero(message = "Fifties cannot be negative")
    private Integer fifties;

    @PositiveOrZero(message = "Hundreds cannot be negative")
    private Integer hundreds;

    @PositiveOrZero(message = "Wickets cannot be negative")
    private Integer wickets;

    @PositiveOrZero(message = "Economy cannot be negative")
    @DecimalMax(value = "1000.0", message = "Economy is too large")
    private Double economy;

    @Min(value = 2008, message = "Season cannot be before IPL began in 2008")
    @Max(value = 2100, message = "Invalid season")
    private Integer season;

    public Player() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getMatches() { return matches; }
    public void setMatches(Integer matches) { this.matches = matches; }

    public Integer getRuns() { return runs; }
    public void setRuns(Integer runs) { this.runs = runs; }

    public Double getAverage() { return average; }
    public void setAverage(Double average) { this.average = average; }

    public Double getStrikeRate() { return strikeRate; }
    public void setStrikeRate(Double strikeRate) { this.strikeRate = strikeRate; }

    public Integer getFifties() { return fifties; }
    public void setFifties(Integer fifties) { this.fifties = fifties; }

    public Integer getHundreds() { return hundreds; }
    public void setHundreds(Integer hundreds) { this.hundreds = hundreds; }

    public Integer getWickets() { return wickets; }
    public void setWickets(Integer wickets) { this.wickets = wickets; }

    public Double getEconomy() { return economy; }
    public void setEconomy(Double economy) { this.economy = economy; }

    public Integer getSeason() { return season; }
    public void setSeason(Integer season) { this.season = season; }
}
