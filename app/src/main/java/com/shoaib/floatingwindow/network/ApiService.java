package com.shoaib.floatingwindow.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiService {

    @GET()
    Call<String> getStringResponse(@Url String url);
}
