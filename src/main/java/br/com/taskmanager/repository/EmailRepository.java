package br.com.taskmanager.repository;

import br.com.taskmanager.domain.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailRepository extends JpaRepository<EmailEntity,Long> {

    List<EmailEntity> findAllBySented(Integer sented);

    Optional<EmailEntity> findByIdAndSentedNot(Long id, Integer sentend);

}
