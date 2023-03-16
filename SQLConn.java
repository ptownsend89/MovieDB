package com.company;

import java.sql.*;

public class SQLConn {

    private static SQLConn sConn = null;
    private int id;
    private Connection conn = null;


    private SQLConn() throws SQLException {
        String connectionUrl = "";
        connectionUrl = "jdbc:mysql://localhost:3306/movieDB";
        conn = DriverManager.getConnection(connectionUrl,"juser","movieUserPW123");
    }

    // Create singleton class so no more than 1 connection to db
    // - thread safe when synchronized as only one instance of initialiseSQL can run at a time
    // OK for small scale app but not larger
    public static synchronized SQLConn initialiseSQL() throws SQLException{
        if (sConn == null){
            sConn = new SQLConn();
        }
        return sConn;
    }

    public synchronized Connection getConnection(){
        return this.conn;
    }

    public void errLog (String user, String error) throws SQLException{
        if(testConn()){
            String solution = null;
            String insErr = "INSERT INTO moviedb.err_Log (user_name, excpt, err_solution) " +
                    "VALUES (?, ?, ?)";
            switch (error.toString()){
                case "SQLException":
                    solution = "Check SQL connection";
                    break;
                case "IOException":
                    solution = "Check streams";
                    break;
                case "FileNotFoundException":
                    solution = "Check file paths";
                    break;
            }
            PreparedStatement ps = conn.prepareStatement(insErr);
            ps.setString(1,user);
            ps.setString(2,error);
            ps.setString(3,solution);
            ps.executeUpdate();
            ps.close();
        }
    }

    public boolean testConn(){
        boolean testOK = false;
        try {
            String usr = "TESTUSER";
            String testID = "SELECT UserID FROM users WHERE username = ?";
            //testConn should expect "3" for test call
            PreparedStatement ps = conn.prepareStatement(testID);
            ps.setString(1, usr);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int result = rs.getInt("UserID");
            rs.close();
            ps.close();
            if (result == 3){
                testOK = true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return testOK;
    }


    public boolean verifyUser(String userName, String pword, String timeCreated) {

        String password = "";

        try {
            String sqlSelectUsers = "SELECT * FROM movieDB.users WHERE username = '"+userName+"'";
            PreparedStatement ps = conn.prepareStatement(sqlSelectUsers);
            ResultSet rs = ps.executeQuery();{
                while (rs.next()) {
                    id = rs.getInt("user_ID");
                    userName = rs.getString("username");
                    pword = rs.getString("pword");
                    timeCreated = rs.getString("created");
                }
                rs.close();
                ps.close();
            }
        } catch (Exception e){
            System.out.println("Connection exception occurred , check MySQL connection"+ e);
        }
        if (pword.equals(password)){
            return true;
        } else {
            return false;
        }
    }

    public boolean createUser(String username, String pword) throws SQLException {
        //Connect to API, create user credentials, store these in SQL table
        //Encrypt password?
        //Retrieve user credential from table before attempting to log in again to API using key
        boolean ok = false;
        try {
            if (testConn()){
                ConnMDB usrConn = new ConnMDB();
                //String createUsr = usrConn.authKey();
            }
            String insertUser = "INSERT INTO movieDB.users (username,pword) " +
                    "VALUES (?,?)";
            PreparedStatement ps = conn.prepareStatement(insertUser);
            //setString index refers to location of parametrised values in insert query
            ps.setString(1,username);
            ps.setString(2,pword);
            ps.executeUpdate();
            System.out.println("Number of records updated: "+ps.getUpdateCount());
            if(ps.getUpdateCount() > 0){
                ok = true;
            }
            ps.close();
        } catch (Exception e) {
            System.out.println("Insert error - exception: "+e+". Cannot log error to SQL.");
        }
        return ok;
    }

    public void authKeyWrite(String user,String expiresAt,String requestToken) throws SQLException{
        String authStr = "INSERT INTO moviedb.userAuth (username, expires_at, request_token) " +
                "VALUES (?, STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s UTC'), ?)";
        PreparedStatement ps = conn.prepareStatement(authStr);
        ps.setString(1,user);
        ps.setString(2,expiresAt);
        ps.setString(3,requestToken);
        ps.executeUpdate();
        ps.close();
    }
}