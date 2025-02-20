package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;

import java.util.ArrayList;
import java.util.List;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.*;


public class PasswordReset implements IntentWorkflow {
    @Override
    public Message conversation(Message message) {
        return new Message(null, null, null, 0, null, null);
    }


    @Override
    public List<WorkFlowQuestion> getQuestions() {
        List<WorkFlowQuestion> questions = new ArrayList<>();
        //for app user
//        questions.add(new WorkFlowQuestion("Are you admin user or application user?", LIST, Arrays.asList("admin", "application")));
        questions.add(new WorkFlowQuestion("Please enter the registered email address associated with your Microsoft account.", TEXT, null));
        questions.add(new WorkFlowQuestion("Please confirm if this is correct  ", YES_NO, null));
        questions.add(new WorkFlowQuestion("An OTP has been sent to your registered mobile number. Please enter the 4-digit OTP to verify your identity.", TEXT, null));
        questions.add(new WorkFlowQuestion("Given OTP is wrong...", PROBLEM, null));
        questions.add(new WorkFlowQuestion("Your identity has been verified. Your new temporary password has been generated : ", TEXT, null));
        questions.add(new WorkFlowQuestion("Please use this password to log in to your Microsoft account. For security reasons, you will be required to change your password upon first login.Your password has been successfully reset! Would you like to reset another password?", YES_NO, null));
        questions.add(new WorkFlowQuestion("Given email not valid email address please check ", PROBLEM, null));


        //for admin
//        questions.add(new WorkFlowQuestion("To proceed, please enter your admin email.", TEXT, null));
//        questions.add(new WorkFlowQuestion("Now, please enter your admin password.", PASSWORD, null));
//        questions.add(new WorkFlowQuestion("The password or email entered is incorrect. Please try again.", TEXT, null));
//        questions.add(new WorkFlowQuestion("Your identity has been verified. Please enter the email of the employee whose password you want to reset.", TEXT, null));
//        questions.add(new WorkFlowQuestion("Please confirm if this is correct  ", YES_NO, null));
//        questions.add(new WorkFlowQuestion("Give email not valid email address please check ", PROBLEM, null));
//        questions.add(new WorkFlowQuestion("An OTP has been sent to your registered mobile number. Please enter the 4-digit OTP to verify your identity.", TEXT, null));
//        questions.add(new WorkFlowQuestion("OTP is wrong please check : ", PROBLEM, null));
//        questions.add(new WorkFlowQuestion("Your identity has been verified. Your new temporary password has been generated : ", TEXT, null));
//        questions.add(new WorkFlowQuestion("Please use this password to log in to your Microsoft account. For security reasons, you will be required to change your password upon first login.Your password has been successfully reset! Would you like to reset another password?", YES_NO, null));
//        questions.add(new WorkFlowQuestion("", TEXT, null));
        return questions;
    }
}
