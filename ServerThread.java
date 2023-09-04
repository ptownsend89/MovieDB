package com.company;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import static com.company.SQLConn.initialiseSQL;

public class ServerThread extends Thread{

    static Scanner sc = new Scanner(System.in);
    static String apiKey = "";
    private final Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run(){

        try {
            ConnMDB con = new ConnMDB();
            InputStream input = socket.getInputStream();

            try {
                SQLConn s = initialiseSQL();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(input)); //wrap byte array read into a character array read
            String inputStr; //read input from client into string via buffered reader
            OutputStream output = socket.getOutputStream(); //prep socket to send an output
            PrintWriter writer = new PrintWriter(output, true); //link writer to outputstream so we can send output to client. TRUE auto-flushes data from writer

            //may be best to receive a json string with for example when logging in:
            //1. operation
            //2. login name
            //3. password

            do {
                //input which requests an action from client
                inputStr = reader.readLine();
                System.out.println("Input string: " + inputStr);
                String operation = "";
                JSONObject jsonOb = new JSONObject();
                if ((inputStr !=null) && (!inputStr.equals(""))) {
                    if (inputStr.indexOf("{") == 0) {
                        jsonOb = new JSONObject(inputStr);
                        operation = jsonOb.get("operation").toString();
                    } else {
                        operation = inputStr;
                    }

                    switch (operation) {
                        case "login":
                            try {
                                String logname = jsonOb.get("logname").toString();
                                String pass = jsonOb.get("password").toString();
                                UserAuth login = new UserAuth(logname, pass);
                                String sessionToken = login.logIn(logname, pass, login.authKey());
                                System.out.println("session token is: " +sessionToken);
                                if (!sessionToken.isEmpty() && !sessionToken.equals("UNKNOWN")) {
                                    writer.println("loginOK");
                                } else {
                                    writer.println("Login error, please try again");
                                }
                            } catch (Exception e){
                                System.out.println("Exception: " + e.getMessage());
                            }
                        case "1":
                            ArrayList<String> watchList;
                            try {
                                String title;String movieJSON;
                                movieJSON = reader.readLine();
                                //con.sanitise(movieJSON);

                                JSONObject movieObj = new JSONObject(movieJSON);
                                title = movieObj.getString("movie");
                                watchList = con.searchMovieTitles(title);

                                JSONArray jArray = new JSONArray();
                                for (int i=0;i<watchList.size();i++){
                                    jArray.put(watchList.get(i));
                                    System.out.println(watchList.get(i));
                                }
                                writer.println(jArray); //pass array of movies to client to decide which to save
                                break;

                            } catch (SQLException e){
                                e.printStackTrace();
                            }
                        case "2":
                            try{
                                String optionJson;
                                optionJson = reader.readLine();
                                System.out.println("JSON for 2 choice:" + optionJson);
                                break;
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                    }
                }
            } while ((inputStr != null) && (!inputStr.equals("bye")));
            if (inputStr.equals("bye")){
                writer.println("bye"); // terminate connection both ends
            }
            System.out.println("ServerThread disconnected");
        } catch (IOException ex){
            System.out.println("I/O Exception: " + ex.getMessage());
        }
    }
}
