package bgu.spl.net.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedList;
import java.util.List;

public class User {
    private String userName;
    private String password;
    private LocalDate birthdayDate;
    boolean loggedIn = false;
    private int ID;
    List<User> usersBlockedMe = new LinkedList<>();


    public User(String userName, String password, String birthday) {
        this.userName = userName;
        this.password = password;
        String s = birthday.substring(6) + birthday.substring(2,6) + birthday.substring(0,2);
        birthdayDate = LocalDate.parse(s);
        ID=-1;
    }

    public void setLoggedIn(boolean loggedIn , int conId){
        this.loggedIn = loggedIn;
        if (!loggedIn)
            ID = -1;
        else
            ID=conId;
    }
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getBirthday() {
        return birthdayDate;
    }
    public int getAge(){
        int i = Period.between(birthdayDate, LocalDate.now()).getYears();
        return i;
    }
    public boolean equals(String userName, String password)
    {
        return this.userName == userName && this.password == password ;
    }
    public void addBlockUser(User blockUser)
    {
        usersBlockedMe.add(blockUser);
    }
    public boolean isBlockedMe(User user)
    {
        return usersBlockedMe.contains(user);
    }
    public boolean isLoggedIn()
    {
        return loggedIn;
    }
}
