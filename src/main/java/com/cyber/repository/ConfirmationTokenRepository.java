package com.cyber.repository;

import com.cyber.entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,Integer> {

    Optional<ConfirmationToken> findByToken(String token);

}
