package com.cass.special_methods;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.*;

public class TestClass {
    public static void main(String[] args) {
        String URL = "https://sms.arkesel.com/sms/api?action=check-balance&api_key=T2lVanFDcUdoR0VqYm1Zd3pyVGY& response=json";

        OkHttpClient httpClient = new OkHttpClient();

        //Create a request to get the data from the client 
        Request request = new Request.Builder().url(URL).build();

        //create a response using the 'httpClient'
        httpClient.newCall(request).enqueue(new Callback() {
            @Override 
            public void onFailure(Call call, IOException e) {
                System.out.println("Failed to fetch Data from server...");
            }

            @Override 
            public void onResponse(Call call, Response response) {
                try {
                    String responseBody = response.body().string();
                    Map<String, Object> dataMap = new ObjectMapper().readValue(responseBody, Map.class);
                    dataMap.entrySet().forEach(each -> {
                        System.out.println(each.getKey() + " : " + each.getValue());
                    });
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
}
