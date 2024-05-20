package com.hiringplatform.Contest.Service;

import com.hiringplatform.Contest.model.*;
import com.hiringplatform.Contest.repos.*;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Service
public class AdminService {
    @Autowired
    Employeerepository employeerepository;
    @Autowired
    Contestrepository contestrepository;
    @Autowired
    Guestrepository guestrepository;
    @Autowired
    Partrepository partrepository;
    @Autowired
    Weightagerepository weightagerepository;

    public void addEmpService(Employee employee){
        employee.setRole(employee.getRole());
        employeerepository.save(employee);
    }
    public int addContestService(Contest contest){
        contestrepository.save(contest);
        return contest.getCid();
    }

    public List<Contest> getLiveContestService(){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        return contestrepository.findByEndTimeAfter(current);
    }

    public List<Contest> getContestService(){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        return contestrepository.findByEndTimeBefore(current);
    }
    public void addUserService(int ContestId,Guest guest){
        guest.setTotalMarks(-1);
        guest.setContest(contestrepository.findById(ContestId).get());
        guest.setRole(guest.getRole());
        System.out.println(guest.getRole());
        guestrepository.save(guest);
    }
    public List<Part> addPartService(int ContestId,String Part ) throws IOException {
        Object file= JSONValue.parse(Part);
        JSONObject jsonObject=(JSONObject)file;
        Part part=new Part();
        part.setName((String)jsonObject.get("Category"));
        Weightage weightage=new Weightage();
        Integer easy=Integer.parseInt((String)jsonObject.get("Easy"));
        Integer medium=Integer.parseInt((String)jsonObject.get("Medium"));
        Integer hard=Integer.parseInt((String)jsonObject.get("Hard"));
        weightage.setEasy(easy);
        weightage.setHard(hard);
        weightage.setMedium(medium);
        int totalScore=easy+medium+hard;
        weightagerepository.save(weightage);
        part.setWeight(weightage);
        Contest contest= contestrepository.findById(ContestId).get();
        if(jsonObject.getAsString("Category").equals("programming")) {
            contest.setTotalScore((totalScore * 10)+contest.getTotalScore());
            String score = contest.getScoreRules();
            if (score == null)
                score = "";
            score = score + "Coding: " + score + " marks";
            contest.setScoreRules(score);
        } else {
            contest.setTotalScore(totalScore+contest.getTotalScore());
            String score = contest.getScoreRules();
            if (score == null)
                score = "";
            if(jsonObject.getAsString("Category").equals("coding"))
            score = score + "Programming: " + totalScore + " marks";
            else score=score+jsonObject.getAsString("Category")+totalScore+"marks";
            contest.setScoreRules(score);
        }
        part.setContest1(contest);
        List<Part> partList = contest.getPart1();
        partList.add(part);
        contest.setPart1(partList);
        contestrepository.save(contest);
        partrepository.save(part);
        return partrepository.findByContest1(contest);
    }
    public List<Employee> getEmployeeService(){
        return employeerepository.findAll();
    }

}
