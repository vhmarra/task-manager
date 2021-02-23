package br.com.taskmanager.repository;

import br.com.taskmanager.domain.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailRepository extends JpaRepository<EmailEntity,Long> {

    List<EmailEntity> findAllBySented(Integer sented);

}
