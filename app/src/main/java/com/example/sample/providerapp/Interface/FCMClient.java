package com.example.sample.providerapp.Interface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Hassan Javaid on 8/17/2018.
 */

public class FCMClient {
    private static Retrofit retrofit=null;
    public static Retrofit getClient(String baseURL)
    {
        if(retrofit==null)
        {
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory .create())
                    .build();
        }
        return retrofit;
    }
}
