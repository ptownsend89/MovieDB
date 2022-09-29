package com.company;

import org.json.JSONObject;
import java.sql.SQLException;
import java.util.*;

import static com.company.ConnMDB.testConn;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static String apiKey = "";

    public static void main(String[] args) throws SQLException {

        System.out.println("Welcome to Movie DB!");

        String sessionID = "";
        while (sessionID.equals("") || (sessionID.equals("UNKNOWN"))) {
            System.out.println("Enter Username:");
            String user = sc.nextLine();
            System.out.println("Enter Password");
            String pass = sc.nextLine();
            //log in and return session ID if successful:
            UserAuth auth = new UserAuth(user, pass);
            try {
                String authK = auth.authKey();
                sessionID = auth.logIn(user, pass, authK);
            } catch (Exception e) {
                SQLConn errLog = new SQLConn();
                errLog.errLog(user,e.toString(),"Login failure");
                System.out.println("Incorrect login details, try again.");
            }
        }


        //logIn will return "UNKNOWN" string if session ID not found:
        if (!Objects.equals(sessionID, "UNKNOWN") && !Objects.equals(sessionID, "")){
            ConnMDB con = new ConnMDB();
            testConn();
            System.out.println("Enter search movie: ");
            ArrayList<String> watchList = new ArrayList<>();
            watchList = con.parseJSON(con.conParams(sc.nextLine()));

            //Select a film to save:
            System.out.println("Select a film to save:");
            int listChoice = sc.nextInt();

            //create json object from selected film option
            JSONObject filmChoice = new JSONObject(watchList.get(listChoice -1));
            System.out.println("SELECTED: " +filmChoice.getString("overview"));

            //get film ID from json object key and call saveWatchList to post
            String choiceID = filmChoice.get("id").toString();
            con.saveWatchlist(sessionID,choiceID);
        }
    }

}
