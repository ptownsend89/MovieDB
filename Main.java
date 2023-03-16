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

