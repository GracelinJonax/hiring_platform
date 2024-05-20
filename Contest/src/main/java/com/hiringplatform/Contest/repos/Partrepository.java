package com.hiringplatform.Contest.repos;

import com.hiringplatform.Contest.model.Contest;
import com.hiringplatform.Contest.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface Partrepository extends JpaRepository<Part,Integer> {
    List<Part> findByContest1(Contest c);
}
