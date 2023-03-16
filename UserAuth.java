package com.company;

import org.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static com.company.ServerThread.apiKey;
import static com.company.ServerThread.sc;
import static com.company.SQLConn.initialiseSQL;


public class UserAuth {
    String user;
    String pass;
    private SQLConn dbConn;

    public UserAuth(String user, String pass) throws SQLException {
        this.user = user;
        this.pass = pass;
        dbConn = initialiseSQL();
    }

    public String authKey() throws SQLException {
        //returns JSON string containing auth key for user to authenticate first time login
        String auth;
        String k = null;
        try {

            String authKey = "https://api.themoviedb.org/3/authentication/token/new?api_key=" + apiKey;
            URL url = new URL(authKey);

            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            urlCon.setConnectTimeout(5000);
            urlCon.setDoOutput(false);

            BufferedReader br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            StringBuilder str = new StringBuilder();
            while ((auth=br.readLine()) != null){
                str.append(auth);
            }

            String js = str.toString();
            JSONObject key = new JSONObject(js);
            k = key.getString("request_token");
            String expiry = key.getString("expires_at");

            //write to authKey record table:
            dbConn.authKeyWrite(this.user,expiry,k);

            //add check to db to see if already authenticated or not
            System.out.println("Now go to: https://www.themoviedb.org/authenticate/"+k+" to log in");
            System.out.println("Press any key to continue:");
            sc.nextLine();

        }catch(Exception e){
            dbConn.errLog(this.user,e.toString());
        }
        return k;
    }

    public String logIn (String user, String pass, String rqToken) throws SQLException{ // needs authkey parameter
        String finalSessionID = "";

        //build json with format:
        //"username:"
        //"password:"
        //"request_token:"
        //with json parameters
        String authenticate = "https://api.themoviedb.org/3/authentication/token/validate_with_login?api_key=" + apiKey;

        try{
            //Can use JSONObject.put("username",this.user) to build JSON string instead of manually?
            //i.e.
            //JSONObject loginJSON = new JSONObject();
            //loginJSON.put("username",this.user);
            //loginJSON.put("password",this.pass);
            //loginJSON.put("request_token",rqToken);
            String loginJSON = "{\"username\": \""+this.user+"\",\"password\": \""+this.pass+"\",\"request_token\": "
                    + "\"" + rqToken + "\"}";

            URL url = new URL(authenticate);
            HttpsURLConnection urlc = (HttpsURLConnection) url.openConnection();
            urlc.setRequestProperty("Content-Type","application/json; utf-8");
            urlc.setRequestMethod("POST");
            urlc.setRequestProperty("Accept", "application/json");
            urlc.setDoOutput(true);
            try(OutputStream os = urlc.getOutputStream()) {
                byte[] input = loginJSON.getBytes(StandardCharsets.UTF_8);
                os.write(input,0,input.length);
                os.flush();
            }

            InputStreamReader is = new InputStreamReader(urlc.getInputStream());
            try(BufferedReader readResponse = new BufferedReader(is)){
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = readResponse.readLine()) != null){
                    response.append(responseLine.trim());
                }
                System.out.println("LOGIN CALL RESPONSE: " + response);
                JSONObject jObject = new JSONObject(response.toString());
                if (jObject.get("success").toString().equals("true")){
                    System.out.println("User logged in");
                }
            }
            urlc.disconnect();
            is.close();

            //Generate session ID
            String sessionID = "https://api.themoviedb.org/3/authentication/session/new?api_key=" + apiKey;
            String body = "{\"request_token\": \""+rqToken+"\"}";
            URL session2 = new URL(sessionID);
            HttpsURLConnection sessionURL = (HttpsURLConnection) session2.openConnection();
            sessionURL.setDoOutput(true);
            sessionURL.setRequestProperty("Content-Type","application/json; utf-8");
            sessionURL.setRequestProperty("Accept", "application/json");

            try(OutputStream os = sessionURL.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input,0,input.length);
                os.flush();
            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("Request body write failed, check body");
            }
            //read response:
            InputStreamReader sessionRead = new InputStreamReader(sessionURL.getInputStream());
            try(BufferedReader readResponse = new BufferedReader(sessionRead)) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = readResponse.readLine()) != null) {
                    response.append(responseLine.trim());
                    finalSessionID = response.toString();
                    JSONObject jobj = new JSONObject(finalSessionID);
                    finalSessionID = jobj.getString("session_id");
                }
            }

        } catch(Exception e){
            dbConn.errLog(user,"Error with RQ token: " + rqToken);
            e.printStackTrace();
        }
        if (finalSessionID.equals("")){
            finalSessionID = "UNKNOWN";
        }
        return finalSessionID;
    }
}

// session ID - constant when authorised or changes? ---> da3d05eaa48bf2792989701e441b1b4f9b20722e