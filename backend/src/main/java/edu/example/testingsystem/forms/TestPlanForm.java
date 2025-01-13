package edu.example.testingsystem.forms;

import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.TestPlan;
import edu.example.testingsystem.security.UserInfoService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class TestPlanForm {
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    @Getter @Setter
    private String testPlanTitle;
    private String startDate;//2024-05-09
    private String endDate;



    public void setStartDate(Date startDate) {
        if(startDate == null)
            throw new IllegalArgumentException("startDate cannot be null");
        this.startDate = formatter.format(startDate);
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        if (endDate == null)
            throw new IllegalArgumentException("endDate cannot be null");
        this.endDate = formatter.format(endDate);
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Date getStartDateDate(){
        if(startDate == null) {
            return null;
        }try {
            return formatter.parse(startDate);
        }catch (ParseException e) {
            return null;
        }
    }

    public String getStartDate() {
        return startDate;
    }

    public Date getEndDateDate(){
        if(endDate == null) {
            return null;
        }
        try{
            return formatter.parse(endDate);
        }catch (ParseException e) {
            return null;
        }
    }

    public String getEndDate(){
        return endDate;
    }

    public TestPlan toTestPlan(Project project){
        Date startDateFormatted = new Date();
        Date endDateFormatted = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            startDateFormatted = formatter.parse(startDate);
            endDateFormatted = formatter.parse(endDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return new TestPlan(null,startDateFormatted,endDateFormatted,testPlanTitle, UserInfoService.getCurrentUser(),false,null,project);

    }
}
