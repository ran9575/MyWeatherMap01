package com.ajw1079.myweathermap01;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class Function {

    /*네트워크의 유무를 판단*/
    public static boolean isNetworkAvailable(Context context){
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static String executeGet(String targetURL){
        URL url;
        HttpURLConnection connection = null;
        try{
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("content-type","application/json; charset=utf-8");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(false);

            /*URL로 접속이 되었음을 확인하는 역할 담당*/
            InputStream is;
            int status = connection.getResponseCode();
            if(status != HttpURLConnection.HTTP_OK){
                is = connection.getErrorStream();
            }else{
                is = connection.getInputStream();
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null){
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        }catch (Exception e){
            return null;   //예외처리를 실시하지만 사용자에게는 URL 접속 여부에 대하여 알릴 필요는 없음
        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }
    }
    /*각 시간 정보를 받아오면서 현재 시간의 날씨를 표현하는 역할 담당*/
    public static String setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset){
                icon = "&#xf00d";
            }else{
                icon = "&#xf02e";
            }
        }else{
            switch(id){
                case 2 : icon = "&#xf01e";
                break;
                case 3 : icon = "&#xf01c";
                break;
                case 5 : icon = "&#xf019";
                break;
                case 6 : icon = "&#xf01b";
                break;
                case 7 : icon = "&#xf014";
                break;
                case 8 : icon = "&#xf013";
                break;
            }
        }
        return icon;
    }
}











