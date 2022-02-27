package com.app.currency.network

import com.app.currency.network.helper.EndPoints
import com.app.currency.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Service {

    @GET(EndPoints.CONVERT_URL)
    suspend fun convertCurrency(
        @Query("api_key") access_key: String = EndPoints.API_KEY,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ) : Response<ApiResponse>

    @GET("${EndPoints.HISTORICAL_URL}/{date}")
    suspend fun currencyHistory(
        @Path("date") date : String,
        @Query("api_key") access_key: String = EndPoints.API_KEY,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ) : Response<ApiResponse>

}