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

        UserAuth auth = new UserAuth("TESTUSER"); //change param if needed
        String authK = auth.authKey("TESTUSER");

        //logs in and returns session ID if successful:
        String sessionID = auth.logIn(authK); 

        //logIn will return "UNKNOWN" string if session ID not found:
        if (sessionID != "UNKNOWN" && sessionID != ""){
            ConnMDB con = new ConnMDB();
            testConn();
            System.out.println("Enter search movie: ");
            List<String> watchList = new ArrayList<>();
            watchlist = con.parseJSON(con.conParams(sc.nextLine()));

            //Select a film to save:
            System.out.println("Select a film to save:");
            int listChoice = sc.nextInt();

            //create json object from selected film option
            JSONObject filmChoice = new JSONObject(resultList.get(listChoice -1).toString());
            System.out.println("SELECTED: " +filmChoice.getString("overview"));

            //get film ID from json object key and call saveWatchList to post
            String choiceID = filmChoice.get("id").toString(); //save ID
            con.saveWatchlist(sessionID,choiceID);
        }
    }

}
