package com.example.demo.repository;

import com.example.demo.model.GameActionLogReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameActionLogRewardRepository extends JpaRepository<GameActionLogReward, Long> {
}
