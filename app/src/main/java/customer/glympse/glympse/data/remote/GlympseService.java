package customer.glympse.glympse.data.remote;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import customer.glympse.glympse.data.model.ApiResponse;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

/**
 * Created by admin on 11/21/2016.
 */

public interface GlympseService {
    String ENDPOINT = "http://glimpse.onsisdev.info/api/";

    @POST("customersignup")
    @Multipart
    Call<ApiResponse> signUp(@PartMap Map<String, RequestBody> requestMap);

    @POST("customerlogin")
    @FormUrlEncoded
    Call<ApiResponse> login(@FieldMap Map<String, String> params);

    @POST("forgotpasswordprovider")
    @FormUrlEncoded
    Call<ApiResponse> forgotPassword(@FieldMap Map<String, String> params);

    @POST("logout")
    @FormUrlEncoded
    Call<ApiResponse> logout(@FieldMap Map<String, String> params);

    @POST("pages")
    @FormUrlEncoded
    Call<ApiResponse> getStaticPages(@FieldMap Map<String, String> params);

    @POST("getcustomerprofile")
    @FormUrlEncoded
    Call<ApiResponse> getProfile(@FieldMap Map<String, String> params);

    @POST("editcustomerprofile")
    @Multipart
    Call<ApiResponse> editProfile(@PartMap Map<String, RequestBody> params);

    @POST("changepassword")
    @FormUrlEncoded
    Call<ApiResponse> resetPassword(@FieldMap Map<String, String> params);

    @POST("getservicetype")
    @FormUrlEncoded
    Call<ApiResponse> resetGetAllServices(@FieldMap Map<String, String> params);

    @POST("getallonlinemechanic")
    @FormUrlEncoded
    Call<ApiResponse> getMechForService(@FieldMap Map<String, String> params);

    @POST("getnearbybyproviders")
    @FormUrlEncoded
    Call<ApiResponse> getNearbyProviders(@FieldMap Map<String, String> params);

    @POST("sendrequest")
    @FormUrlEncoded
    Call<ApiResponse> getSendRequest(@FieldMap Map<String, String> params);

    @POST("getrequest")
    @FormUrlEncoded
    Call<ApiResponse> getMechanicDetail(@FieldMap Map<String, String> requestMap);

    @POST("acceptoffer")
    @FormUrlEncoded
    Call<ApiResponse> acceptOffer(@FieldMap Map<String, String> requestMap);

    @POST("cancelrequest")
    @FormUrlEncoded
    Call<ApiResponse> cancelRequest(@FieldMap Map<String, String> requestMap);

    @POST("customerratemechanic")
    @FormUrlEncoded
    Call<ApiResponse> rateMechanic(@FieldMap Map<String, String> requestMap);

    @POST("getlatlong")
    @FormUrlEncoded
    Call<ApiResponse> getLatLng(@FieldMap Map<String, String> requestMap);

    @POST("customerapphistory")
    @FormUrlEncoded
    Call<ApiResponse> getHistory(@FieldMap Map<String, String> requestParams);

    /********
     * Factory class that sets up a new ribot services
     *******/
    class Factory {

        public static GlympseService makeFairRepairService(Context context) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .addInterceptor(new UnauthorisedInterceptor(context))
                    .addInterceptor(logging)
                    .build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GlympseService.ENDPOINT)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            return retrofit.create(GlympseService.class);
        }

    }
}
