package com.hiringplatform.Contest.repos;

import com.hiringplatform.Contest.model.McqQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface McqQuestionrepository extends JpaRepository<McqQuestion,String> {
}
