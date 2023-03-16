package com.company;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.company.Main.apiKey;
import static com.company.SQLConn.initialiseSQL;
import static java.lang.String.valueOf;

public class ConnMDB {
    private SQLConn errCon = null;


    public ConnMDB (){
        try {
            errCon = initialiseSQL();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static boolean testConn() throws SQLException {

        boolean connected = false;
        String respCode = null;

        try {
            URLFactory uf = new URLFactory("https://api.themoviedb.org/3/configuration?api_key=" + apiKey);
            HttpURLConnection openURL = uf.createURL();
            openURL.setDoOutput(true);
            openURL.setAllowUserInteraction(false);
            openURL.connect();
            respCode = valueOf(openURL.getResponseCode());
            if (respCode.equals("200")) {
                System.out.println("Connected to MDB");
                connected = true;
            }
            openURL.disconnect();
        } catch (Exception e) {
            System.out.println(respCode);
        }
        return connected;
    }

    public ArrayList<String> searchMovieTitles(String movieTitle) throws SQLException {
        //Retrieves full list of search results matching selection
        //Build URL string for movie search:
        String urlStr = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&language=en-US&query=";
        movieTitle = movieTitle.replace(" ","-");
        movieTitle = movieTitle.toLowerCase();
        String inf = "&page=1&include_adult=false";
        urlStr += movieTitle += inf;

        ArrayList<String> resultList = new ArrayList<>();
        try {
            URLFactory uf = new URLFactory(urlStr);
            HttpURLConnection openConnect = uf.createURL();
            BufferedReader br = new BufferedReader(new InputStreamReader(openConnect.getInputStream()));
            String l;

            StringBuilder sb = new StringBuilder();
            while ((l = br.readLine()) != null) {
                sb.append(l);
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
                System.out.println("No results found");
            }
            br.close();

        } catch (Exception e) {
            errCon.errLog("user",e.toString());
        }
        return resultList;
    }

    public void saveWatchlist(String sessionID, String filmID) throws SQLException{
        //ID string is movie ID user wants to save

        try{
            //Retrieve account ID from session ID
            String retrieveAccount = "https://api.themoviedb.org/3/account?api_key=" + apiKey + "&session_id=" + sessionID;
            URLFactory uf = new URLFactory(retrieveAccount);
            HttpURLConnection openURL = uf.createURL();

            BufferedReader br = new BufferedReader(new InputStreamReader(openURL.getInputStream()));
            String in;
            StringBuilder sb = new StringBuilder();
            while ((in = br.readLine()) != null){
                sb.append(in);
            }
            String account = sb.toString();
            JSONObject accountInfo = new JSONObject(account);
            String accountID = accountInfo.get("id").toString();
            openURL.disconnect();

            //POST ID to account start:
            String savePost = "https://api.themoviedb.org/3/account/" +accountID+ "/watchlist?api_key=" + apiKey;
            savePost = savePost + "&session_id=" + sessionID;
            URLFactory save = new URLFactory(savePost);
            HttpURLConnection saveConn = uf.createURL();
            saveConn.setRequestProperty("Content-Type","application/json; utf-8");
            saveConn.setDoOutput(true);
            saveConn.setRequestProperty("Accept", "application/json");

            JSONObject saveJson = new JSONObject();
            saveJson.put("media_type","movie");
            saveJson.put("media_id",filmID);
            saveJson.put("watchlist",true);
            try(OutputStream os = saveConn.getOutputStream()) {
                byte[] input = saveJson.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                os.flush();
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("Output stream error");
            }

            StringBuilder response = new StringBuilder();
            try(InputStreamReader iReader = new InputStreamReader(saveConn.getInputStream())) {
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
            errCon.errLog("USER",e.toString());
        }
    }

    public void deleteWatchlist (String sessionID, String filmShowID){

    }
}