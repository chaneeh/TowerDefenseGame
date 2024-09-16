package com.example.demo.repository;

import com.example.demo.model.GameActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameActionLogRepository extends JpaRepository<GameActionLog, Long> {
}
