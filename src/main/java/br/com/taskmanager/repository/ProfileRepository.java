package br.com.taskmanager.repository;

import br.com.taskmanager.domain.ProfileEntity;
import br.com.taskmanager.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
}
