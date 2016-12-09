package com.google.firebase.codelab.friendlychat;

import java.util.List;

/**
 * Created by JCLIN on 2016/12/8.
 */

public class UserSchedule {
    private String googleEmail;
    private List<WeekContent> plan;

    public UserSchedule(){}
    public UserSchedule(String googleEmail, List<WeekContent> plan) {
        this.googleEmail = googleEmail;
        this.plan = plan;
    }

    public String getGoogleEmail(){ return this.googleEmail; }
    public List<WeekContent> getPlan(){ return this.plan;}

    public void setGoogleEmail(String email) { this.googleEmail = email; }
    public void setPlan(List<WeekContent> plan) { this.plan = plan; }

}
