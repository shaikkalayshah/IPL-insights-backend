
package com.shaik.IPL.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.shaik.IPL.model.Player;

public interface PlayerRepository
        extends JpaRepository<Player, Integer>,
                JpaSpecificationExecutor<Player> {

    boolean existsByPlayerNameIgnoreCaseAndTeamIgnoreCaseAndSeason(
            String playerName,
            String team,
            Integer season
    );

    boolean existsByPlayerNameIgnoreCaseAndTeamIgnoreCaseAndSeasonAndIdNot(
            String playerName,
            String team,
            Integer season,
            int id
    );
}
