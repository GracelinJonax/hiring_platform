package com.hiringplatform.Contest.repos;

import com.hiringplatform.Contest.model.CodeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeQuestionrepository extends JpaRepository<CodeQuestion,String> {
}
