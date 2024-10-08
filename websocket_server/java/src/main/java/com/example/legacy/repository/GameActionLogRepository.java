package com.example.legacy.repository;

import com.example.legacy.model.GameActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameActionLogRepository extends JpaRepository<GameActionLog, Long> {
}
