package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.*;

@Component
public class PasswordReset implements IntentWorkflow {
    @Override
    public Message conversation(Message message) {
        return new Message(null, null, null, 0, null);
    }

    @Override
    public List<WorkFlowQuestion> getQuestions() {
        List<WorkFlowQuestion> questions = new ArrayList<>();
        questions.add(new WorkFlowQuestion("Please enter the email of the user you wish to change the password.", TEXT));
        questions.add(new WorkFlowQuestion("An OTP has been sent to your registered mobile number. OTP is only valid for two minutes. You only have two attempts to enter the correct OTP. Please enter the 4-digit OTP to verify your identity.", TEXT));
        questions.add(new WorkFlowQuestion("Invalid OTP, please try again! You have one more attempt", TEXT));
        questions.add(new WorkFlowQuestion("OTP verified! Your password has been changed", TEXT));
        questions.add(new WorkFlowQuestion("Processing....", TEXT));
        questions.add(new WorkFlowQuestion("Invalid Email, please try again!", TEXT));
        questions.add(new WorkFlowQuestion("You account has been suspended. Please contact your administrator for further proceedings.", TEXT));
        questions.add(new WorkFlowQuestion("Do you need any further assistance??", TEXT));
        questions.add(new WorkFlowQuestion("The OTP you entered is incorrect. Please try again later!", TEXT));
        questions.add(new WorkFlowQuestion("Your new password has been sent to your registered mobile number.", TEXT));
        questions.add(new WorkFlowQuestion("Would you like to see your temporary password?", YES_NO));
        questions.add(new WorkFlowQuestion("Your OTP has expired. Please try again later!", TEXT));
        questions.add(new WorkFlowQuestion("You will be asked to change your password once you login with the temporary password.", TEXT));
        return questions;
    }
}

/*


 */
