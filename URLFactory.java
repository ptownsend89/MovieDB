package com.company;


import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class URLFactory {
    private String urlStr;
    private HttpURLConnection httpOpen;

    public URLFactory(String url) {
        this.urlStr = url;
    }

    public HttpURLConnection createURL (){
        if (this.urlStr == null || this.urlStr.isEmpty()) {
            return null;
        } else {
            try {
                URL openURL = new URL(urlStr);
                httpOpen = (HttpURLConnection) openURL.openConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } return httpOpen;
    }
}
