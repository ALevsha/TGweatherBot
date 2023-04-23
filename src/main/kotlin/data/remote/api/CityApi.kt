package data.remote.api

import data.remote.models.nominatim.City
import retrofit2.http.GET
import retrofit2.http.Query

interface CityApi {

    @GET("search")
    suspend fun getCityCoord(
        // здесь указываются обязательные (и не очень) параметры запроса согласно документации
        @Query("city") city: String, // название города
        @Query("country") country: String,
        @Query("format") formatData: String // формат получения данных (gson)
    ): List<City>

    @GET("reverse")
    suspend fun getCityName(
        @Query("lat") lat: String,
        @Query("lon") lon: String
    ): City
}









