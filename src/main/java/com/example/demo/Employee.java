package com.example.demo;

public class Employee {

    public int id;
    public String firstName;
    public String lastName;
    public int number;

    public Employee(int id, String firstName, String lastName, int number) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastname() {
        return lastName;
    }

    public void setLastname(String lastName) {
        this.lastName = lastName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
