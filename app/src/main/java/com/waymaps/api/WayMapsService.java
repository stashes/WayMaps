package com.waymaps.api;


import com.waymaps.data.requestEntity.Credentials;
import com.waymaps.data.responseEntity.FinGet;
import com.waymaps.data.responseEntity.FirmList;
import com.waymaps.data.responseEntity.GetCurrent;
import com.waymaps.data.responseEntity.GetGroup;
import com.waymaps.data.responseEntity.GetParking;
import com.waymaps.data.responseEntity.GetTrack;
import com.waymaps.data.responseEntity.PointData;
import com.waymaps.data.responseEntity.Report;
import com.waymaps.data.responseEntity.Ticket;
import com.waymaps.data.responseEntity.TicketList;
import com.waymaps.data.responseEntity.TrackCount;
import com.waymaps.data.responseEntity.TrackerList;
import com.waymaps.data.responseEntity.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Admin on 27.01.2018.
 */

public interface WayMapsService {
    public static final String url = "http://mob.waymaps.com/wm_mob_data.php/";

    public static final String DEFAULT_FORMAT = "json";

    //@todo
    /*@POST("./")
    Call<User> loginProcedure(@Body Credentials credentials);*/

    @FormUrlEncoded
    @POST("./")
    Call<User[]> loginProcedure(@Field("action") String action, @Field("pass") String pass,
                                @Field("os") String os, @Field("login") String login,
                                @Field("identificator") String identificator, @Field("format") String format);

    @FormUrlEncoded
    @POST("./")
    Call<Void> logoutProcedure(@Field("action") String action,@Field("user_id") String userId,
                           @Field("identificator") String identificator);

    @FormUrlEncoded
    @POST("./")
    Call<Void> updateProcedure(@Field("action") String action, @Field("user_id") String user_id,
                               @Field("identificator") String identificator,@Field("os") String os);

    @FormUrlEncoded
    @POST("./")
    Call<FinGet[]> finGetProcedure(@Field("action") String action, @Field("name") String name,
                                      @Field("identificator") String identificator, @Field("user_id") String user_id,
                                      @Field("format") String format, @Field("params") String params);
    @FormUrlEncoded
    @POST("./")
    Call<TrackerList[]> trackerProcedure(@Field("action") String action, @Field("name") String name,
                                        @Field("identificator") String identificator, @Field("user_id") String user_id,
                                        @Field("format") String format, @Field("params") String params);

    @FormUrlEncoded
    @POST("./")
    Call<GetCurrent[]> getCurrentProcedure(@Field("action") String action, @Field("name") String name,
                                               @Field("identificator") String identificator, @Field("user_id") String user_id,
                                               @Field("format") String format, @Field("params") String params);


    /*@FormUrlEncoded
    @POST("./")
    <T>Call<T[]> defaultProcedure(@Field("action") String action, @Field("name") String name,
                                        @Field("identificator") String identificator, @Field("user_id") String user_id,
                                        @Field("format") String format,@Field("params") String params);*/

    @FormUrlEncoded
    @POST("./")
    Call<Void> sendMessage(@Field("action") String action, @Field("name") String name ,
                           @Field("identificator") String identificator ,
                           @Field("format") String format ,@Field("params") String params );

    @FormUrlEncoded
    @POST("./")
    Call<TicketList[]> tickerListProcedure(@Field("action") String action, @Field("name") String name,
                                         @Field("identificator") String identificator, @Field("user_id") String user_id,
                                         @Field("format") String format, @Field("params") String params);

    @FormUrlEncoded
    @POST("./")
    Call<Ticket[]> getTicket(@Field("action") String action, @Field("name") String name,
                             @Field("identificator") String identificator, @Field("user_id") String user_id,
                             @Field("format") String format, @Field("params") String params);

    @FormUrlEncoded
    @POST("./")
    Call<Void> addComment(@Field("action") String action, @Field("name") String name,
                             @Field("identificator") String identificator, @Field("user_id") String user_id,
                             @Field("format") String format, @Field("params") String params);

    @FormUrlEncoded
    @POST("./")
    Call<FirmList[]> getFirmList(@Field("action") String action, @Field("name") String name,
                               @Field("identificator") String identificator,
                               @Field("format") String format, @Field("params") String params);

    @FormUrlEncoded
    @POST("./")
    Call<TrackCount[]> getTrackCount(@Field("action") String action, @Field("name") String name,
                                   @Field("identificator") String identificator,
                                   @Field("format") String format, @Field("params") String params);

    @FormUrlEncoded
    @POST("./")
    Call<Report[]> getReport(@Field("action") String action, @Field("name") String name,
                                 @Field("identificator") String identificator,
                                 @Field("format") String format, @Field("params") String params);

    @FormUrlEncoded
    @POST("./")
    Call<GetTrack[]> getTrack(@Field("action") String action, @Field("name") String name,
                                     @Field("identificator") String identificator,
                                     @Field("format") String format, @Field("params") String params);

    @FormUrlEncoded
    @POST("./")
    Call<PointData[]> getPoint(@Field("action") String action, @Field("name") String name,
                               @Field("identificator") String identificator,
                               @Field("format") String format, @Field("params") String params);

    @FormUrlEncoded
    @POST("./")
    Call<GetParking[]> getParking(@Field("action") String action, @Field("name") String name,
                                @Field("identificator") String identificator,
                                @Field("format") String format, @Field("params") String params);

    @FormUrlEncoded
    @POST("./")
    Call<GetGroup[]> getGroup(@Field("action") String action, @Field("name") String name,
                              @Field("identificator") String identificator,
                              @Field("format") String format, @Field("params") String params);

    @FormUrlEncoded
    @POST("./")
    Call<Void> sendMailComment(@Field("action") String action,
                               @Field("comment_text") String comment_text,
                              @Field("firm_id") String firm_id,
                              @Field("firm_name") String firm_name,
                               @Field("user_name") String user_name,
                               @Field("device_name") String device_name);

    @FormUrlEncoded
    @POST("./")
    Call<Void> sendMailCreate(@Field("action") String action,
                               @Field("firm_id") String firm_id,
                               @Field("firm_name") String firm_name,
                               @Field("user_name") String user_name,
                               @Field("device_name") String device_name);


}
