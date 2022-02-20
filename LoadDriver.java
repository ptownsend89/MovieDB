package com.company;
import java.sql.DriverManager;
import java.sql.Connection;

public class LoadDriver {
    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex){
            System.out.println("Exception "+ex);
        }
    }
}