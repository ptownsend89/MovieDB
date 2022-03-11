package com.company;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

import static com.company.ConnMDB.testConn;

public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws SQLException {
//        try{
//            String tst = "TESTUSER";
//            SQLConn con = new SQLConn();
//            //con.testConn(tst);
//            if (con.testConn(tst)){
//                System.out.println("Enter new Username:");
//                String inp = sc.nextLine();
//                System.out.println("Now enter password:");
//                String inpPW = sc.nextLine();
//                if (con.createUser(inp,inpPW)){
//                    System.out.println("Success! New user "+inp+" created.");
//                } else{
//                    System.out.println("New user creation failed.");
//                }
//            } else {
//                System.out.println("Connection to database failed.");
//            }
//        } catch (Exception e){
//            e.printStackTrace(); //print stack trace is method from Exception class
//        }
        ConnMDB con = new ConnMDB();
            testConn();
            System.out.println("Enter search movie: ");
            con.parseJSON(con.conParams(sc.nextLine()));
    }

}
