package com.company;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;


public class Main {

    static Scanner sc = new Scanner(System.in);
    static String apiKey = "";

    public static void main(String[] args) throws SQLException {

        try (ServerSocket serverSocket = new ServerSocket(6868)){ 

            while (true){
                Socket socket = serverSocket.accept();
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
