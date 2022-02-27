import com.mysql.cj.xdevapi.JsonParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class APIConn {
    public static void main(String[] args) {
        testConn();
    }
    public static void testConn(){
        try {
            URL url = new URL("https://api.themoviedb.org/3/search/movie?api_key=7d36ba51a1f8556d39d8af08af87b75d&language=en-US&query=goldfinger&page=1&include_adult=false");
            String query = "output parameters";

            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setDoOutput(true);
            urlc.setAllowUserInteraction(false);
            urlc.connect();
            if (urlc.getResponseCode() == 200){
                System.out.println("Connected to API");
            } else {
                System.out.println("Connection issue, response code: " + urlc.getResponseCode());
            }


            BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            String l = null; //set string to null so can become br.readLine
            StringBuilder sb = new StringBuilder();
            while ((l = br.readLine()) != null) {
                sb.append(l);
            }
//            sb.append("]");
//            String sd = "[";
//            sb.insert(0,"[");
            br.close();
            String backslash = "\\\\";
            String s = sb.toString();
            String sbArray[] = s.split(backslash); //split JSON string appropriately...

            JSONArray ja = new JSONArray();
            JSONObject jo = new JSONObject();
            jo.put("results",s);

            for(int i=0;i<3;i++){
                ja.add(i,sbArray[i]);
                System.out.println(sbArray[i]);
                //loop through string array until end, add each split into JSON object?
            }




            //PrintWriter writes json string to a file named "JSONExample.json" stored under this project
//            PrintWriter pw = new PrintWriter("JSONExample.json");
//            pw.write(jo.toJSONString());
//
//            pw.flush();
//            pw.close();



        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
