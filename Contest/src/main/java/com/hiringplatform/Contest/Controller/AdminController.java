package com.hiringplatform.Contest.Controller;

import com.hiringplatform.Contest.Service.AdminService;
import com.hiringplatform.Contest.model.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class AdminController {
     @Autowired
    AdminService adminService;

    @PostMapping("/addEmp")
    public void addEmp(@RequestBody Employee employee){
    adminService.addEmpService(employee);
    }

    @PostMapping("/addContest")
    public int addContest(@RequestBody Contest contest){
        return adminService.addContestService(contest);
    }

    @GetMapping("/getLiveContest")
    public List<Contest> getLiveContest(){
       return adminService.getLiveContestService();
    }

    @GetMapping("/getContestLog")
    public List<Contest> getContest(){
        return adminService.getContestService();
    }

    @PostMapping("/addUser/{Cid}")
    public void addUser(@PathVariable int ContestId, @RequestBody Guest guest){
        adminService.addUserService(ContestId,guest);
    }
    @Transactional
    @PostMapping("/addPart/{Cid}")
    public List<Part> addPart(@PathVariable int Cid, @RequestBody String Part ) throws IOException {
        return adminService.addPartService(Cid,Part);
    }

    @GetMapping("/getEmployee")
    public List<Employee> getEmployee(){
        return adminService.getEmployeeService();
    }

}

