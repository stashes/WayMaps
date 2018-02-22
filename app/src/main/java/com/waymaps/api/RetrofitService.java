package com.waymaps.api;


import okhttp3.OkHttpClient;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


/**
 * Created by Admin on 27.01.2018.
 */

public class RetrofitService {



    private static Retrofit retrofit = null;

    private static Retrofit getClient(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();


        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(WayMapsService.url)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    private static WayMapsService WAY_MAPS_SERVICE = null;

    public static WayMapsService getWayMapsService() {
        if (WAY_MAPS_SERVICE==null){
            WAY_MAPS_SERVICE = getClient().create(WayMapsService.class);
        }
        return WAY_MAPS_SERVICE;
    }
}
