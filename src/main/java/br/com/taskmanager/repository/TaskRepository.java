package br.com.taskmanager.repository;

import br.com.taskmanager.domain.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity,Long> {

    List<TaskEntity> findAllByUser_Id(Long userId);
    List<TaskEntity> findAllByUser_IdAndFinalized(Long userId,Boolean b);

}
