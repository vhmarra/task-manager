package br.com.taskmanager.repository;

import br.com.taskmanager.domain.AccessToken;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

    Optional<AccessToken> findByTokenAndIsActive(String token, Boolean active);

    Optional<AccessToken> findByUser_Id(Long id);

    Optional<AccessToken> findByUser_IdAndIsActive(Long id, Boolean active);

    List<AccessToken> findAllByIsActive(Boolean active);

    List<AccessToken> findAllByUser_Id(Long id);
}
