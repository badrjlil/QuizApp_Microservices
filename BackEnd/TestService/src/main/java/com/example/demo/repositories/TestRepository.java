package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.*;;
public interface TestRepository  extends JpaRepository<Test, Long>{
    @Query("SELECT t.id FROM Test t WHERE :candidatId MEMBER OF t.candidateIds")
    Long findTestIdByCandidatId(@Param("candidatId") Long candidatId);

    @Modifying
    @Transactional
    @Query("UPDATE Test t SET t.candidateIds = :candidateId WHERE t.id = :testId")
    void addCandidateToTest(@Param("testId") Long testId, @Param("candidateId") Long candidateId);



}
