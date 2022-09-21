package com.company;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

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
//            e.printStackTrace();
//        }
        UserAuth auth = new UserAuth("TESTUSER"); //change param if needed
        String authK = auth.authKey("TESTUSER");
        //boolean done = auth.logIn(authK); //logs in and returns bool if successful. Needs to return session ID instead...
        String sessionID = auth.logIn(authK);

        ConnMDB con = new ConnMDB();
            testConn();
            System.out.println("Enter search movie: ");
            List<String> watchList = new ArrayList<>();
            watchlist = con.parseJSON(con.conParams(sc.nextLine()));
            
            //Select a film to save:
            System.out.println("Select a film to save:");
            int selection = sc.nextInt();
            JSONObject filmChoice = new JSONObject(resultList.get(selection -1).toString());
            System.out.println("SELECTED: " +filmChoice.getString("overview"));
            String choiceID = filmChoice.get("id").toString(); //save ID

            con.saveWatchlist(choiceID);
    }

}
