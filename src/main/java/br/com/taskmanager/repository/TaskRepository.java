package br.com.taskmanager.repository;

import br.com.taskmanager.domain.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> findAllByUser_Id(String userId);

    List<TaskEntity> findAllByUser_IdAndFinalized(String userId, Boolean b);

    List<TaskEntity> findAllByPriorityAndFinalized(Integer priority, Boolean b);

}
