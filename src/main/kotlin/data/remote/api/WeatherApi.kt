package data.remote.api

import data.remote.models.openweathermap.CurrentWeather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: String, // широта
        @Query("lon") lon: String, // долгота
        @Query("appid") apiKey: String // API key
    ): CurrentWeather
}