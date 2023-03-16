package com.company;

public class LoadDriver {
    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e){
            System.out.println("Exception "+e);
        }
    }
}