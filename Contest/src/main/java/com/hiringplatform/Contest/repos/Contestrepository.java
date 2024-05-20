package com.hiringplatform.Contest.repos;

import com.hiringplatform.Contest.model.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;
@Repository
public interface Contestrepository extends JpaRepository<Contest,Integer> {
    List<Contest> findByEndTimeBefore(Timestamp current);
    List<Contest> findByEndTimeAfter(Timestamp current);
}
