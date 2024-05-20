package com.hiringplatform.Contest.Controller;

import com.hiringplatform.Contest.Service.GuestService;
import com.hiringplatform.Contest.model.CodeQuestion;
import com.hiringplatform.Contest.model.Contest;
import com.hiringplatform.Contest.model.Guest;
import com.hiringplatform.Contest.model.McqQuestion;
import com.hiringplatform.Contest.repos.CodeQuestionrepository;
import com.hiringplatform.Contest.repos.McqQuestionrepository;
import com.nimbusds.jose.shaded.gson.*;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class GuestController {
    @Autowired
    GuestService guestService;
    @Autowired
    CodeQuestionrepository codeQuestionrepository;
    @Autowired
    McqQuestionrepository mcqQuestionrepository;
    @PostMapping("/login")
    public Object Login(@RequestBody Guest guest){
       return guestService.loginService(guest);
    }
    @PostMapping("/signup")
    public void signUp(@RequestBody Guest guest){
        guestService.signupService(guest);
    }
    @GetMapping("/getContestDetails/{ContestId}")
    public Contest getContestDetails(@PathVariable int ContestId){
        return guestService.getContest(ContestId);
    }
    @GetMapping("/viewScore/{ContestId}")
    public List<Guest> viewScore(@PathVariable int ContestId){
        return guestService.viewscoreService(ContestId);
    }
    @PostMapping("/evaluate/{GuestId}")
    public void evaluate(@PathVariable int GuestId,@RequestBody String output){
        guestService.evaluateService(GuestId,output);
    }
    @PostMapping("/codeEvaluate/{GuestId}/{codeQuestionId}")
    public JSONObject codeEvaluate(@PathVariable int GuestId,@PathVariable String codeQuestionId,@RequestBody String code) throws IOException {
       return guestService.codeEvaluateService(GuestId,codeQuestionId,code);
    }
    @PostMapping("/codeSubmit/{GuestId}/{CodeQuestionId}")
    public void codeSubmit(@PathVariable int GuestId,@PathVariable String CodeQuestionId,@RequestBody String code) throws IOException {
        JSONObject jsonObject=guestService.codeEvaluateService(GuestId,CodeQuestionId,code);
        String output=(String)jsonObject.get("status");
        guestService.submitCode(output,code,GuestId,CodeQuestionId);
    }
    @RequestMapping("/split/{id}/{pass_mark}")
    public void split(@PathVariable("id") int id, @PathVariable("pass_mark") int pass_mark) {
        guestService.sentToOneonOne(id, pass_mark);
    }










//    @PostMapping("/add")
//    public void add(@RequestBody String a){
//        System.out.println("ques   "+a);
//        Gson gson = new Gson();
//        Object[] jsonArray = gson.fromJson(a, Object[].class);
//        for(int i=0;i<jsonArray.length;i++)
//            System.out.println(jsonArray[i].);
////        JSONArray jsonArray = new JSONArray(a);
//        for(JsonElement jsonElement:jsonArray){
//            JsonObject jsonObject=jsonElement.getAsJsonObject();
//            System.out.println(jsonObject);
//    }
//    }
}

