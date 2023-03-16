package com.company;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;



public class Main {

    public static void main(String[] args) throws SQLException {

        try (ServerSocket serverSocket = new ServerSocket(6868)){ //serversocket param should be port if available as param from cmd line

            while (true){
                Socket socket = serverSocket.accept(); //opens socket to start listening for client requests
                ServerThread s = new ServerThread(socket);
                System.out.println("ServerThread ID: " + s.getId());
                s.start();
            }
        } catch (IOException io) {
            System.out.println("Server IO exception " + io.getMessage());
            io.printStackTrace();
        }
    }

}




//MOVIEDB API KEY: 7d36ba51a1f8556d39d8af08af87b75d
//example API request: https://api.themoviedb.org/3/movie/550?api_key=7d36ba51a1f8556d39d8af08af87b75d
//API read access token: eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3ZDM2YmE1MWExZjg1NTZkMzlkOGFmMDhhZjg3Yjc1ZCIsInN1YiI6IjYyMGVhMDgxMzk2ZTk3MDA2YTg2ZDBlNCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.-aIXz2czsUOYd7VX-jV4oLWS1XtK_DA6SVE6Vs2lzws
