package br.com.taskmanager.repository;

import br.com.taskmanager.domain.ChangeUserDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChangeUserDataRepository extends JpaRepository<ChangeUserDataEntity, Long> {

    Optional<ChangeUserDataEntity> findByCodeAndUsed(String code,Integer used);

    List<ChangeUserDataEntity> findAllByUsed(Integer used);

}
