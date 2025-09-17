package com.example.crud.repository;

import com.example.crud.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByContractId(Long contractId);
    
    @Query("SELECT p FROM Portfolio p WHERE p.contract.id = :contractId AND p.title LIKE %:title%")
    List<Portfolio> findByContractIdAndTitleContaining(@Param("contractId") Long contractId, @Param("title") String title);
    
    @Query("SELECT p FROM Portfolio p WHERE p.contract.user.id = :userId")
    List<Portfolio> findByUserId(@Param("userId") Long userId);
}