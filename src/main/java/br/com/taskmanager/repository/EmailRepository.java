package br.com.taskmanager.repository;

import br.com.taskmanager.domain.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email,Long> {

    List<Email> findAllBySented(Integer sented);

}
