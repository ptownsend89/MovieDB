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

import static com.company.Main.apiKey;
import static com.company.Main.sc;

public class UserAuth {
    String user;

    public UserAuth(String user) {
        this.user = user;
    }
    public UserAuth (){
    }

    public String authKey(String user) throws SQLException {
        //returns JSON string containing auth key for user to authenticate first time login
        //implement USER passing
        String auth;
        String k = null;
        try {

            String authKey = "https://api.themoviedb.org/3/authentication/token/new?api_key=" + apiKey;
            URL url = new URL(authKey);
            //send for auth key:
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            urlCon.setConnectTimeout(5000);
            urlCon.setDoOutput(false);
            //read auth key:
            BufferedReader br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            StringBuilder str = new StringBuilder();
            while ((auth=br.readLine()) != null){
                str.append(auth);
            }
            System.out.println(str);

            String js = str.toString();
            JSONObject key = new JSONObject(js);
            k = key.getString("request_token");
            String exp = key.getString("expires_at");

            //write to authKey record table:
            SQLConn authWrite = new SQLConn();
            authWrite.authKeyWrite(user,exp,k);

            System.out.println("Auth key generated...");
            System.out.println("Now go to: https://www.themoviedb.org/authenticate/"+k+" to log in");
            System.out.println("Press any key to continue:");
            sc.nextLine();

        }catch(Exception e){
            SQLConn errConn = new SQLConn();
            errConn.errLog("user",e.toString(),"404");
        }
        return k;
    }

    public String logIn (String rqToken) throws SQLException{ // needs authkey parameter
        String finalSessionID;

        System.out.println("Enter Username:");
        String user = sc.nextLine();
        System.out.println("Enter Password:");
        String pwd = sc.nextLine();
        //build json with format:
        //"username:"
        //"password:"
        //"request_token:"
        //with json parameters
        String authenticate = "https://api.themoviedb.org/3/authentication/token/validate_with_login?api_key=" + apiKey;

        try{
            System.out.println("Auth key "+rqToken+" passed, login initiated...");
            String loginJSON = "{\"username\": \""+user+"\",\"password\": \""+pwd+"\",\"request_token\": "
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
            //write body to url:

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
                    finalSessionID = response;
                }
                System.out.println("Session_ID request response: "+response);
            }

        } catch(Exception e){
            SQLConn errConn = new SQLConn();
            //errConn.errLog("user",e.toString(),"404");
            e.printStackTrace();
        }
        if (finalSessionID = ""){
            finalSessionID = "UNKNOWN";
        }
        return finalSessionID;
    }
}


// session ID - constant when authorised or changes? ---> da3d05eaa48bf2792989701e441b1b4f9b20722e