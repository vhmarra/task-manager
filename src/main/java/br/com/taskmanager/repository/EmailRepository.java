package br.com.taskmanager.repository;

import br.com.taskmanager.domain.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email,Long> {
}
