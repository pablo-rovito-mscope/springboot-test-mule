package com.example.crud.repository;

import com.example.crud.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByUserId(Long userId);
    
    @Query("SELECT c FROM Contract c WHERE c.user.id = :userId AND c.name LIKE %:name%")
    List<Contract> findByUserIdAndNameContaining(@Param("userId") Long userId, @Param("name") String name);
    
    @Query("SELECT COUNT(c) FROM Contract c WHERE c.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}