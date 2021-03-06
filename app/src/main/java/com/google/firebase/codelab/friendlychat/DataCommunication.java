package com.google.firebase.codelab.friendlychat;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by CatLin on 2016/12/25.
 */

public interface DataCommunication {
    public double getTotalDistance();
    public void setTotalDistance(double distance);
    public boolean getStart();
    public void setStart(boolean start);
    public boolean getFirstLocate();
    public void setFirstLocate(boolean firstLocate);
    public UserProfile getUserProfile();
    public void setUserProfile(UserProfile userProfile);
    public DatabaseReference getDatabase();
}