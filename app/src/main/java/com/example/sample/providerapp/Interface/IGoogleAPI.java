package com.example.sample.providerapp.Interface;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Hassan Javaid on 8/24/2018.
 */

public interface IGoogleAPI {
    @GET
    Call<String> getPath(@Url String url);
}
