package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;

import java.util.ArrayList;
import java.util.List;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.TEXT;
import static com.ncinga.chatservice.service.impl.workflow.Dictionary.YES_NO;

public class UnlockUser implements IntentWorkflow{

    @Override
    public Message conversation(Message message){
        return new Message(null, null, null, 0, null);
    }

    @Override
    public List<WorkFlowQuestion> getQuestions() {
        List<WorkFlowQuestion> questions = new ArrayList<>();
        questions.add(new WorkFlowQuestion("Got it...To continue this operation, please login", YES_NO));
        questions.add(new WorkFlowQuestion("Thank you!", TEXT));
        questions.add(new WorkFlowQuestion("What is your username?", TEXT));
        questions.add(new WorkFlowQuestion("What is your password?", TEXT));
        questions.add(new WorkFlowQuestion("What is the email of the user account you want to unlock?", TEXT));
        questions.add(new WorkFlowQuestion("Unlocking user account......", TEXT));
        questions.add(new WorkFlowQuestion("User account unlocked successfully!", TEXT));
        questions.add(new WorkFlowQuestion("Could not unlock user account.... Please try again!", TEXT));


        return questions;
    }
}
