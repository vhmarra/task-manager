package br.com.taskmanager.repository;

import br.com.taskmanager.domain.AccessToken;
import br.com.taskmanager.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByCpf(String cpf);
}
