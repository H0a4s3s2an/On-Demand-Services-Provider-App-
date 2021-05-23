package com.example.sample.providerapp.Interface;

import com.example.sample.providerapp.Model.FCMResponse;
import com.example.sample.providerapp.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Hassan Javaid on 8/17/2018.
 */

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAJfEZ44s:APA91bE9auZnJ8EmPv5wcm88b3N3PgbOvNGZCutDNwilXayF9nOhogJQ5KszYK8XfoBK-TUbKgo8xRNS1Qfp1oTENJNnpdzV3ULEbXbE0E-DWEW86EXZYB6oZIGdVp5fGWbG1N8W53Cj"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
