package com.hiringplatform.Contest.Service;

import com.hiringplatform.Contest.model.*;
import com.hiringplatform.Contest.repos.*;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GuestService {

    @Autowired
    Guestrepository guestrepository;
    @Autowired
    Employeerepository employeerepository;
    @Autowired
    Contestrepository contestrepository;
    @Autowired
    McqQuestionrepository mcqQuestionrepository;
    @Autowired
    CodeQuestionrepository codeQuestionrepository;
    public Object loginService(Guest guest){
        if(guestrepository.existsByEmail(guest.getEmail())){
            Guest guest1=guestrepository.findByEmail(guest.getEmail());
            Map<String, Object> map = new HashMap<>();
            map.put("contest",guest1.getContest());
            map.put("GuestId",guest1.getUserId());
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject;}
        else if(employeerepository.existsByEmail(guest.getEmail())){
            int Eid=employeerepository.findByEmail(guest.getEmail()).getEid();
            return employeerepository.findById(Eid).get().getGuest2();
        }
        else{
            return null;
        }
    }

    public void signupService(Guest guest){
        if(guestrepository.existsByEmail(guest.getEmail())){
            Guest guest1=guestrepository.findByEmail(guest.getEmail());
            guest1.setPassword(guest.getPassword());
            guest1.setName(guest.getName());
            guestrepository.save(guest1);
        }
        else{
            Employee employee=employeerepository.findByEmail(guest.getEmail());
            employee.setName(guest.getName());
            employee.setPassword(guest.getPassword());
            employeerepository.save(employee);
        }
    }
    public Contest getContest(int ContestId){
        return contestrepository.findById(ContestId).get();
    }
    public List<Guest> viewscoreService(int ContestId){
       return guestrepository.findByContestOrderByTotalMarksDesc(contestrepository.findById(ContestId).get());
    }


    public void evaluateService(int GuestId,String output){
        int partLogical=0,partGrammer=0,partCoding=0;
        JsonArray jsonArray = JsonParser.parseString(output).getAsJsonArray();
        for(JsonElement jsonElement:jsonArray){
            JsonObject jsonObject=jsonElement.getAsJsonObject();
            String QuestionId=jsonObject.get("Qid").getAsString();
            String answer=jsonObject.get("answer").getAsString();
            McqQuestion mcqQuestion=mcqQuestionrepository.findById(QuestionId).get();
            if(mcqQuestion.getCorrectOp().equalsIgnoreCase(answer)){
                if(mcqQuestion.getPart().equalsIgnoreCase("logical"))
                    partLogical++;
                else if (mcqQuestion.getPart().equalsIgnoreCase("grammar"))
                    partGrammer++;
                else
                    partCoding++;  }}
        Guest guest=guestrepository.findById(GuestId).get();
        String answer="logical: "+partLogical+",grammar: "+partGrammer+",coding: "+partCoding;
        guest.setMcqAns(answer);
//        guest.setMcqMarks(partCoding+partGrammer);
        guest.setMcqQues(output);
        int total=partLogical+partCoding+partGrammer;
        guest.setMcqMarks(total);
        guest.setTotalMarks(total);
        guestrepository.save(guest);
    }


    public void sentToOneonOne(int id, int mark) {
        Contest contest=contestrepository.findById(id).get();
        contest.setPassPercentage(mark);
        mark=(mark*contest.getTotalScore())/100;
        List<Employee> employees = employeerepository.findAll();
        List<Guest> guests = guestrepository.findByMarks(contestrepository.findById(id).get(), mark);

        List<AssignGuestsToEmployees.EmployeeGuestPair> employeeGuestPairs = AssignGuestsToEmployees.assignGuestsToEmployees(employees, guests);

        for (AssignGuestsToEmployees.EmployeeGuestPair employeeGuestPair : employeeGuestPairs) {
            Guest guest = employeeGuestPair.getGuest();
            guest.setEmployee(employeeGuestPair.getEmployee());
            guestrepository.save(guest);
        }
    }
    public JSONObject codeEvaluateService(int GuestId,String CodeQuetionId,String code) throws IOException {
        JSONObject frontoutput=new JSONObject();
        CodeQuestion codeQuestion=codeQuestionrepository.findById(CodeQuetionId).get();
        String input=codeQuestion.getInput();
        String output=codeQuestion.getOutput();
        try {
            File tempDirectory = Files.createTempDirectory("java-execution").toFile();
            String fileName = "Main.java";
            File javaFile = new File(tempDirectory, fileName);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(javaFile))) {
                writer.write(code);
            }
            File inputFile = new File(tempDirectory,"input.txt");
            try (PrintWriter writer = new PrintWriter(new FileWriter(inputFile))) {
                writer.write(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject2=compileJavaCode(tempDirectory,fileName);
            if(jsonObject2.getAsString("now").equals("false")){
                frontoutput.put("status", "error");
                frontoutput.put("output", jsonObject2.getAsString("error"));
                return frontoutput;
            }
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", tempDirectory.getPath(), "Main");
            processBuilder.redirectInput(ProcessBuilder.Redirect.from(inputFile));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            StringBuilder output2 = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output2.append(line);
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                if(output2.toString().equals(output)) {
                    frontoutput.put("status", "success");
                    frontoutput.put("output", output2);
                    return frontoutput;
                }
                else{
                    frontoutput.put("status", "error");
                    frontoutput.put("output", output2);
                    return frontoutput;
                }}
            else {
                frontoutput.put("status", "error");
                frontoutput.put("output", output2);
                return frontoutput;
            }
        } catch (IOException | InterruptedException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String output3 = sw.toString();
            frontoutput.put("status","error");
            frontoutput.put("output",output3);
            return frontoutput;
        }}
    private JSONObject compileJavaCode(File directory, String fileName) {
        StringWriter errorWriter = new StringWriter();
        JSONObject jsonObject=new JSONObject();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        if (compiler == null) {
            jsonObject.put("now",false);
            jsonObject.put("error","no compiller found");
            return jsonObject;
        }
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(new File(directory, fileName));
            List<String> compileOptions = Arrays.asList("-d", directory.getPath());
            PrintWriter errorPrinter = new PrintWriter(errorWriter);
            JavaCompiler.CompilationTask task = compiler.getTask(
                    errorPrinter,
                    fileManager,
                    diagnostics,
                    compileOptions,
                    null,
                    compilationUnits
            );
            if(task.call()){
                jsonObject.put("now",true);
                return jsonObject;
            }
            else{
//                System.out.println(errorWriter.toString()+"123456");
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    errorWriter.append(diagnostic.getLineNumber()+" Line ");
                    errorWriter.append(diagnostic.getMessage(null)).append("\n");
                }
                jsonObject.put("now",false);
                jsonObject.put("error",errorWriter.toString());
                return jsonObject;
            }
        }catch (Error | IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String z = sw.toString();
            jsonObject.put("now",false);
            jsonObject.put("error",z);
            return jsonObject;
        }}

    public void submitCode(String output,String code,int GuestId,String CodeQuestionId){
        Guest guest=guestrepository.findById(GuestId).get();
        int codemark=guest.getCodeMarks();
        if(output.equals("success")){
            codemark=guest.getCodeMarks()+10;}
        System.out.println(codemark+"asdfghj");
            guest.setCodeMarks(codemark);
            guest.setTotalMarks(guest.getTotalMarks()+codemark);
            Object object= JSONValue.parse(guest.getCodeQues());
            JSONObject jsonObject;
            if(object==null)
            jsonObject=new JSONObject();
            else
             jsonObject=(JSONObject)object;
            jsonObject.put("Qid",CodeQuestionId);
            jsonObject.put("ans",code);
            guest.setCodeQues(jsonObject.toString());
            guestrepository.save(guest);
        }
}