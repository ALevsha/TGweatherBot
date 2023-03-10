package data.remote.api

import data.remote.models.CurrentWeather
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("current.json")
    fun getCurrentWeather(
        @Query("key") apiKey: String, // Api ключ
        @Query("q") countryName: String, // q - Query (запрос). Передается имя города
        @Query("aqi") airQualityData: String // данные, связанные с сервером (передается NO или YES, не нужно, поэтому NO)
    ): Deferred<CurrentWeather>
}