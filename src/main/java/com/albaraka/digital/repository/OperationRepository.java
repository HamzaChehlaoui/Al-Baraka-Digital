package com.albaraka.digital.repository;

import com.albaraka.digital.model.Operation;
import com.albaraka.digital.model.enums.OperationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    List<Operation> findByStatus(OperationStatus status);

    List<Operation> findByAccountId(Long accountId);

    @Query("SELECT o FROM Operation o WHERE o.account.user.id = :userId ORDER BY o.date DESC")
    List<Operation> findByAccountUserId(@Param("userId") Long userId);

    @Query("SELECT o FROM Operation o WHERE o.status = :status ORDER BY o.date ASC")
    List<Operation> findByStatusOrderByDateAsc(@Param("status") OperationStatus status);
}
