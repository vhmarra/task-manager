package br.com.taskmanager.repository;

import br.com.taskmanager.domain.FeatureEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureRepository extends JpaRepository<FeatureEntity, Long> {

    @Query(value = "insert ignore into feature (id,is_active,feature_name) values (1,true,'EMAIL_FEATURE')")
    void saveFeature();

}

