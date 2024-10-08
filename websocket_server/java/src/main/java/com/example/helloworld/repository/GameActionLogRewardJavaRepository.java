package com.example.helloworld.repository;

import com.example.helloworld.model.GameActionLogRewardJava;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameActionLogRewardJavaRepository extends JpaRepository<GameActionLogRewardJava, Long> {
    // 필요한 경우 사용자 정의 쿼리를 추가할 수 있습니다.
}
