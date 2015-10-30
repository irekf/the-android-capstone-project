package com.acpcoursera.diabetesmanagment.model;

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

    @POST("/gcmtoken")
    public Call<Void> sendGcmToken(@Query("token") String token);

}
