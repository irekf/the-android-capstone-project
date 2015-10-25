package com.acpcoursera.diabetesmanagment.model;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface DmServiceProxy {

    public final static String DM_SVC_SIGN_UP_PATH = "/signup";

    @POST(DM_SVC_SIGN_UP_PATH)
    public Call<String> signUp(@Body UserInfo info);

    @GET("/print/{text}")
    public Void printText(@Path("text") String text);

}
