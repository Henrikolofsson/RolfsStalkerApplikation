package Entities;

public class User {
    private String userName;
    private String userID;
    private String group;

    public User(String userName){
        this.userName = userName;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    public void setGroup(String group){
        this.group = group;
    }

    public String getGroup(){
        return group;
    }

    public String getUserName(){
        return userName;
    }

    public String getUserID(){
        return userID;
    }

}
