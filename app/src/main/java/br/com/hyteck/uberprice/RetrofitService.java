package br.com.hyteck.uberprice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("api/get-prices/{destiny}/{origin}")
    Call<String> getPrice(@Path("destiny") String destiny,
                                  @Path("origin") String origin);

    @GET("api/get-prices/{origin}")
    Call<List<String>> getPrice(@Path("origin") String origin);

}
