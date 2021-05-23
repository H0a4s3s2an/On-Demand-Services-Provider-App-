package com.example.sample.providerapp.Interface;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Hassan Javaid on 8/24/2018.
 */

public class RetrofitClient {
    public static Retrofit retrofit=null;
    public static Retrofit getClient(String baseURL)
    {
        if(retrofit==null)
        {
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
