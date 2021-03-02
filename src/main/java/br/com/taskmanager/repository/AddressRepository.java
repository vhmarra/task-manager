package br.com.taskmanager.repository;

import br.com.taskmanager.domain.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity,Long> {

    @Query(value = "select addresses_id from user_addresses where user_entity_id = :id",nativeQuery = true)
    List<Long> findAddressIdByUserId(@Param("id") Long id);
}
