package com.app.boardgame4521;

//class for representing a user
//use
public class User {
    //one instance of current user
    public static User currentUser = new User();

    //data
    public boolean isSet = false;
    public String name;
    public int level;
    public int join_year;
    public int join_month;
    public int join_day;
    public int start_playing_time;
}
