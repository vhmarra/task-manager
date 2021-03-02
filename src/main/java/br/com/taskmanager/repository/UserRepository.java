package br.com.taskmanager.repository;

import br.com.taskmanager.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByCpf(String cpf);

    Optional<UserEntity> findByCpfAndPassword(String cpf, String password);

    Optional<UserEntity> findByCpf(String cpf);


}
