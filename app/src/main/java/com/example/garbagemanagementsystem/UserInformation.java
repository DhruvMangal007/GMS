package com.example.garbagemanagementsystem;

public class UserInformation {
    private String Name;
    private int Age;
    private String Gender;
    private String Email;
    private String Password;

    public UserInformation(){
        //public no-arg constructor
    }

    public UserInformation(String Age, String Email, String Gender, String Name,  String Password) {
        this.Age = Integer.getInteger(Age);
        this.Email = Email;
        this.Gender = Gender;
        this.Name = Name;
        this.Password = Password;
    }

    public String getName() {return Name;}
    public void setName(String name) {this.Name = name;}

    public String getGender() {return Gender;}
    public void setGender(String gender) {this.Gender = gender;}

    public String getEmail() {return Email;}
    public void setEmail(String Email) {this.Email = Email;}

    public int getAge() {return Age;}
    public void setAge(int Age) {this.Age = Age;}

    public String getPassword() {return Password;}
    public void setPassword(String Password) {this.Password = Password;}

    public String toString(){
        String result = getName() + ", " + getAge() + ", " + getGender() + ", " + getEmail() + ", " + getPassword();
        return result;
    }
}
