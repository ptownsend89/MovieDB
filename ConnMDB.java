package com.company;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.company.Main.apiKey;
import static com.company.Main.sc;
import static java.lang.String.valueOf;

public class ConnMDB {
    private URL url; //needed to be passed or create in constructor?
    private String pwd;

    public ConnMDB (){
    }
    public static boolean testConn() throws SQLException {
        boolean connected = false;
        String respCode = null;
        try {
            URL url = new URL("https://api.themoviedb.org/3/configuration?api_key=7d36ba51a1f8556d39d8af08af87b75d");
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setDoOutput(true);
            urlc.setAllowUserInteraction(false);
            urlc.connect();
            respCode = valueOf(urlc.getResponseCode());
            if (respCode.equals("200")) {
                System.out.println("Connected to MDB");
                connected = true;
            }
            urlc.disconnect();
        } catch (Exception e) {
            SQLConn errCon = new SQLConn();
            System.out.println(respCode);
            errCon.errLog("user",e.toString(),respCode); //error logging - "user" placeholder for actual user to be passed
        }
        return connected;
    }

    public ArrayList<String> parseJSON(String urlStr) throws SQLException {
        //Retrieves full list of search results matching selection
        //Offers user selection of result to save to watch list
        //Selection chosen in main()

        String response = null;
        ArrayList<String> resultList = new ArrayList<>();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            response = urlc.getResponseMessage();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            String l;

            StringBuilder sb = new StringBuilder();
            while ((l = br.readLine()) != null) {
                sb.append(l);
                //System.out.println("Full JSON string: " + l);
            }
            String full = sb.toString();

            //build arrayList of full list of results returned in JSON:
            JSONObject jsnOb = new JSONObject(full);
            JSONArray jarray = jsnOb.getJSONArray("results");
            if (jarray !=null){
                for(int i=0;i<jarray.length();i++){
                    resultList.add(jarray.get(i).toString());
                }
            } else {
                System.out.println("No results returned");
            }

            for (int i=0;i<resultList.size();i++){
                String filmResult = resultList.get(i).toString();

                JSONObject filmID = new JSONObject(filmResult);
                String ID = filmID.get("id").toString();

                JSONObject filmOb = new JSONObject(filmResult);
                String s = filmOb.get("overview").toString();
                System.out.println((i+1) + ". "+s+" ID: "+ID);
            }
            br.close();

        } catch (Exception e) {
            SQLConn err = new SQLConn();
            err.errLog("user",e.toString(),response);
        }
        return resultList;
    }

    public void saveWatchlist(String sessionID, String filmID) throws SQLException{
        //ID string is movie ID user wants to save
        //filmID = "335984";
        try{
            //Retrieve account ID from session ID
            String retrieveAccount = "https://api.themoviedb.org/3/account?api_key=" + apiKey + "&session_id=" + sessionID;
            URL url = new URL(retrieveAccount);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String in;
            StringBuilder sb = new StringBuilder();
            while ((in = br.readLine()) != null){
                sb.append(in);
            }
            String account = sb.toString();
            JSONObject accountInfo = new JSONObject(account);
            String accountID = accountInfo.get("id").toString();
            String accountName = accountInfo.getString("username");
            //System.out.println("Account ID for "+accountName+ " is "+accountID);
            conn.disconnect();

            //POST ID to account start:
            String savePost = "https://api.themoviedb.org/3/account/" +accountID+ "/watchlist?api_key=" +apiKey;
            savePost = savePost + "&session_id=" + sessionID;
            URL saveURL = new URL(savePost);
            HttpsURLConnection saveConnection = (HttpsURLConnection) saveURL.openConnection();
            saveConnection.setRequestProperty("Content-Type","application/json; utf-8");
            saveConnection.setDoOutput(true); // implicitly sets request method to "POST"
            saveConnection.setRequestProperty("Accept", "application/json");

            String saveBody = "{\"media_type\": \"movie\", \"media_id\": " +filmID+ ", \"watchlist\": true }";
            try(OutputStream os = saveConnection.getOutputStream()) {
                byte[] input = saveBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                os.flush();
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("Output stream error");
            }

            StringBuilder response = new StringBuilder();
            try(InputStreamReader iReader = new InputStreamReader(saveConnection.getInputStream())) {
                BufferedReader bReader = new BufferedReader(iReader);
                String r;
                while ((r = bReader.readLine()) != null){
                    response.append(r.trim());
                }
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("Error setting up input stream");
            }

            JSONObject saveResponse = new JSONObject(response.toString());
            if (saveResponse.get("success").toString().equals("true")){
                System.out.println("Success saving film");
            }


        } catch (Exception e){
            SQLConn err = new SQLConn();
            err.errLog("USER",e.toString(),"RESPONSE?");
        }
    }

    public String conParams(String mName){
        //return http string for URL movie search set up
        String urlBase = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&language=en-US&query=";
        mName = mName.replace(" ","-");
        mName = mName.toLowerCase();
        String inf = "&page=1&include_adult=false";
        urlBase += mName += inf;
        return urlBase;
    }
}
