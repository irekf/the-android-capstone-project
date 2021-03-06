package com.acpcoursera.diabetesmanagment.model;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface DmServiceProxy {

    public final static String DM_SVC_SIGN_UP_PATH = "/signup";

    @POST(DM_SVC_SIGN_UP_PATH)
    public Call<Void> signUp(@Body UserInfo info);

    @GET("/print/{text}")
    public Void printText(@Path("text") String text);

    @POST("oauth/token?scope=read+write&grant_type=password")
    public Call<AccessToken> login(@Header("Authorization") String authorization,
                                   @Query("username") String username,
                                   @Query("password") String password,
                                   @Query("client_id") String clientId,
                                   @Query("client_secret") String clientSecret);

    @POST("/logout")
    public Call<Void> logout();

    @POST("/connect")
    public Call<UserInfo> establishConnection(@Query("token") String token);

    @POST("/checkin")
    public Call<Void> checkIn(@Body CheckInData data);

    @GET("/checkin")
    public Call<List<CheckInData>> getCheckInData();

    @GET("/users")
    public Call<List<UserInfo>> getUserList(@Query("teen_only") boolean teenOnly);

    @GET("/followers")
    public Call<List<Follower>> getFollowers();

    @GET("/following")
    public Call<List<Following>> getFollowing();

    @POST("/follow")
    public Call<Void> follow(
            @Query("username_to_follow") String usernameToFollow,
            @Query("major_data") boolean majorData,
            @Query("minor_data") boolean minorData
    );

    @POST("/invite")
    public Call<Void> invite(
            @Query("username_to_invite") String usernameToInvite,
            @Query("major_data") boolean majorData,
            @Query("minor_data") boolean minorData
    );

    @POST("/accept")
    public Call<Void> accept(@Query("username_to_accept") String usernameToAccept,
                             @Query("is_invite") boolean isInvite,
                             @Query("major_data") boolean majorData,
                             @Query("minor_data") boolean minorData);

    @POST("/delete")
    public Call<Void> delete(@Query("username_to_delete") String usernameToDelete,
                             @Query("is_follower") boolean isFollower);

    @POST("/settings")
    public Call<Void> changeSettings(@Query("settings_username") String settingsUsername,
                                     @Query("major_data") boolean majorData,
                                     @Query("minor_data") boolean minorData);

}
