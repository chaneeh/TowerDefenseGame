package com.example.legacy.repository;

import com.example.legacy.model.GameActionLogReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameActionLogRewardRepository extends JpaRepository<GameActionLogReward, Long> {
}
