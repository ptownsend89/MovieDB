package com.company;

public class Main {

    public static void main(String[] args) {
        try{
            String tst = "TESTUSER";
            SQLConn con = new SQLConn();
            //con.testConn(tst);
            System.out.println(con.testConn(tst));
        } catch (Exception e){
            e.printStackTrace(); //print stack trace is method from Exception class
        }
    }



}






//MOVIEDB API KEY: 7d36ba51a1f8556d39d8af08af87b75d
//example API request: https://api.themoviedb.org/3/movie/550?api_key=7d36ba51a1f8556d39d8af08af87b75d
//API read access token: eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3ZDM2YmE1MWExZjg1NTZkMzlkOGFmMDhhZjg3Yjc1ZCIsInN1YiI6IjYyMGVhMDgxMzk2ZTk3MDA2YTg2ZDBlNCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.-aIXz2czsUOYd7VX-jV4oLWS1XtK_DA6SVE6Vs2lzws
